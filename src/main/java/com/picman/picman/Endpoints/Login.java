package com.picman.picman.Endpoints;

import com.picman.picman.Exceptions.AccessDeniedException;
import com.picman.picman.SpringAuthentication.JwtService;
import com.picman.picman.SpringAuthentication.LoginResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("u/")
public class Login {
    private final Logger logger;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public Login(AuthenticationManager am, JwtService js) {
        authenticationManager = am;
        jwtService = js;
        logger = LoggerFactory.getLogger(Login.class);
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
        logger.info("New login request received");
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, psw)
            );
            logger.info("Login successful");

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
            logger.info("Login unsuccessful");
            throw new AccessDeniedException("Wrong username or password");
        }
    }
}
