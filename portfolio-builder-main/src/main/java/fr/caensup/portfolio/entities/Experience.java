package fr.caensup.portfolio.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Experience {
    @Id
    private UUID id = UUID.randomUUID();
    private String company;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    @ManyToOne
    private Portfolio portfolio;
}