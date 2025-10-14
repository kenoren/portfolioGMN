package fr.caensup.portfolio.controllers;

import fr.caensup.portfolio.dtos.PortfolioDto; // Changement
import fr.caensup.portfolio.dtos.UserDto;
import fr.caensup.portfolio.entities.Portfolio; // Changement
import fr.caensup.portfolio.entities.User;
import fr.caensup.portfolio.exceptions.UserNotFoundException;
import fr.caensup.portfolio.repositories.PortfolioRepository; // Changement
import fr.caensup.portfolio.repositories.UserRepository;
import fr.caensup.portfolio.ui.UiMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepository; // Changement

    @RequestMapping(value = "", method = {
            RequestMethod.GET,
            RequestMethod.POST
    })
    public ModelAndView index() {
        List<User> users = userRepository.findAllWithPortfolios(); // Changement
        return new ModelAndView("/users/index", "users", users);
    }

    @GetMapping("/{id}")
    public ModelAndView getOne(@PathVariable UUID id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isPresent()) {
            return new ModelAndView("/users/index", "user", opt.get());
        }
        return null;
    }

    @GetMapping("/add")
    public ModelAndView addForm() {
        ModelAndView mv = new ModelAndView("/users/userForm", "user", new UserDto());
        mv.addObject("action", "/users/add");
        return mv;
    }

    @PostMapping("/add")
    public RedirectView submitAdd(
            @ModelAttribute UserDto userDto,
            RedirectAttributes attrs
    ) {
        User newUser = new User();
        userDto.toEntity(newUser);
        userRepository.save(newUser);
        attrs.addFlashAttribute("message",
                new UiMessage("Ajout", "L'utilisateur " + newUser.getLogin() + " a été ajouté", "success", "info circle")
        );
        return new RedirectView("/users");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editForm(
            @PathVariable UUID id
    ) throws UserNotFoundException {
        Optional<User> optUser = userRepository.findById(id);
        if (optUser.isPresent()) {
            ModelAndView mv = new ModelAndView("/users/userForm", "user", optUser.get());
            mv.addObject("action", "/users/edit/" + id);
            return mv;
        }
        throw new UserNotFoundException("Utilisateur d'id " + id + " non trouvé !");
    }

    @PostMapping("/edit/{id}")
    public RedirectView submitEdit(
            @PathVariable UUID id,
            @ModelAttribute UserDto userDto,
            RedirectAttributes attrs
    ) throws UserNotFoundException {
        Optional<User> optUser = userRepository.findById(id);
        if (optUser.isPresent()) {
            User user = optUser.get();
            userDto.toEntity(user);
            userRepository.save(user);
            attrs.addFlashAttribute("message",
                    new UiMessage("Modification", "L'utilisateur " + user.getLogin() + " a été modifié", "success", "info circle")
            );
            return new RedirectView("/users");
        }
        throw new UserNotFoundException("Utilisateur d'id " + id + " non trouvé !");
    }

    @GetMapping("/delete/{id}")
    public RedirectView delete(
            @PathVariable UUID id,
            RedirectAttributes attrs
    ) throws UserNotFoundException {
        Optional<User> optUser = userRepository.findById(id);
        if (optUser.isPresent()) {
            User u = optUser.get();
            userRepository.delete(u);
            attrs.addFlashAttribute("message",
                    new UiMessage("Suppression", "L'utilisateur " + u.getLogin() + " a été supprimé", "success", "info circle")
            );
            return new RedirectView("/users");
        }
        throw new UserNotFoundException("Utilisateur d'id " + id + " non trouvé !");
    }

    @PostMapping("/search")
    public ModelAndView search(@RequestParam String searchText) {
        List<User> users = userRepository.search("%" + searchText.toLowerCase() + "%");
        return new ModelAndView("/users/index", "users", users);
    }

    @GetMapping("/{userId}/portfolios") // Changement
    public ModelAndView viewUserPortfolios(@PathVariable UUID userId) throws UserNotFoundException { // Changement
        Optional<User> optUser = userRepository.findByIdWithPortfolios(userId); // Changement
        if (optUser.isPresent()) {
            User user = optUser.get();
            ModelAndView mv = new ModelAndView("/users/viewPortfolios"); // Changement du template
            mv.addObject("user", user);
            mv.addObject("portfolioDto", new PortfolioDto()); // Changement
            return mv;
        }
        throw new UserNotFoundException("Utilisateur d'id " + userId + " non trouvé !");
    }

    @PostMapping("/{userId}/portfolios/add") // Changement
    public RedirectView addPortfolio( // Changement
                                      @PathVariable UUID userId,
                                      @ModelAttribute PortfolioDto portfolioDto, // Changement
                                      RedirectAttributes attrs
    ) throws UserNotFoundException {
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isPresent()) {
            User user = optUser.get();
            Portfolio newPortfolio = new Portfolio(); // Changement
            portfolioDto.toEntity(newPortfolio);
            newPortfolio.setOwner(user);
            portfolioRepository.save(newPortfolio); // Changement
            attrs.addFlashAttribute("message",
                    new UiMessage("Ajout de portfolio", "Le portfolio '" + newPortfolio.getName() + "' a été ajouté.", "success", "info circle") // Changement
            );
            return new RedirectView("/users/" + userId + "/portfolios"); // Changement
        }
        throw new UserNotFoundException("Utilisateur d'id " + userId + " non trouvé pour ajouter un portfolio !"); // Changement
    }

    @GetMapping("/{userId}/portfolios/edit/{portfolioId}") // Changement
    public ModelAndView editPortfolioForm( // Changement
                                           @PathVariable UUID userId,
                                           @PathVariable UUID portfolioId // Changement
    ) throws UserNotFoundException {
        Optional<Portfolio> optPortfolio = portfolioRepository.findById(portfolioId); // Changement
        if (optPortfolio.isPresent()) {
            Portfolio portfolio = optPortfolio.get(); // Changement
            if (!portfolio.getOwner().getId().equals(userId)) { // Changement
                throw new UserNotFoundException("Le portfolio d'id " + portfolioId + " n'appartient pas à l'utilisateur " + userId); // Changement
            }
            ModelAndView mv = new ModelAndView("/users/portfolios/portfolioForm"); // Changement du template
            mv.addObject("portfolio", portfolio); // Changement
            mv.addObject("portfolioDto", new PortfolioDto()); // Changement
            mv.addObject("userId", userId);
            mv.addObject("action", "/users/" + userId + "/portfolios/edit/" + portfolioId); // Changement
            return mv;
        }
        throw new UserNotFoundException("Portfolio d'id " + portfolioId + " non trouvé !"); // Changement
    }

    @PostMapping("/{userId}/portfolios/edit/{portfolioId}") // Changement
    public RedirectView submitEditPortfolio( // Changement
                                             @PathVariable UUID userId,
                                             @PathVariable UUID portfolioId, // Changement
                                             @ModelAttribute PortfolioDto portfolioDto, // Changement
                                             RedirectAttributes attrs
    ) throws UserNotFoundException {
        Optional<Portfolio> optPortfolio = portfolioRepository.findById(portfolioId); // Changement
        if (optPortfolio.isPresent()) {
            Portfolio portfolioToUpdate = optPortfolio.get(); // Changement
            if (!portfolioToUpdate.getOwner().getId().equals(userId)) { // Changement
                attrs.addFlashAttribute("message",
                        new UiMessage("Erreur", "Le portfolio spécifié n'appartient pas à cet utilisateur.", "error", "times circle") // Changement
                );
                return new RedirectView("/users/" + userId + "/portfolios"); // Changement
            }
            portfolioDto.toEntity(portfolioToUpdate);
            portfolioRepository.save(portfolioToUpdate); // Changement
            attrs.addFlashAttribute("message",
                    new UiMessage("Modification de portfolio", "Le portfolio '" + portfolioToUpdate.getName() + "' a été modifié.", "success", "info circle") // Changement
            );
            return new RedirectView("/users/" + userId + "/portfolios"); // Changement
        }
        throw new UserNotFoundException("Portfolio d'id " + portfolioId + " non trouvé !"); // Changement
    }

    @GetMapping("/{userId}/portfolios/delete/{portfolioId}") // Changement
    public RedirectView deletePortfolio( // Changement
                                         @PathVariable UUID userId,
                                         @PathVariable UUID portfolioId, // Changement
                                         RedirectAttributes attrs
    ) throws UserNotFoundException {
        Optional<Portfolio> optPortfolio = portfolioRepository.findById(portfolioId); // Changement
        if (optPortfolio.isPresent()) {
            Portfolio portfolioToDelete = optPortfolio.get(); // Changement
            if (!portfolioToDelete.getOwner().getId().equals(userId)) { // Changement
                attrs.addFlashAttribute("message",
                        new UiMessage("Erreur", "Le portfolio spécifié n'appartient pas à cet utilisateur.", "error", "times circle") // Changement
                );
                return new RedirectView("/users/" + userId + "/portfolios"); // Changement
            }
            portfolioRepository.delete(portfolioToDelete); // Changement
            attrs.addFlashAttribute("message",
                    new UiMessage("Suppression de portfolio", "Le portfolio '" + portfolioToDelete.getName() + "' a été supprimé.", "success", "info circle") // Changement
            );
            return new RedirectView("/users/" + userId + "/portfolios"); // Changement
        }
        throw new UserNotFoundException("Portfolio d'id " + portfolioId + " non trouvé !"); // Changement
    }
}