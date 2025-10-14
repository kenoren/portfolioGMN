package fr.caensup.portfolio.controllers;

import fr.caensup.portfolio.dtos.ProjectDto;
import fr.caensup.portfolio.entities.Portfolio;
import fr.caensup.portfolio.entities.Project;
import fr.caensup.portfolio.exceptions.PortfolioNotFoundException; // Nous devrons créer cette exception
import fr.caensup.portfolio.exceptions.ProjectNotFoundException; // Nous devrons créer cette exception
import fr.caensup.portfolio.repositories.PortfolioRepository;
import fr.caensup.portfolio.repositories.ProjectRepository;
import fr.caensup.portfolio.ui.UiMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/users/{userId}/portfolios/{portfolioId}/projects")
public class ProjectController {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private ProjectRepository projectRepository;

    // ----- Méthode pour afficher la liste des projets d'un portfolio (optionnel, on peut le faire dans viewPortfolios) -----
    @GetMapping("")
    public ModelAndView listProjects(
            @PathVariable UUID userId,
            @PathVariable UUID portfolioId
    ) throws PortfolioNotFoundException {
        Optional<Portfolio> optPortfolio = portfolioRepository.findByIdWithProjects(portfolioId);
        if (optPortfolio.isPresent()) {
            Portfolio portfolio = optPortfolio.get();
            // Vérifier que le portfolio appartient bien à l'utilisateur
            if (!portfolio.getOwner().getId().equals(userId)) {
                throw new PortfolioNotFoundException("Le portfolio d'id " + portfolioId + " n'appartient pas à l'utilisateur " + userId);
            }
            ModelAndView mv = new ModelAndView("/users/portfolios/projects/index"); // Nouvelle vue
            mv.addObject("portfolio", portfolio);
            mv.addObject("userId", userId);
            return mv;
        }
        throw new PortfolioNotFoundException("Portfolio d'id " + portfolioId + " non trouvé !");
    }

    // ----- Méthode pour afficher le formulaire d'ajout de projet -----
    @GetMapping("/add")
    public ModelAndView addProjectForm(
            @PathVariable UUID userId,
            @PathVariable UUID portfolioId
    ) throws PortfolioNotFoundException {
        Optional<Portfolio> optPortfolio = portfolioRepository.findById(portfolioId);
        if (optPortfolio.isPresent()) {
            Portfolio portfolio = optPortfolio.get();
            if (!portfolio.getOwner().getId().equals(userId)) {
                throw new PortfolioNotFoundException("Le portfolio d'id " + portfolioId + " n'appartient pas à l'utilisateur " + userId);
            }
            ModelAndView mv = new ModelAndView("/users/portfolios/projects/projectForm"); // Nouvelle vue
            mv.addObject("projectDto", new ProjectDto());
            mv.addObject("userId", userId);
            mv.addObject("portfolioId", portfolioId);
            mv.addObject("action", "/users/" + userId + "/portfolios/" + portfolioId + "/projects/add");
            return mv;
        }
        throw new PortfolioNotFoundException("Portfolio d'id " + portfolioId + " non trouvé !");
    }

    // ----- Méthode pour soumettre l'ajout de projet -----
    @PostMapping("/add")
    public RedirectView submitAddProject(
            @PathVariable UUID userId,
            @PathVariable UUID portfolioId,
            @ModelAttribute ProjectDto projectDto,
            RedirectAttributes attrs
    ) throws PortfolioNotFoundException {
        Optional<Portfolio> optPortfolio = portfolioRepository.findById(portfolioId);
        if (optPortfolio.isPresent()) {
            Portfolio portfolio = optPortfolio.get();
            if (!portfolio.getOwner().getId().equals(userId)) {
                throw new PortfolioNotFoundException("Le portfolio d'id " + portfolioId + " n'appartient pas à l'utilisateur " + userId);
            }
            Project newProject = new Project();
            projectDto.toEntity(newProject);
            newProject.setPortfolio(portfolio); // Associer le projet au portfolio
            projectRepository.save(newProject);
            attrs.addFlashAttribute("message",
                    new UiMessage("Ajout de projet", "Le projet '" + newProject.getTitle() + "' a été ajouté au portfolio '" + portfolio.getName() + "'.", "success", "info circle")
            );
            return new RedirectView("/users/" + userId + "/portfolios/" + portfolioId); // Rediriger vers la vue du portfolio ou des projets
        }
        throw new PortfolioNotFoundException("Portfolio d'id " + portfolioId + " non trouvé pour ajouter un projet !");
    }

