package com.picman.picman.Endpoints;

import com.picman.picman.AssignationMgmt.AssignationServiceImplementation;
import com.picman.picman.CategoriesMgmt.CategoryServiceImplementation;
import com.picman.picman.Exceptions.NotImplementedException;
import com.picman.picman.PicturesMgmt.Picture;
import com.picman.picman.PicturesMgmt.PictureServiceImplementation;
import com.picman.picman.SpringAuthentication.JwtService;
import com.picman.picman.SpringSettings.PicmanSettings;
import com.picman.picman.UserMgmt.User;
import com.picman.picman.UserMgmt.UserServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.File;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("c/i/")
public class ImageEdit {
    private final Logger logger;
    private final UserServiceImplementation userService;
    private final PictureServiceImplementation pictureService;
    private final CategoryServiceImplementation categoryService;
    private final AssignationServiceImplementation assignationService;
    private final JwtService jwtService;
    private final PicmanSettings picmanSettings;

    public ImageEdit(UserServiceImplementation usi, PictureServiceImplementation psi, CategoryServiceImplementation csi, AssignationServiceImplementation asi, JwtService js) {
        userService = usi;
        pictureService = psi;
        categoryService = csi;
        assignationService = asi;
        jwtService = js;
        picmanSettings = new PicmanSettings();
        logger = LoggerFactory.getLogger(Home.class);
    }

    @RequestMapping("/delete")
    public String delete(
            @CookieValue(name = "jwt", required = false) String jwt,
            @RequestParam("pic-id") int id
    ) {
        String email = jwtService.extractUserMail(jwt);
        User current = userService.findByEmail(email);
        Set<Character> privileges = current.getPrivileges();

        if (privileges.contains('o') || privileges.contains('s') || privileges.contains('d')) {
            Picture p = pictureService.getById(id);
            File pictureFile = new File(picmanSettings.getDefaultFileOutput() + p.getPath());
            if (pictureFile.isFile() && pictureFile.delete()) {
                pictureService.deleteById(id);
                logger.info("Deleted file {} and all matching database entries", p.getPath());
            }

        } else {
            logger.error("User {} tried to delete Picture {}", current.getId(), id);
        }
        return "c/dashboard";
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @RequestMapping("/edit")
    public String edit(
            @CookieValue(name = "jwt", required = false) String jwt,
            @RequestParam("pic-id") int id
    ) {
        throw new NotImplementedException("Function not yet implemented");
    }
}
