package com.picman.picman.Endpoints;

import com.picman.picman.Exceptions.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ControllerAdvice
public class CentralizedHttpStatusHandler {

    @ExceptionHandler(NotImplementedException.class)
    public String _501Handler(Model model) {
        model.addAttribute("path", "/ work in progress");
        return "wip";
    }

    @ExceptionHandler(InvalidEntryPointException.class)
    public String _401Handler(InvalidEntryPointException ex, Model model) {
        model.addAttribute("path", "/ access denied!");
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String _403Handler(AccessDeniedException ex, Model model) {
        model.addAttribute("path", "/ access denied!");
        model.addAttribute("error", ex.getMessage());
        return "error";
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public String _404EntityHandler(EntityNotFoundException ex, Model model) {
        model.addAttribute("path", "/ not found");
        model.addAttribute("class", ex.getOriginClass());
        model.addAttribute("error", ex.getMessage());
        return "error";
    }
    @ExceptionHandler(InvalidFormParamException.class)
    public String _422Handler(InvalidFormParamException ex, Model model) {
        model.addAttribute("path", "/ invalid form");
        model.addAttribute("class", ex.getOriginClass());
        model.addAttribute("error", ex.getMessage());
        return "error";
    }
}
