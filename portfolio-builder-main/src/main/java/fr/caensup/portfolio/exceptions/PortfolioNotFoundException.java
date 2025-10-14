package fr.caensup.portfolio.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PortfolioNotFoundException extends Exception {
    public PortfolioNotFoundException(String message) {
        super(message);
    }
}