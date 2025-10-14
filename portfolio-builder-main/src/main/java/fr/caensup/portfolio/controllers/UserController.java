package fr.caensup.portfolio.controllers;

import fr.caensup.portfolio.dtos.ProfileDto;
import fr.caensup.portfolio.dtos.UserDto;
import fr.caensup.portfolio.entities.Profile;
import fr.caensup.portfolio.entities.User;
import fr.caensup.portfolio.exceptions.UserNotFoundException;
import fr.caensup.portfolio.repositories.ProfileRepository;
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
    private ProfileRepository profileRepository;

    @RequestMapping(value = "", method = {
            RequestMethod.GET,
            RequestMethod.POST
    })
    public ModelAndView index() {
        List<User> users = userRepository.findAllWithProfiles();
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

    @GetMapping("/{userId}/profiles")
    public ModelAndView viewUserProfiles(@PathVariable UUID userId) throws UserNotFoundException {
        Optional<User> optUser = userRepository.findByIdWithProfiles(userId);
        if (optUser.isPresent()) {
            User user = optUser.get();
            ModelAndView mv = new ModelAndView("/users/viewProfiles");
            mv.addObject("user", user);
            mv.addObject("profileDto", new ProfileDto());
            return mv;
        }
        throw new UserNotFoundException("Utilisateur d'id " + userId + " non trouvé !");
    }

    @PostMapping("/{userId}/profiles/add")
    public RedirectView addProfile(
            @PathVariable UUID userId,
            @ModelAttribute ProfileDto profileDto,
            RedirectAttributes attrs
    ) throws UserNotFoundException {
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isPresent()) {
            User user = optUser.get();
            Profile newProfile = new Profile();
            profileDto.toEntity(newProfile);
            newProfile.setOwner(user);
            profileRepository.save(newProfile);
            attrs.addFlashAttribute("message",
                    new UiMessage("Ajout de profil", "Le profil '" + newProfile.getName() + "' a été ajouté.", "success", "info circle")
            );
            return new RedirectView("/users/" + userId + "/profiles");
        }
        throw new UserNotFoundException("Utilisateur d'id " + userId + " non trouvé pour ajouter un profil !");
    }

    @GetMapping("/{userId}/profiles/edit/{profileId}")
    public ModelAndView editProfileForm(
            @PathVariable UUID userId,
            @PathVariable UUID profileId
    ) throws UserNotFoundException {
        Optional<Profile> optProfile = profileRepository.findById(profileId);
        if (optProfile.isPresent()) {
            Profile profile = optProfile.get();
            if (!profile.getOwner().getId().equals(userId)) {
                throw new UserNotFoundException("Le profil d'id " + profileId + " n'appartient pas à l'utilisateur " + userId);
            }
            ModelAndView mv = new ModelAndView("/users/profiles/profileForm");
            mv.addObject("profile", profile);
            mv.addObject("profileDto", new ProfileDto());
            mv.addObject("userId", userId);
            mv.addObject("action", "/users/" + userId + "/profiles/edit/" + profileId);
            return mv;
        }
        throw new UserNotFoundException("Profil d'id " + profileId + " non trouvé !");
    }

    @PostMapping("/{userId}/profiles/edit/{profileId}")
    public RedirectView submitEditProfile(
            @PathVariable UUID userId,
            @PathVariable UUID profileId,
            @ModelAttribute ProfileDto profileDto,
            RedirectAttributes attrs
    ) throws UserNotFoundException {
        Optional<Profile> optProfile = profileRepository.findById(profileId);
        if (optProfile.isPresent()) {
            Profile profileToUpdate = optProfile.get();
            if (!profileToUpdate.getOwner().getId().equals(userId)) {
                attrs.addFlashAttribute("message",
                        new UiMessage("Erreur", "Le profil spécifié n'appartient pas à cet utilisateur.", "error", "times circle")
                );
                return new RedirectView("/users/" + userId + "/profiles");
            }
            profileDto.toEntity(profileToUpdate);
            profileRepository.save(profileToUpdate);
            attrs.addFlashAttribute("message",
                    new UiMessage("Modification de profil", "Le profil '" + profileToUpdate.getName() + "' a été modifié.", "success", "info circle")
            );
            return new RedirectView("/users/" + userId + "/profiles");
        }
        throw new UserNotFoundException("Profil d'id " + profileId + " non trouvé !");
    }

    @GetMapping("/{userId}/profiles/delete/{profileId}")
    public RedirectView deleteProfile(
            @PathVariable UUID userId,
            @PathVariable UUID profileId,
            RedirectAttributes attrs
    ) throws UserNotFoundException {
        Optional<Profile> optProfile = profileRepository.findById(profileId);
        if (optProfile.isPresent()) {
            Profile profileToDelete = optProfile.get();
            if (!profileToDelete.getOwner().getId().equals(userId)) {
                attrs.addFlashAttribute("message",
                        new UiMessage("Erreur", "Le profil spécifié n'appartient pas à cet utilisateur.", "error", "times circle")
                );
                return new RedirectView("/users/" + userId + "/profiles");
            }
            profileRepository.delete(profileToDelete);
            attrs.addFlashAttribute("message",
                    new UiMessage("Suppression de profil", "Le profil '" + profileToDelete.getName() + "' a été supprimé.", "success", "info circle")
            );
            return new RedirectView("/users/" + userId + "/profiles");
        }
        throw new UserNotFoundException("Profil d'id " + profileId + " non trouvé !");
    }
}