    // ----- Méthode pour afficher le formulaire d'édition de projet -----
    @GetMapping("/edit/{projectId}")
    public ModelAndView editProjectForm(
            @PathVariable UUID userId,
            @PathVariable UUID portfolioId,
            @PathVariable UUID projectId
    ) throws ProjectNotFoundException, PortfolioNotFoundException {
        Optional<Project> optProject = projectRepository.findById(projectId);
        if (optProject.isPresent()) {
            Project project = optProject.get();
            // Vérifier que le projet appartient bien au portfolio et que le portfolio appartient à l'utilisateur
            if (!project.getPortfolio().getId().equals(portfolioId) || !project.getPortfolio().getOwner().getId().equals(userId)) {
                throw new ProjectNotFoundException("Le projet d'id " + projectId + " n'appartient pas au portfolio ou à l'utilisateur spécifié.");
            }
            ModelAndView mv = new ModelAndView("/users/portfolios/projects/projectForm"); // Vue pour le formulaire
            mv.addObject("project", project); // L'entité complète pour pré-remplir
            mv.addObject("projectDto", new ProjectDto()); // DTO vide pour le formulaire
            mv.addObject("userId", userId);
            mv.addObject("portfolioId", portfolioId);
            mv.addObject("action", "/users/" + userId + "/portfolios/" + portfolioId + "/projects/edit/" + projectId);
            return mv;
        }
        throw new ProjectNotFoundException("Projet d'id " + projectId + " non trouvé !");
    }

    // ----- Méthode pour soumettre l'édition de projet -----
    @PostMapping("/edit/{projectId}")
    public RedirectView submitEditProject(
            @PathVariable UUID userId,
            @PathVariable UUID portfolioId,
            @PathVariable UUID projectId,
            @ModelAttribute ProjectDto projectDto,
            RedirectAttributes attrs
    ) throws ProjectNotFoundException, PortfolioNotFoundException {
        Optional<Project> optProject = projectRepository.findById(projectId);
        if (optProject.isPresent()) {
            Project projectToUpdate = optProject.get();
            if (!projectToUpdate.getPortfolio().getId().equals(portfolioId) || !projectToUpdate.getPortfolio().getOwner().getId().equals(userId)) {
                attrs.addFlashAttribute("message",
                        new UiMessage("Erreur", "Le projet spécifié n'appartient pas à ce portfolio ou cet utilisateur.", "error", "times circle")
                );
                return new RedirectView("/users/" + userId + "/portfolios/" + portfolioId);
            }
            projectDto.toEntity(projectToUpdate);
            projectRepository.save(projectToUpdate);
            attrs.addFlashAttribute("message",
                    new UiMessage("Modification de projet", "Le projet '" + projectToUpdate.getTitle() + "' a été modifié.", "success", "info circle")
            );
            return new RedirectView("/users/" + userId + "/portfolios/" + portfolioId);
        }
        throw new ProjectNotFoundException("Projet d'id " + projectId + " non trouvé !");
    }

    // ----- Méthode pour supprimer un projet -----
    @GetMapping("/delete/{projectId}")
    public RedirectView deleteProject(
            @PathVariable UUID userId,
            @PathVariable UUID portfolioId,
            @PathVariable UUID projectId,
            RedirectAttributes attrs
    ) throws ProjectNotFoundException, PortfolioNotFoundException {
        Optional<Project> optProject = projectRepository.findById(projectId);
        if (optProject.isPresent()) {
            Project projectToDelete = optProject.get();
            if (!projectToDelete.getPortfolio().getId().equals(portfolioId) || !projectToDelete.getPortfolio().getOwner().getId().equals(userId)) {
                attrs.addFlashAttribute("message",
                        new UiMessage("Erreur", "Le projet spécifié n'appartient pas à ce portfolio ou cet utilisateur.", "error", "times circle")
                );
                return new RedirectView("/users/" + userId + "/portfolios/" + portfolioId);
            }
            String projectName = projectToDelete.getTitle(); // Pour le message de succès
            projectRepository.delete(projectToDelete);
            attrs.addFlashAttribute("message",
                    new UiMessage("Suppression de projet", "Le projet '" + projectName + "' a été supprimé.", "success", "info circle")
            );
            return new RedirectView("/users/" + userId + "/portfolios/" + portfolioId);
        }
        throw new ProjectNotFoundException("Projet d'id " + projectId + " non trouvé !");
    }
}