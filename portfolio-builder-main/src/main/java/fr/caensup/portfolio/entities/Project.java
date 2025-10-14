package fr.caensup.portfolio.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Project {
    @Id
    private UUID id = UUID.randomUUID();

    @Column(length = 100, nullable = false)
    private String title;

    @Column(columnDefinition = "CLOB") // CHANGEMENT ICI : TEXT remplacé par CLOB
    private String description;

    @Column(length = 255)
    private String imageUrl;

    @Column(length = 255)
    private String projectUrl;

    private LocalDate completionDate;

    @ManyToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;
}