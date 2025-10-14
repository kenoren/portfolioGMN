package fr.caensup.portfolio.dtos;

import fr.caensup.portfolio.entities.Profile;
import lombok.Data;

@Data
public class ProfileDto {
    private String name;

    public Profile toEntity(Profile profile) {
        profile.setName(this.name);
        return profile;
    }
}