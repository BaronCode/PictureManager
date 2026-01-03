package com.picman.picman.Endpoints;

import com.picman.picman.SpringAuthentication.JwtService;
import com.picman.picman.SpringSettings.PicmanSettings;
import com.picman.picman.UserMgmt.User;
import com.picman.picman.UserMgmt.UserServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("c/")
public class Home {
    private final Logger logger;
    private final UserServiceImplementation userService;
    private final JwtService jwtService;
    private final PicmanSettings picmanSettings;

    public Home(UserServiceImplementation usi, JwtService js) {
        userService = usi;
        jwtService = js;
        picmanSettings = new PicmanSettings();
        logger = LoggerFactory.getLogger(Home.class);
    }

    @RequestMapping("/home")
    public String home(@CookieValue(name = "jwt", required = false) String jwt, Model model) {
        String email = jwtService.extractUserMail(jwt);
        User current = userService.findByEmail(email);

        Set<Character> privileges = current.getPrivileges();
        Set<String> longPrivileges = privileges
                .stream()
                .map(c->
                    switch (c) {
                        case 'o' -> "administrator";
                        case 'u' -> "user manager";
                        case 'd' -> "delete";
                        case 'w' -> "write";
                        case 'r' -> "readonly";
                        case 's' -> "support";
                        default -> throw new IllegalStateException("Unexpected value: " + c);
                    }
                )
                .collect(Collectors.toSet()
        );

        model.addAttribute("name", current.getName());
        model.addAttribute("email", current.getEmail());
        model.addAttribute("uuid", current.getId());
        model.addAttribute("org", current.getOrganization());
        model.addAttribute("privileges", longPrivileges);
        model.addAttribute("path", " / home");
        model.addAttribute("gear", privileges.contains('o') || privileges.contains('u') || privileges.contains('s'));
        return "c/home";
    }
}
