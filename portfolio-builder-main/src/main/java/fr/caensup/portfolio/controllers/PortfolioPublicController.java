package fr.caensup.portfolio.controllers;

import fr.caensup.portfolio.entities.Portfolio;
import fr.caensup.portfolio.exceptions.PortfolioNotFoundException;
import fr.caensup.portfolio.repositories.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;
import java.util.UUID;

@Controller
public class PortfolioPublicController {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @GetMapping("/view/{portfolioId}") // URL publique pour un portfolio
    public ModelAndView viewPublicPortfolio(@PathVariable UUID portfolioId) throws PortfolioNotFoundException {
        Optional<Portfolio> optPortfolio = portfolioRepository.findByIdWithProjects(portfolioId); // Récupère le portfolio avec ses projets
        if (optPortfolio.isPresent()) {
            Portfolio portfolio = optPortfolio.get();
            String theme = portfolio.getThemeName() != null ? portfolio.getThemeName() : "default"; // Utilise le thème choisi, ou "default"

            // Renvoie vers le template spécifique au thème
            // Par exemple: "/public/themes/default/portfolioView" ou "/public/themes/modern/portfolioView"
            ModelAndView mv = new ModelAndView("/public/themes/" + theme + "/portfolioView");
            mv.addObject("portfolio", portfolio);
            return mv;
        }
        throw new PortfolioNotFoundException("Le portfolio demandé n'existe pas ou n'est pas public.");
    }

    // Tu peux ajouter d'autres mappings ici si ton portfolio public a plusieurs pages
    // Par exemple: /view/{portfolioId}/about, /view/{portfolioId}/contact
}