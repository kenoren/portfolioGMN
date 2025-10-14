package fr.caensup.portfolio.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Education {
    @Id
    private UUID id = UUID.randomUUID();
    private String school;
    private String degree;
    private String field;
    private LocalDate graduationDate;
    private String description;

    @ManyToOne
    private Portfolio portfolio;
}