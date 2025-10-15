package fr.caensup.portfolio.controllers;

import fr.caensup.portfolio.entities.User;
import fr.caensup.portfolio.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String landing() {
        return "landing"; // Cherche landing.html dans templates/
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard() {
        return new ModelAndView("/home/dashboard");
    }
}