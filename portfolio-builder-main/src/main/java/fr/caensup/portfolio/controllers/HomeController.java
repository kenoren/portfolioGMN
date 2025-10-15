package fr.caensup.portfolio.controllers;


import lombok.Data;
import org.springframework.ui.Model;
import fr.caensup.portfolio.entities.*;
import fr.caensup.portfolio.repositories.PortfolioRepository;
import fr.caensup.portfolio.repositories.ProjectRepository;
import fr.caensup.portfolio.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Data
@AllArgsConstructor

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    public ModelAndView home() {
        // TODO: Récupérer l'utilisateur connecté depuis la session
        // Pour l'instant, on prend le premier utilisateur ou on crée une vue vide
        return new ModelAndView("home");
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard() {
        return new ModelAndView("/home/dashboard");
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, PortfolioRepository portfolioRepo) {
        model.addAttribute("portfolios", portfolioRepo.findAll());
        return "home/dashboard";
    }

    @GetMapping("/dashboard/add")
    public String showAddForm(Model model) {
        model.addAttribute("action", "/dashboard/add");
        return "home/dashboard.add-form";
    }

    @PostMapping("/dashboard/add")
    public String addPortfolio(
            @RequestParam Map<String, String> formData,
            PortfolioRepository portfolioRepo,
            UserRepository userRepo,
            ProjectRepository projectRepo
    ) {
        // 1️⃣ Créer les objets
        User user = new User();
        user.setEmail(formData.get("user.email"));
        userRepo.save(user);


        Project project = new Project();
        project.setTitle(formData.get("project.title"));
        project.setDescription(formData.get("project.description"));

        projectRepo.save(project);


        return "redirect:/dashboard";
    }

}