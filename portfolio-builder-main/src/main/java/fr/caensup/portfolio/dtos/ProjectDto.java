package fr.caensup.portfolio.dtos;

import fr.caensup.portfolio.entities.Project;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ProjectDto {
    private String title;
    private String description;
    private String imageUrl;
    private String projectUrl;

    @DateTimeFormat(pattern = "yyyy-MM-dd") // Pour la gestion de la date dans les formulaires
    private LocalDate completionDate;

    public Project toEntity(Project project) {
        project.setTitle(this.title);
        project.setDescription(this.description);
        project.setImageUrl(this.imageUrl);
        project.setProjectUrl(this.projectUrl);
        project.setCompletionDate(this.completionDate);
        return project;
    }
}