package ru.stepan.cinema.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.stepan.cinema.service.UserService;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String firstName, @RequestParam String surname,
                           @RequestParam String email, @RequestParam String city,
                           @RequestParam String password, Model model) {
        try {
            userService.createUser(firstName, surname, email, city, password);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("firstName", firstName);
            model.addAttribute("surname", surname);
            model.addAttribute("email", email);
            model.addAttribute("city", city);
            return "register";
        }
        return "redirect:/login?registered";
    }
}
