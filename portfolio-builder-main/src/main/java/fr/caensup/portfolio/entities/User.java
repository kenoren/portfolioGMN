package fr.caensup.portfolio.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "users") // C'est une bonne pratique de spécifier explicitement le nom de la table
public class User {

    private String bio;
    private String jobTitle;
    private String profileImageUrl;
    private String email;
    private String phone;
    private String linkedinUrl;
    private String githubUrl;
    private String location;

    @Id
    private UUID id = UUID.randomUUID();

    @Column(length = 20, unique = true, nullable = false) // Ajout de unique et nullable pour login
    private String login;

    @Column(length = 255, nullable = false) // Ajoutez le champ password ici. La longueur 255 est standard pour les mots de passe hachés.
    private String password;

    @Column(length = 20)
    private String firstName;

    @Column(length = 35)
    private String lastName;

    @OneToMany(mappedBy = "owner", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    private List<Portfolio> portfolios = new ArrayList<>();

    // Ajoutez un constructeur par défaut si nécessaire, Lombok ne le génère pas toujours si d'autres constructeurs sont présents
    public User() {}
}