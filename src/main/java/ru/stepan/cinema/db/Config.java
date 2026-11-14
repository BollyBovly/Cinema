package ru.stepan.cinema.db;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.stepan.cinema.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class Config {


    @Bean
    public PasswordEncoder passwordEndcore(){
        return new BCryptPasswordEncoder();
    }
}