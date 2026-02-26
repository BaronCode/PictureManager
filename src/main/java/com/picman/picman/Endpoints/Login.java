package com.picman.picman.Endpoints;

import com.picman.picman.Exceptions.AccessDeniedException;
import com.picman.picman.LoggingMgmt.Log;
import com.picman.picman.LoggingMgmt.LogServiceImplementation;
import com.picman.picman.SpringAuthentication.JwtService;
import com.picman.picman.UserMgmt.User;
import com.picman.picman.UserMgmt.UserServiceImplementation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("u/")
public class Login {
    private final Logger logger;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LogServiceImplementation logService;
    private final UserServiceImplementation userService;

    public Login(AuthenticationManager am, JwtService js, LogServiceImplementation lsi, UserServiceImplementation usi) {
        authenticationManager = am;
        jwtService = js;
        logger = LoggerFactory.getLogger(Login.class);
        this.logService = lsi;
        this.userService = usi;
    }

    @GetMapping("/")
    public String root() {
        return "u/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "u/login";
    }

    @PostMapping("/loginsubmit")
    public String login(
            @RequestParam("email") 	String 		email,
            @RequestParam("password") 	String 		psw,
            HttpServletResponse response
    )
    {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, psw)
            );
            User u = userService.findByEmail(email);
            Log l = new Log(LocalDateTime.now(), "/u/login", "Login", u, "Successful login attempt");
            logService.save(l);

            String token = jwtService.generateToken(auth.getName(), auth.getAuthorities());

            /* Obsolete organization check
            if (!userServiceImplementation.findByEmail(jwtService.extractUserMail(token)).getOrganization().equals(picmanSettings.getDefaultOrganizationName())) {
                throw new PicmanSettingsDiscrepancyException("Database organization entry does not match with internal settings");
            } */

            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge((int) (jwtService.getJwtExpiration() / 1000));
                    //.sameSite("LaX");

            response.addCookie(cookie);

            return "redirect:/cn/gallery";
        } catch (AuthenticationException e) {
            Log l = new Log(LocalDateTime.now(), "/u/login", "Login", null, "Failed login attempt");
            logService.save(l);
            throw new AccessDeniedException("Wrong username or password");
        }
    }
}
