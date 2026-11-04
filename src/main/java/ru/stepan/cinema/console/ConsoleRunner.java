package ru.stepan.cinema.console;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.stepan.cinema.model.User;
import ru.stepan.cinema.service.UserService;

import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConsoleRunner implements CommandLineRunner {
    private final UserService userService;

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            printMenu();
            if (!scanner.hasNextLine()) {
                break;
            }
            String command = scanner.nextLine().trim();

            try {
                switch (command) {
                    case "1" -> createUser(scanner);
                    case "2" -> updateUserEmail(scanner);
                    case "3" -> deleteUser(scanner);
                    case "4" -> findUser(scanner);
                    case "exit" -> isRunning = false;
                    default -> System.out.println("Неизвестная команда");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
        System.out.println("Выход из программы");
    }

    private void createUser(Scanner scanner) {
        String name = ask(scanner, "Имя: ");
        String surname = ask(scanner, "Фамилия");
        String email = ask(scanner, "Email: ");
        String city = ask(scanner, "Город: ");
        String password = ask(scanner, "Пароль: ");

        User user = userService.createUser(name, surname, email, city, password);
        System.out.println("Пользователь создан"  + user);
    }

    private UUID askId(Scanner scanner) {
        String raw = ask(scanner, "id пользователя");
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Некорректный id" + raw);
        }
    }

    private String ask(Scanner scanner, String line) {
        System.out.println(line);
        return scanner.nextLine();
    }


    private void updateUserEmail(Scanner scanner) {
        UUID id = askId(scanner);
        String newEmail = ask(scanner, "Новый email: ");

        Optional<User> updated = userService.updateUserEmail(id, newEmail);
        if (updated.isPresent()) {
            System.out.println("Email обновлён: " + updated.get());
        } else {
            System.out.println("Пользователь с id " + id + " не найден");
        }
    }

    private void deleteUser(Scanner scanner) {
        UUID id = askId(scanner);

        if (userService.deleteUserById(id)) {
            System.out.println("Пользователь удалён");
        } else {
            System.out.println("Пользователь с id " + id + " не найден");
        }
    }

    private void findUser(Scanner scanner) {
        UUID id = askId(scanner);

        Optional<User> found = userService.getUserById(id);
        if (found.isPresent()) {
            System.out.println(found.get());
        } else {
            System.out.println("Пользователь с id " + id + " не найден");
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("Введите команду:" + "\n" +
                "1 - Создание пользователя" + "\n" +
                "2 - Изменить email у пользователя" + "\n" +
                "3 - Удаление пользователя" + "\n" +
                "4 - Найти пользователя по id" + "\n" +
                "exit - выход");
    }
}
