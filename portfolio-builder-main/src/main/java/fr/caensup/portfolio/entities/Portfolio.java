package fr.caensup.portfolio.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Getter @Setter
public class Portfolio {
    @Id
    private UUID id = UUID.randomUUID();

    // Personnalisation du design
    private String primaryColor = "#2185d0";
    private String secondaryColor = "#21ba45";
    private String fontFamily = "Roboto";
    private String backgroundColor = "#ffffff";

    // Configuration des sections
    private boolean showAboutSection = true;
    private boolean showSkillsSection = true;
    private boolean showExperienceSection = true;
    private boolean showEducationSection = true;
    private boolean showContactSection = true;

    // Hero section
    private String heroTitle;
    private String heroSubtitle;
    private String heroImageUrl;

    // Statut de publication
    private boolean published = false;
    private String publicUrl; // URL unique pour le portfolio public

    @Column(length = 60)
    private String name;

    @Column(length = 50)
    private String themeName = "default"; // Valeur par défaut au niveau Java

    @ManyToOne
    private User owner;

    @OneToMany(mappedBy = "portfolio", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<Project> projects = new HashSet<>();
}