package ru.stepan.cinema.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.stepan.cinema.dto.FilmResponse;
import ru.stepan.cinema.repository.FilmRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/pages")
@RequiredArgsConstructor
public class FilmPageController {
    private final FilmRepository filmRepository;

    @GetMapping("/films")
    public String films(Model model) {
        List<FilmResponse> films = StreamSupport
                .stream(filmRepository.findAll().spliterator(), false)
                .map(FilmResponse::from)
                .toList();
        model.addAttribute("films", films);
        return "films";
    }
}
