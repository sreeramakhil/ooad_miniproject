package com.stockflow.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(Model model) {
        model.addAttribute("error", "Access denied.");
        return "error";
    }

    // Only catch non-Spring errors to avoid interfering with routing
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArg(IllegalArgumentException ex, Model model,
                                   HttpServletRequest request) {
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("path", request.getRequestURI());
        return "error";
    }
}
