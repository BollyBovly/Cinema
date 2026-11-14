package ru.stepan.cinema.console;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.stepan.cinema.service.UserService;

import java.util.Scanner;
import java.util.UUID;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class ConsoleRunner implements CommandLineRunner {
    private final UserService userService;

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("""
                    1 - Создать пользователя
                    2 - Изменить email
                    3 - Удалить пользователя
                    4 - Найти по id
                    exit - выход""");

            if (!scanner.hasNextLine()) {
                break;
            }
            String command = scanner.nextLine().trim();
            if (command.equals("exit")) {
                break;
            }

            try {
                switch (command) {
                    case "1" -> System.out.println(userService.createUser(
                            ask(scanner, "Имя: "), ask(scanner, "Фамилия: "), ask(scanner, "Email: "),
                            ask(scanner, "Город: "), ask(scanner, "Пароль: ")));
                    case "2" -> System.out.println(userService
                            .updateUserEmail(askId(scanner), ask(scanner, "Новый email: "))
                            .map(Object::toString).orElse("Пользователь не найден"));
                    case "3" -> System.out.println(userService.deleteUserById(askId(scanner))
                            ? "Удалён" : "Пользователь не найден");
                    case "4" -> System.out.println(userService.getUserById(askId(scanner))
                            .map(Object::toString).orElse("Пользователь не найден"));
                    default -> System.out.println("Неизвестная команда");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
        System.out.println("Выход из программы");
    }

    private String ask(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private UUID askId(Scanner scanner) {
        String raw = ask(scanner, "id: ").trim();
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Некорректный id: " + raw);
        }
    }
}