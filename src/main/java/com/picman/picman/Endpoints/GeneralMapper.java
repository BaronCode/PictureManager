package com.picman.picman.Endpoints;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Slf4j
@RequestMapping("/")
@Controller
public class GeneralMapper {
    @RequestMapping(value = {"/", "/login", "/u/"})
    public String login(Model model) {
        model.addAttribute("path", "/ login");
        return "u/login";
    }

    @RequestMapping(value = {"/cn/i/upload", "/cn/upload", "/upload"}, method = RequestMethod.GET)
    public String upload(Model model) {
        model.addAttribute("path", "/ upload");
        return "cn/i/upload";
    }

    @RequestMapping(value = {"/404", "/wip"})
    public String wip(Model model) {
        model.addAttribute("path", "/ work in progress");
        return "/wip";
    }
}
