package com.picman.picman.Endpoints;

import com.picman.picman.SpringAuthentication.JwtService;
import com.picman.picman.SpringSettings.Settings;
import com.picman.picman.UserMgmt.User;
import com.picman.picman.UserMgmt.UserServiceImplementation;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequestMapping("cn/admin/")
public class AdminDashboard {

    private final JwtService jwtService;
    private final UserServiceImplementation userService;
    private final PasswordEncoder passwordEncoder;

    public AdminDashboard(JwtService jwtService, UserServiceImplementation userServiceImplementation, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userService = userServiceImplementation;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String root() {
        return "cn/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            @CookieValue(name = "jwt") String jwt,
            Model model)
    {
        String email = jwtService.extractUserMail(jwt);
        User u = userService.findByEmail(email);

        model.addAttribute("users", userService.findAll());
        model.addAttribute("path", "/ admin dashboard");
        return "cn/admin/dashboard";
    }

    @PostMapping("/create-user")
    public String createUser(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("tempPsw") String tempPsw,
            @RequestParam("roles") String roles
    ) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPrivileges(Set.of());
        //TODO: check how roles are parsed, if added via HTML checkbox
        u.setPassword("TEMPPSW_" + passwordEncoder.encode(tempPsw));

        userService.save(u);
        return "cn/admin/dashboard";
    }

    @GetMapping("/delete-user")
    public String delete(
            @RequestParam("id") long id
    ) {
        User u = userService.findById(id);

        if (!Settings.get("super_admin_id").contains(String.valueOf(id))) {
            userService.deleteById(id);
        }
            
        return "cn/admin/dashboard";
    }
}
