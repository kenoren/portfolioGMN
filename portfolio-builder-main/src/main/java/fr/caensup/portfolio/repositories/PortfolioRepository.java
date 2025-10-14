package fr.caensup.portfolio.repositories;

import fr.caensup.portfolio.entities.Portfolio; // Importer la nouvelle entité
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {
    @Query("SELECT p FROM Portfolio p LEFT JOIN FETCH p.projects WHERE p.id = :id")
    Optional<Portfolio> findByIdWithProjects(@Param("id") UUID id);
}