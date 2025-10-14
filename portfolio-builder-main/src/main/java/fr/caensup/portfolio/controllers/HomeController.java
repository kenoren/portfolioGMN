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
    public ModelAndView home() {
        // TODO: Récupérer l'utilisateur connecté depuis la session
        // Pour l'instant, on prend le premier utilisateur ou on crée une vue vide

        ModelAndView mv = new ModelAndView("/home/dashboard");

        // Exemple: récupérer le premier utilisateur (à remplacer par l'utilisateur connecté)
        // User currentUser = userRepository.findAll().stream().findFirst().orElse(null);
        // mv.addObject("currentUser", currentUser);

        return mv;
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard() {
        return new ModelAndView("/home/dashboard");
    }
}