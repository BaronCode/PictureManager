package com.picman.picman.Endpoints;

import com.picman.picman.CategoriesMgmt.Category;
import com.picman.picman.CategoriesMgmt.CategoryServiceImplementation;
import com.picman.picman.Exceptions.InvalidFormParamException;
import com.picman.picman.SpringAuthentication.JwtService;
import com.picman.picman.UserMgmt.User;
import com.picman.picman.UserMgmt.UserServiceImplementation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@Controller
@RequestMapping("cn/c/")
public class Categories {
    private final Logger logger;
    private final UserServiceImplementation userService;
    private final CategoryServiceImplementation categoryService;
    private final JwtService jwtService;

    public Categories(UserServiceImplementation usi, CategoryServiceImplementation csi, JwtService js) {
        userService = usi;
        categoryService = csi;
        jwtService = js;
        logger = LoggerFactory.getLogger(Categories.class);
    }

    @GetMapping("/")
    public String root() {
        return "cn/c/categories";
    }

    @GetMapping("/categories")
    public String home(
            @CookieValue(name = "jwt") String jwt,
            Model model
    ) {
        String email = jwtService.extractUserMail(jwt);
        User current = userService.findByEmail(email);
        Set<Character> privileges = current.getPrivileges();

        model.addAttribute("all", categoryService.findAll());
        model.addAttribute("o", privileges.contains('o'));
        model.addAttribute("d", privileges.contains('d'));
        model.addAttribute("w", privileges.contains('w'));
        model.addAttribute("s", privileges.contains('s'));
        model.addAttribute("path", "/ categories management");
        return "cn/c/categories";
    }

    @PostMapping("/create")
    public String create(
            @RequestParam("name") @Valid String name,
            @RequestParam(value = "description", defaultValue = "") String description
    ) throws InvalidFormParamException {
        //Permissions are already checked in SecurityConfig, so no need for jwt token
        if (name == null || name.isBlank()) {
            throw new InvalidFormParamException(Categories.class, "Cannot assign null or blank name to category");
        }

        Category c = new Category();
        c.setName(name);
        c.setDescription(description);

        categoryService.save(c);
        logger.info("Created new category");
        return "redirect:categories";
    }

    @GetMapping("/delete")
    public String delete(
            @RequestParam("cat-id") @Valid Integer id
    ) throws InvalidFormParamException {
        //Permissions are already checked in SecurityConfig, so no need for jwt token
        if (id == null) throw new InvalidFormParamException(Categories.class, "Category id cannot be null");

        categoryService.deleteById(id);
        return "redirect:categories";
    }

    @GetMapping("/edit")
    public String edit(
            @RequestParam("cat-id") @Valid Integer id,
            Model model
    ) throws InvalidFormParamException {
        //Permissions are already checked in SecurityConfig, so no need for jwt token
        if (id == null) throw new InvalidFormParamException(Categories.class, "Category id cannot be null");

        Category c = categoryService.findById(id);

        model.addAttribute("path", "/ category editor");
        model.addAttribute("id", c.getId());
        model.addAttribute("name", c.getName());
        model.addAttribute("description", c.getDescription());
        return "cn/c/edit";
    }

    @PostMapping(value = "/edit/{cat-id}")
    public String edit_p(
            @PathVariable("cat-id") @Valid Integer id,
            @RequestParam("name") @Valid String name,
            @RequestParam("description") @Valid String description
    ) throws InvalidFormParamException {
        //Permissions are already checked in SecurityConfig, so no need for jwt token
        if (id == null) throw new InvalidFormParamException(Categories.class, "Category id cannot be null");

        Category old = categoryService.findById(id);
        Category n = new Category();

        if (name != null && !name.isBlank()) {
            n.setName(name.strip());
        } else n.setName(old.getName());
        if (description == null) {
            n.setDescription("");
        } else n.setDescription(description.strip());
        if (!old.equals(n)) {
            categoryService.updateCategory(id, n);
        }

        return "redirect:/cn/c/categories";
    }
}
