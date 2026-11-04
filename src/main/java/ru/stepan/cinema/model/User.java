package ru.stepan.cinema.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class User {
    private UUID id;
    private String name;
    private String surname;
    private String email;
    private String city;

    @ToString.Exclude
    private String password;
}