package fr.caensup.portfolio.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter @Setter
public class Profile {
    @Id
    private UUID id=UUID.randomUUID();

    @Column(length = 60)
    private String name;

    @ManyToOne
    private User owner;
}
