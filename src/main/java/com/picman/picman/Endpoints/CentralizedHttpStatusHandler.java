package com.picman.picman.Endpoints;

import com.picman.picman.Exceptions.AccessDeniedException;
import com.picman.picman.Exceptions.InvalidFormParamException;
import com.picman.picman.Exceptions.NotImplementedException;
import com.picman.picman.Exceptions.EntityNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CentralizedHttpStatusHandler {

    @ExceptionHandler(NotImplementedException.class)
    public String _501Handler(Model model) {
        model.addAttribute("path", "/ work in progress");
        return "wip";
    }
    @ExceptionHandler(AccessDeniedException.class)
    public String _403Handler(Model model) {
        model.addAttribute("path", "/ access denied!");
        return "wip";
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
        model.addAttribute("class", ex.getMessage());
        return "error";
    }
}
