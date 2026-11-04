package ru.stepan.cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.stepan.cinema.model.User;
import ru.stepan.cinema.service.UserService;
import ru.stepan.cinema.service.UserServiceImpl;

@SpringBootApplication
public class CinemaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CinemaApplication.class, args);

    }
}
