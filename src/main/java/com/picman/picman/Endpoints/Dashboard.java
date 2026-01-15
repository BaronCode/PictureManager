package com.picman.picman.Endpoints;

import com.picman.picman.AssignationMgmt.Assignation;
import com.picman.picman.AssignationMgmt.AssignationServiceImplementation;
import com.picman.picman.CategoriesMgmt.Category;
import com.picman.picman.CategoriesMgmt.CategoryService;
import com.picman.picman.CategoriesMgmt.CategoryServiceImplementation;
import com.picman.picman.Exceptions.InvalidTagsResearchException;
import com.picman.picman.PicturesMgmt.Picture;
import com.picman.picman.PicturesMgmt.PictureServiceImplementation;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Slf4j
@Controller
@RequestMapping("c/")
public class Dashboard {
    private final Logger logger;
    private final UserServiceImplementation userService;
    private final PictureServiceImplementation pictureService;
    private final CategoryServiceImplementation categoryService;
    private final AssignationServiceImplementation assignationService;
    private final JwtService jwtService;
    private final PicmanSettings picmanSettings;

    public Dashboard(UserServiceImplementation usi, PictureServiceImplementation psi, CategoryServiceImplementation csi, AssignationServiceImplementation asi, JwtService js) {
        userService = usi;
        pictureService = psi;
        categoryService = csi;
        assignationService = asi;
        jwtService = js;
        picmanSettings = new PicmanSettings();
        logger = LoggerFactory.getLogger(Home.class);
    }

    @RequestMapping("/dashboard")
    public String dashboard(@CookieValue(name = "jwt", required = false) String jwt, Model model) {
        String email = jwtService.extractUserMail(jwt);
        User current = userService.findByEmail(email);
        Set<Character> privileges = current.getPrivileges();

        model.addAttribute("categories", categoryService.findAll().stream().map(Category::getName).toList());
        model.addAttribute("path", "/ dashboard");
        model.addAttribute("defaultPath", picmanSettings.getDefaultFileOutput());
        model.addAttribute("last", pictureService.getLast20Added());
        model.addAttribute("uuid", current.getId());
        model.addAttribute("o", privileges.contains('o'));
        model.addAttribute("d", privileges.contains('d'));
        model.addAttribute("w", privileges.contains('w'));
        model.addAttribute("s", privileges.contains('s'));
        model.addAttribute("r", privileges.contains('r'));
        return "c/dashboard";
    }
    @RequestMapping("/dashboard/submitSearchQuery")
    public String searchQuery(@CookieValue(name = "jwt", required = false) String jwt,
                              @RequestParam("hidden-tags") String tags,
                              Model model) throws InvalidTagsResearchException {
        return "wip";
        /*

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
            return "c/research";
        } else {
            throw new PicmanSettings.InvalidTagsResearchException("Tried to parse unrecognized tag(s): '" + tagsArray + "'");
        }

         */
    }
}

