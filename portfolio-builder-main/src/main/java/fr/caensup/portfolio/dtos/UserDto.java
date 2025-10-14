package fr.caensup.portfolio.dtos;

import fr.caensup.portfolio.entities.User;
import lombok.Data;

@Data
public class UserDto {
    private String login;

    private String firstName;

    private String lastName;

    public User toEntity(User user){
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setLogin(login);
        return user;
    }

}
