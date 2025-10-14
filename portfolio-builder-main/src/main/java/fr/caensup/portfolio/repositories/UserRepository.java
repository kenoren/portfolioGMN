// src/main/java/fr/caensup/portfolio/repositories/UserRepository.java
package fr.caensup.portfolio.repositories;

import fr.caensup.portfolio.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.profiles")
    List<User> findAllWithProfiles();

    User findByLogin(String login);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profiles WHERE u.id = :id")
    Optional<User> findByIdWithProfiles(@Param("id") UUID id);

    @Query("""
            SELECT DISTINCT u from User u LEFT JOIN FETCH u.profiles p
            where lower(u.login) like :search
            or lower(u.firstName) like :search
            or lower(u.lastName) like :search
            or lower(p.name) like :search
    """)
    List<User> search(@Param("search") String search);
}