package fr.caensup.portfolio.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.util.UUID;

@Entity
public class Skill {
    @Id
    private UUID id = UUID.randomUUID();
    private String name;
    private Integer level; // 0-100
    private String category; // "Frontend", "Backend", etc.

    @ManyToOne
    private Portfolio portfolio;
}
