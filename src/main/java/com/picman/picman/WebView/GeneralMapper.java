package com.picman.picman.WebView;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RequestMapping("/")
@Controller
public class GeneralMapper {
    @RequestMapping(value = {"/", "/login", "/u/"})
    public String login(Model model) {
        model.addAttribute("path", "/ login");
        return "u/login";
    }

    @RequestMapping(value = {"/c/i/upload", "/c/upload", "/upload"}, method = RequestMethod.GET)
    public String upload(Model model) {
        model.addAttribute("path", "/ upload");
        return "c/i/upload";
    }

    @RequestMapping(value = {"/404", "/wip"})
    public String wip(Model model) {
        model.addAttribute("path", "/ work in progress");
        return "/wip";
    }
}
