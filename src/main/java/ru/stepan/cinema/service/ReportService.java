package ru.stepan.cinema.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import ru.stepan.cinema.entity.Film;
import ru.stepan.cinema.entity.Report;
import ru.stepan.cinema.enums.ReportStatus;
import ru.stepan.cinema.exception.ResourceNotFoundException;
import ru.stepan.cinema.repository.FilmRepository;
import ru.stepan.cinema.repository.ReportRepository;
import ru.stepan.cinema.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final ExecutorService reportExecutor;

    public Long createReport() {
        Report report = new Report();
        report.setStatus(ReportStatus.CREATED);
        return reportRepository.save(report).getId();
    }

    public Report getReport(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Отчет не найден: " + id));
    }

    public CompletableFuture<Void> generateReportAsync(Long reportId) {
        return CompletableFuture
                .runAsync(() -> generate(reportId), reportExecutor)
                .exceptionally(e -> {
                    log.error("Формирование отчета {} завершилось сбоем", reportId, e);
                    return null;
                });
    }

    private void generate(Long reportId) {
        long startTime = System.currentTimeMillis();
        TaskResult<Long> users = new TaskResult<>();
        TaskResult<List<Film>> films = new TaskResult<>();

        Thread usersThread = new Thread(() -> users.run(userRepository::count), "report-users");
        Thread filmsThread = new Thread(() -> films.run(this::loadFilms), "report-films");
        usersThread.start();
        filmsThread.start();

        try {
            usersThread.join();
            filmsThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            finish(reportId, ReportStatus.ERROR, "Формирование отчета прервано");
            return;
        }
        long totalMs = System.currentTimeMillis() - startTime;

        if (users.error != null || films.error != null) {
            log.error("Ошибка при формировании отчета {}: пользователи={}, фильмы={}",
                    reportId, users.error, films.error);
            finish(reportId, ReportStatus.ERROR, "Не удалось сформировать отчет");
            return;
        }
        finish(reportId, ReportStatus.COMPLETED, buildHtml(users, films, totalMs));
    }

    private List<Film> loadFilms() {
        List<Film> list = new ArrayList<>();
        filmRepository.findAll().forEach(list::add);
        return list;
    }

    private void finish(Long reportId, ReportStatus status, String content) {
        Report report = getReport(reportId);
        report.setStatus(status);
        report.setContent(content);
        reportRepository.save(report);
    }

    private String buildHtml(TaskResult<Long> users, TaskResult<List<Film>> films, long totalMs) {
        StringBuilder html = new StringBuilder("""
                <!DOCTYPE html>
                <html lang="ru">
                <head><meta charset="UTF-8"><title>Отчет</title></head>
                <body>
                <h1>Отчет о состоянии приложения</h1>
                """);
        html.append("<h2>Пользователи</h2>")
                .append("<p>Зарегистрировано: ").append(users.value).append("</p>")
                .append("<p>Время вычисления: ").append(users.elapsedMs).append(" мс</p>");

        html.append("<h2>Фильмы</h2>")
                .append("<table border=\"1\" cellpadding=\"6\"><tr>")
                .append("<th>Название</th><th>Длительность, мин</th><th>Дата выхода</th><th>Возраст</th></tr>");
        for (Film film : films.value) {
            html.append("<tr><td>").append(HtmlUtils.htmlEscape(film.getTitle())).append("</td><td>")
                    .append(film.getDurationMin()).append("</td><td>")
                    .append(film.getReleaseDate()).append("</td><td>")
                    .append(film.getAgeRating().getMinAge()).append("+</td></tr>");
        }
        html.append("</table>")
                .append("<p>Время получения списка: ").append(films.elapsedMs).append(" мс</p>")
                .append("<h2>Общее время формирования отчета: ").append(totalMs).append(" мс</h2>")
                .append("</body></html>");
        return html.toString();
    }

    private static final class TaskResult<T> {
        private T value;
        private long elapsedMs;
        private Exception error;

        void run(Supplier<T> task) {
            long start = System.currentTimeMillis();
            try {
                value = task.get();
            } catch (Exception e) {
                error = e;
            }
            elapsedMs = System.currentTimeMillis() - start;
        }
    }
}