package fr.caensup.portfolio.controllers;

import fr.caensup.portfolio.dtos.UserDto;
import fr.caensup.portfolio.entities.User;
import fr.caensup.portfolio.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository UserRepository;

    @GetMapping("/register")
    public String registerForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public RedirectView submitRegister(
            @ModelAttribute UserDto userDto,
            HttpSession session,
            RedirectAttributes attrs
    ) {
        // Vérifier si l'utilisateur existe déjà
        Optional<User> existingUser = UserRepository.findByLogin(userDto.getLogin());
        if (existingUser.isPresent()) {
            attrs.addFlashAttribute("error", "Ce nom d'utilisateur est déjà utilisé.");
            return new RedirectView("/register");
        }

        // Créer le nouvel utilisateur
        User newUser = new User();
        userDto.toEntity(newUser);
        UserRepository.save(newUser);

        // Créer la session
        session.setAttribute("currentUser", newUser);

        attrs.addFlashAttribute("success", "Compte créé avec succès !");
        return new RedirectView("/users/" + newUser.getId() + "/portfolios");
    }

    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    @PostMapping("/login")
    public RedirectView submitLogin(
            @ModelAttribute("login") String login,
            @ModelAttribute("password") String password,
            HttpSession session,
            RedirectAttributes attrs
    ) {
        // Rechercher l'utilisateur
        Optional<User> optUser = UserRepository.findByLogin(login);

        if (optUser.isPresent()) {
            User user = optUser.get();
            // TODO: Vérifier le mot de passe (à implémenter avec BCrypt)
            if (user.getPassword().equals(password)) {
                // Créer la session
                session.setAttribute("currentUser", user);
                return new RedirectView("/users/" + user.getId() + "/portfolios");
            }
        }

        attrs.addFlashAttribute("error", "Identifiant ou mot de passe incorrect.");
        return new RedirectView("/login");
    }

    @GetMapping("/logout")
    public RedirectView logout(HttpSession session, RedirectAttributes attrs) {
        session.invalidate();
        attrs.addFlashAttribute("success", "Vous avez été déconnecté avec succès.");
        return new RedirectView("/");
    }
}