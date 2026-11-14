package ru.stepan.cinema.console;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AppConfig {
    @Value("${app.name}")
    private String name;
    @Value("${app.version}")
    private String version;
}
