package fr.caensup.portfolio.repositories;

import fr.caensup.portfolio.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Requête corrigée pour s'assurer que "portfolios" est bien le nom de la collection dans l'entité User
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.portfolios") // Re-vérifiée, cette requête est standard et devrait fonctionner
    List<User> findAllWithPortfolios();

    User findByLogin(String login);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.portfolios p LEFT JOIN FETCH p.projects WHERE u.id = :id")
    Optional<User> findByIdWithPortfolios(@Param("id") UUID id);

    @Query("""
            SELECT DISTINCT u from User u LEFT JOIN FETCH u.portfolios p LEFT JOIN FETCH p.projects pr
            where lower(u.login) like :search
            or lower(u.firstName) like :search
            or lower(u.lastName) like :search
            or lower(p.name) like :search
            or lower(pr.title) like :search
    """)
    List<User> search(@Param("search") String search);
}