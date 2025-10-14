package fr.caensup.portfolio.ui;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UiMessage {
    private String title;
    private String content;
    private String className="info";
    private String icon="circle info";
}
