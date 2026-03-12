package com.picman.picman.Endpoints;

import com.picman.picman.AssignationMgmt.AssignationServiceImplementation;
import com.picman.picman.CategoriesMgmt.Category;
import com.picman.picman.CategoriesMgmt.CategoryServiceImplementation;
import com.picman.picman.Exceptions.*;
import com.picman.picman.LoggingMgmt.Log;
import com.picman.picman.LoggingMgmt.LogServiceImplementation;
import com.picman.picman.PicturesMgmt.Picture;
import com.picman.picman.PicturesMgmt.PictureServiceImplementation;
import com.picman.picman.SpringAuthentication.JwtService;
import com.picman.picman.SpringSettings.Settings;
import com.picman.picman.SpringSettings.SettingsService;
import com.picman.picman.UserMgmt.User;
import com.picman.picman.UserMgmt.UserServiceImplementation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
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
    private final SettingsService settingsService;
    private final CategoryServiceImplementation categoryService;

    public AdminDashboard(JwtService jwtService, UserServiceImplementation userServiceImplementation, PasswordEncoder passwordEncoder, LogServiceImplementation logServiceImplementation, PictureServiceImplementation pictureServiceImplementation, AssignationServiceImplementation assignationServiceImplementation, SettingsService settingsService, CategoryServiceImplementation categoryServiceImplementation) {
        this.jwtService = jwtService;
        this.userService = userServiceImplementation;
        this.passwordEncoder = passwordEncoder;
        this.logService = logServiceImplementation;
        this.pictureService = pictureServiceImplementation;
        this.assignationService = assignationServiceImplementation;
        this.settingsService = settingsService;
        this.categoryService = categoryServiceImplementation;
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
        LinkedHashMap<Category, List<Long>> cpsbind = new LinkedHashMap<>();
        LinkedHashMap<String, String> settings = new LinkedHashMap<>();

        for (Picture p : pictureService.getAllOrdered()) {
            pcbind.put(
                    p,
                    assignationService.getCategoriesByPictureId(p.getId()).stream().map(Category::getId).toList()
            );
        }

        for (Category c : categoryService.findAll().stream().sorted(Comparator.comparing(Category::getId)).toList()) {
            cpsbind.put(
                    c,
                    assignationService.getPicturesByCategory(c)
            );
        }

        for (String key : settingsService.getAll().keySet().stream().sorted().toList()) {
            settings.put(key, settingsService.get(key));
        }

        model.addAttribute("current", u);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("pictures", pictureService.findAll());
        model.addAttribute("cpsbind", cpsbind);
        model.addAttribute("pcbind", pcbind);
        model.addAttribute("roles", List.of('o', 'u', 's', 'w', 'd', 'r'));
        model.addAttribute("logs", logService.findAll());
        model.addAttribute("settings", settings);
        model.addAttribute("path", "/ admin dashboard");
        return "cn/admin/dashboard";
    }

    @PostMapping("/create-user")
    public String createUser(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("organization") String organization
    ) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setOrganization(organization);
        u.setPrivileges(Set.of('r'));
        u.setPassword("TEMPPSW_" + passwordEncoder.encode(String.valueOf(new SecureRandom().nextInt())));

        userService.save(u);
        return "redirect:/cn/admin/dashboard";
    }

    @PostMapping("/edit-privileges")
    @ResponseBody
    public void editPrivileges(
            @RequestParam("user") String id,
            @RequestParam("role") String role,
            @RequestParam("nowchecked") String assign
    ) throws InvalidFormParamException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User main = userService.findByName(auth.getName());

        long uid; char urole; boolean uassign;

        try {
            uid = Integer.parseInt(id);
        } catch (NumberFormatException nfe) {
            throw new InvalidFormParamException(AdminDashboard.class, "Invalid user id");
        }

        if (role == null || role.isBlank() || role.length()>1) {
            throw new InvalidFormParamException(AdminDashboard.class, "Invalid role");
        }
        urole = role.toCharArray()[0];
        if (!List.of('o', 'u', 's', 'w', 'd', 'r').contains(urole)) {
            throw new InvalidFormParamException(AdminDashboard.class, "Invalid role");
        }

        if (assign == null || (!assign.equalsIgnoreCase("true") && !assign.equalsIgnoreCase("false"))) {
            throw new InvalidFormParamException(AdminDashboard.class, "Invalid assignment param");
        }
        uassign = Boolean.parseBoolean(assign);

        User u = userService.findById(uid);
        if (u == null) throw new EntityNotFoundException(AdminDashboard.class, "User " + uid + " not found");
        userService.editPrivileges(u, urole, uassign);
        Log l = new Log(
                LocalDateTime.now(),
                "/cn/admin/edit-privileges",
                "AdminDashboard",
                main,
                (uassign?"Added " : "Removed ") + "privilege " + urole + (uassign?" to " : " from ") + " user " + uid

        );
        logService.save(l);
    }

    @GetMapping("/delete-user")
    public String delete(
            @RequestParam("id") long id
    ) {
        User u = userService.findById(id);

        if (u == null) throw new EntityNotFoundException(AdminDashboard.class, "Could not find user with id " + id);

        if (Settings.get("super_admin_id").equals(String.valueOf(id))) {
            throw new AccessDeniedException("Cannot delete superadmin user via application, database usage needed.");
        }
        userService.deleteById(id);
        return "cn/admin/dashboard";
    }

    @GetMapping("/reset-psw")
    public String resetpsw(
            @RequestParam("id") long id
    ) {
        User u = userService.findById(id);

        if (u == null) throw new EntityNotFoundException(AdminDashboard.class, "Could not find user with id " + id);

        if (Settings.get("super_admin_id").equals(String.valueOf(id))) {
            throw new AccessDeniedException("Cannot reset superadmin password, database usage needed.");
        }

        u.setPassword("TEMPPSW_" + passwordEncoder.encode(String.valueOf(new SecureRandom().nextInt())));
        userService.save(u);
        return "redirect:/cn/admin/dashboard";
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
