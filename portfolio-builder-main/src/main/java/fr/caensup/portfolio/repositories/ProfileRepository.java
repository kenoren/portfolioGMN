package fr.caensup.portfolio.repositories;

import fr.caensup.portfolio.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
}