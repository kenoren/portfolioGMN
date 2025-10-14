package fr.caensup.portfolio.repositories;

import fr.caensup.portfolio.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByPortfolioId(UUID portfolioId);
}