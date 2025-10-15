package fr.caensup.portfolio.dtos;

import fr.caensup.portfolio.entities.User;
import lombok.Data;

@Data
public class UserDto {
    private String login;
    private String firstName;
    private String lastName;
    private String password; // Assure-toi que ce champ existe bien

    public User toEntity(User user){
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setLogin(login);
        user.setPassword(password); // <--- AJOUTE CETTE LIGNE
        return user;
    }

    // Les getters et setters sont générés par @Data de Lombok, donc tu n'as pas besoin de les écrire explicitement
    // si tu utilises Lombok. Si tu ne l'utilises pas, assure-toi d'avoir un getter et un setter pour 'password'.
    // public String getPassword() {
    //     return password;
    // }
    // public void setPassword(String password) {
    //     this.password = password;
    // }
}