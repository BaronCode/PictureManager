package com.picman.picman.Endpoints;

import com.picman.picman.AssignationMgmt.AssignationServiceImplementation;
import com.picman.picman.CategoriesMgmt.Category;
import com.picman.picman.Exceptions.ImageProcessingException;
import com.picman.picman.LoggingMgmt.LogServiceImplementation;
import com.picman.picman.PicturesMgmt.Picture;
import com.picman.picman.PicturesMgmt.PictureServiceImplementation;
import com.picman.picman.SpringAuthentication.JwtService;
import com.picman.picman.SpringSettings.Settings;
import com.picman.picman.UserMgmt.User;
import com.picman.picman.UserMgmt.UserServiceImplementation;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("cn/admin/")
public class AdminDashboard {

    private final JwtService jwtService;
    private final UserServiceImplementation userService;
    private final PasswordEncoder passwordEncoder;
    private final LogServiceImplementation logService;
    private final PictureServiceImplementation pictureService;
    private final AssignationServiceImplementation assignationService;

    public AdminDashboard(JwtService jwtService, UserServiceImplementation userServiceImplementation, PasswordEncoder passwordEncoder, LogServiceImplementation logServiceImplementation, PictureServiceImplementation pictureServiceImplementation, AssignationServiceImplementation assignationServiceImplementation) {
        this.jwtService = jwtService;
        this.userService = userServiceImplementation;
        this.passwordEncoder = passwordEncoder;
        this.logService = logServiceImplementation;
        this.pictureService = pictureServiceImplementation;
        this.assignationService = assignationServiceImplementation;
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
        LinkedHashMap<Picture, List<Integer>> pcbind = new LinkedHashMap<>();

        for (Picture p : pictureService.getAllOrdered()) {
            pcbind.put(
                    p,
                    assignationService.getCategoriesByPictureId(p.getId()).stream().map(Category::getId).toList()
            );
        }

        model.addAttribute("users", userService.findAll());
        model.addAttribute("pictures", pictureService.findAll());
        model.addAttribute("pcbind", pcbind);
        model.addAttribute("roles", List.of('o', 'u', 's', 'w', 'd', 'r'));
        model.addAttribute("logs", logService.findAll());
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

    @GetMapping("/protect-picture")
    public String protect(
            @RequestParam("pic-id") int id
    ) {
        Picture p = pictureService.getById(id);
        if (p == null) {
            throw new ImageProcessingException("An error happened while processing the image");
        }
        p.setProtection(!p.isProtection());
        pictureService.save(p);

        return "redirect:/cn/admin/dashboard";
    }
}
