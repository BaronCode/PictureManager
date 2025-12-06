package com.picman.picman.WebView;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("u/")
@Controller
public class UserMapper {
    @RequestMapping(value = {"/", "/login"})
    public String index() {
        return "u/login";
    }
}
