package com.picman.picman.Endpoints;

import com.picman.picman.Exceptions.NotImplementedException;
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
}
