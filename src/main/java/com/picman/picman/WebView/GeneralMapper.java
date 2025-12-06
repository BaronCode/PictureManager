package com.picman.picman.WebView;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RequestMapping("/")
@Controller
public class GeneralMapper {
    @RequestMapping(value = {"/", "/login"})
    public RedirectView login() {
        return new RedirectView("/u/login");
    }
}
