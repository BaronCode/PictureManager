package com.picman.picman.Endpoints;

import com.picman.picman.Exceptions.PicmanSettingsDiscrepancyException;
import com.picman.picman.SpringAuthentication.JwtService;
import com.picman.picman.SpringAuthentication.LoginResponse;
import com.picman.picman.SpringAuthentication.UserDetailsService;
import com.picman.picman.SpringSettings.PicmanSettings;
import com.picman.picman.UserMgmt.UserServiceImplementation;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("u/")
public class Login {
    private final Logger logger;
    private final UserDetailsService userDetailsService;
    private final UserServiceImplementation userServiceImplementation;
    private final PicmanSettings picmanSettings;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public Login(UserDetailsService uds, UserServiceImplementation usi, AuthenticationManager am, JwtService js) {
        userDetailsService = uds;
        userServiceImplementation = usi;
        picmanSettings = new PicmanSettings();
        authenticationManager = am;
        jwtService = js;
        logger = LoggerFactory.getLogger(Login.class);
    }

    @RequestMapping(value="/login", method= RequestMethod.POST)
    public ResponseEntity<?> login(
            @RequestParam("email") 	String 		email,
            @RequestParam("password") 	String 		psw
    )
    {
        logger.info("New login request received");
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, psw)
            );
            logger.info("Login successful");

            String token = jwtService.generateToken(auth.getName(), auth.getAuthorities());

            if (!userServiceImplementation.findByEmail(jwtService.extractUserMail(token)).getOrganization().equals(picmanSettings.getDefaultOrganizationName())) {
                throw new PicmanSettingsDiscrepancyException("Database organization entry does not match with internal settings");
            }

            ResponseCookie cookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(jwtService.getJwtExpiration() / 1000)
                    .sameSite("LaX")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new LoginResponse(token, jwtService.getJwtExpiration()));
        } catch (AuthenticationException e) {
            logger.info("Login unsuccessful");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong email or password");
        }
    }
}
