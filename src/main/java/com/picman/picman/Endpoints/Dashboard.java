package com.picman.picman.Endpoints;

import com.picman.picman.AssignationMgmt.AssignationServiceImplementation;
import com.picman.picman.CategoriesMgmt.Category;
import com.picman.picman.CategoriesMgmt.CategoryServiceImplementation;
import com.picman.picman.Exceptions.InvalidTagsResearchException;
import com.picman.picman.PicturesMgmt.Picture;
import com.picman.picman.PicturesMgmt.PictureServiceImplementation;
import com.picman.picman.SpringAuthentication.JwtService;
import com.picman.picman.SpringSettings.PicmanSettings;
import com.picman.picman.SpringSettings.Settings;
import com.picman.picman.UserMgmt.User;
import com.picman.picman.UserMgmt.UserServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("cn/")
public class Dashboard {
    private final UserServiceImplementation userService;
    private final PictureServiceImplementation pictureService;
    private final CategoryServiceImplementation categoryService;
    private final AssignationServiceImplementation assignationService;
    private final JwtService jwtService;
    private final Logger logger = LoggerFactory.getLogger(Dashboard.class);

    public Dashboard(UserServiceImplementation usi, PictureServiceImplementation psi, CategoryServiceImplementation csi, AssignationServiceImplementation asi, JwtService js) {
        userService = usi;
        pictureService = psi;
        categoryService = csi;
        assignationService = asi;
        jwtService = js;
    }

    @GetMapping("/")
    public String root() {
        return "cn/home";
    }

    @GetMapping("/home")
    public String home(@CookieValue(name = "jwt") String jwt, Model model) {
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
        model.addAttribute("path", "/ home");
        model.addAttribute("gear", privileges.contains('o') || privileges.contains('u') || privileges.contains('s'));
        return "cn/home";
    }

    @GetMapping("/dashboard")
    public String dashboard(@CookieValue(name = "jwt", required = false) String jwt, Model model) {
        Set<Character> privileges;

        if (jwt != null) {
            String email = jwtService.extractUserMail(jwt);
            User current = userService.findByEmail(email);
            privileges = current.getPrivileges();
        } else {
            privileges = Set.of('r');
        }

        model.addAttribute("categories", categoryService.findAll().stream().map(Category::getName).toList());
        model.addAttribute("path", "/ dashboard");
        model.addAttribute("defaultPath", Settings.get("output"));
        model.addAttribute("last", pictureService.getLast20Added());
        model.addAttribute("o", privileges.contains('o'));
        model.addAttribute("d", privileges.contains('d'));
        model.addAttribute("w", privileges.contains('w'));
        model.addAttribute("s", privileges.contains('s'));
        model.addAttribute("r", privileges.contains('r'));
        return "cn/dashboard";
    }



    @RequestMapping("/dashboard/submitSearchQuery")
    public String searchQuery(
            @RequestParam("hidden-tags") String tags,
            Model model
    ) throws InvalidTagsResearchException {

        final String[] splitTags = tags.split(",");
        Set<String> tagsArray = new HashSet<>(Arrays.stream(splitTags).toList());
        categoryService.findAll().forEach(i -> tagsArray.removeIf(j -> tagsArray.contains(i.getName())));

        int[] categoryIds = new int[splitTags.length];
        for (int i = 0; i < splitTags.length; i++) {
            categoryIds[i] = categoryService.findByName(splitTags[i]).getId();
        }

        if (tagsArray.isEmpty()) {
            List<Picture> pictures = assignationService.getAssignationsByCategoryList(categoryIds);
            model.addAttribute("fetched", pictures);
            model.addAttribute("tags", tagsArray);
            model.addAttribute("path", "/ search result");
            return "cn/research";
        } else {
            throw new InvalidTagsResearchException("Tried to parse unrecognized tag(s): '" + tagsArray + "'");
        }


    }
}

