package com.picman.picman.WebView;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("/c")
@Controller
public class ContentMapper {
    @RequestMapping("/")
    public String index() {
        return "c/dashboard";
    }
}
