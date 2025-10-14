package fr.caensup.portfolio.dtos;

import fr.caensup.portfolio.entities.Portfolio;
import lombok.Data;

@Data
public class PortfolioDto {
    private String name;
    private String themeName; // Nouveau champ

    public Portfolio toEntity(Portfolio portfolio) {
        portfolio.setName(this.name);
        portfolio.setThemeName(this.themeName); // Mettre à jour le thème
        return portfolio;
    }
}