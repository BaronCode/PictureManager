package com.picman.picman.Endpoints;

import com.picman.picman.AssignationMgmt.Assignation;
import com.picman.picman.AssignationMgmt.AssignationServiceImplementation;
import com.picman.picman.CategoriesMgmt.Category;
import com.picman.picman.CategoriesMgmt.CategoryServiceImplementation;
import com.picman.picman.Exceptions.AccessDeniedException;
import com.picman.picman.Exceptions.FieldNotFoundException;
import com.picman.picman.Exceptions.ImageProcessingException;
import com.picman.picman.PicturesMgmt.Picture;
import com.picman.picman.PicturesMgmt.PictureBuilder;
import com.picman.picman.PicturesMgmt.PictureServiceImplementation;
import com.picman.picman.SpringSettings.Settings;
import com.picman.picman.SpringSettings.SettingsService;
import com.picman.picman.PicturesMgmt.Thumbs;
import com.picman.picman.Utilities.ZipEntryMultipartFile;
import com.picman.picman.SpringAuthentication.JwtService;
import com.picman.picman.UserMgmt.User;
import com.picman.picman.UserMgmt.UserServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.picman.picman.Utilities.Utilities.*;

@Slf4j
@Controller
@RequestMapping("cn/i/")
public class ImageEdit {
    private final Logger logger;
    private final UserServiceImplementation userService;
    private final PictureServiceImplementation pictureService;
    private final CategoryServiceImplementation categoryService;
    private final AssignationServiceImplementation assignationService;
    private final JwtService jwtService;
    private final SettingsService settings;

    public ImageEdit(UserServiceImplementation usi, PictureServiceImplementation psi, CategoryServiceImplementation csi, AssignationServiceImplementation asi, JwtService js, SettingsService ss) {
        userService = usi;
        pictureService = psi;
        categoryService = csi;
        assignationService = asi;
        jwtService = js;
        settings = ss;
        logger = LoggerFactory.getLogger(ImageEdit.class);
    }

    @GetMapping("/")
    public String root() {
        return "galley";
    }

    @GetMapping("/delete")
    public String delete(
            @CookieValue(name = "jwt") String jwt,
            @RequestParam("pic-id") int id
    ) throws FieldNotFoundException {
        String email = jwtService.extractUserMail(jwt);
        User current = userService.findByEmail(email);
        Set<Character> privileges = current.getPrivileges();

        if (!checkPermissions(privileges, new char[]{'o', 'w', 's'})) {
            logger.error("User {} tried to delete Picture {}", current.getId(), id);
            throw new AccessDeniedException("Access denied: user has not enough privileges!");
        }

        Picture p = pictureService.getById(id);
        if (p_nexists(p, id)) {
            throw new FieldNotFoundException("Picture with id" + id + "does not exist!");
        }

        File pictureFile = new File(Settings.get("output") + p.getPath() + "." + p.getExt());
        if (pictureFile.isFile() && p_deletable(p, privileges)) {
            if (pictureFile.delete()) {
                Thumbs.deleteThumb(p.getPath().concat(".").concat(p.getExt()));
                pictureService.deleteById(id);
                logger.info("Deleted file {} and all matching database entries", p.getPath());
            }
        } else {
            logger.warn("Something went wrong while deleting file {}", p.getPath());
        }
        return "redirect:/cn/dashboard";
    }
    
    @GetMapping( "/edit")
    public String edit(
            @CookieValue(name = "jwt") String jwt,
            @RequestParam("pic-id") int id,
            Model model
    ) throws FieldNotFoundException {
        String email = jwtService.extractUserMail(jwt);
        User current = userService.findByEmail(email);
        Set<Character> privileges = current.getPrivileges();

        if (!checkPermissions(privileges, new char[]{'o', 's', 'w'})) {
            throw new AccessDeniedException("Access denied: user has not enough privileges!");
        }

        Picture picture = pictureService.getById(id);
        if (p_nexists(picture, id)) {
            throw new FieldNotFoundException("Picture with id" + id + "does not exist!");
        }

        List<Category> pictureCat = assignationService.getCategoriesByPictureId(picture.getId());
        List<Category> allCategories = categoryService.findAll();

        model.addAttribute("pic", picture);
        model.addAttribute("defaultPath", Settings.get("output"));
        model.addAttribute("pictureCategories", pictureCat);
        model.addAttribute("categories", allCategories);
        model.addAttribute("path", "/ image edit");
        return "cn/i/edit";
    }

    @PostMapping("/tagedit")
    @ResponseBody
    public Map<String, String> tagedit(
            @CookieValue(name = "jwt") String jwt,
            @RequestParam("pic-id") String sid,
            @RequestParam("tag-id") String stagid,
            @RequestParam("remove") String sremove
    ) {
        String email = jwtService.extractUserMail(jwt);
        User current = userService.findByEmail(email);
        Set<Character> privileges = current.getPrivileges();

        int id = Integer.parseInt(sid);
        boolean remove = Boolean.parseBoolean(sremove);

        Picture p = pictureService.getById(id);
        if (p.isProtection()) {
            if (!checkPermissions(privileges, new char[]{'o', 's'})) {
                throw new AccessDeniedException("Access denied: user has not enough privileges!");
            }
        }


        if (remove) {
            int tagid = Integer.parseInt(stagid);
            Assignation a = new Assignation(p, categoryService.findById(tagid));
            assignationService.removeAssignation(a);
        } else {
            int[] tags = Arrays.stream(stagid.split(",")).mapToInt(Integer::parseInt).toArray();
            for (int i : tags) {
                Assignation a = new Assignation(p, categoryService.findById(i));
                assignationService.addAssignation(a);
            }
        }

        return Map.of("redirect","/cn/i/edit?pic-id=".concat(String.valueOf(p.getId())));
    }

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, String> upload(
            @CookieValue(name = "jwt") String jwt,
            @RequestPart(value = "file") MultipartFile file,
            @RequestPart(value = "photographer") String photographer
    ) {
        String email = jwtService.extractUserMail(jwt);
        User current = userService.findByEmail(email);
        Set<Character> privileges = current.getPrivileges();

        if (!checkPermissions(privileges, new char[]{'o', 's', 'w'})) {
            throw new AccessDeniedException("Access denied: user has not enough privileges!");
        }


        if (file.getOriginalFilename() != null && file.getOriginalFilename().endsWith(".zip")) {
            List<MultipartFile> files = new ArrayList<>();
            try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (entry.isDirectory()) continue;

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = zis.read(buf)) > 0) {
                        bos.write(buf, 0, len);
                    }
                    files.add(new ZipEntryMultipartFile(entry.getName(), bos.toByteArray()));
                    zis.closeEntry();
                }
            } catch (IOException ioe) {
                logger.error("Error while unwrapping zip file!");
            }

            files.forEach(f->{
                Picture p = handleFile(f);
                if (p != null) {
                    p.setPhotographer(photographer);
                    pictureService.addPicture(p);
                }
            });
            return Map.of("redirect","/cn/dashboard");
        } else {
            Picture p = handleFile(file);
            if (p == null) {
                throw new ImageProcessingException("An error happened while processing the image");
            } else {
                p.setPhotographer(photographer);
                pictureService.addPicture(p);
            }


            return Map.of("redirect","/cn/i/edit?pic-id=".concat(String.valueOf(p.getId())));
        }
    }


    private Picture handleFile(MultipartFile file) {
        try {
            if (file.getOriginalFilename()==null) {
                throw new ImageProcessingException("Original file has no name!");
            }
            String path = Settings.get("output")
                    .concat(
                            file
                                    .getOriginalFilename()
                                    .substring(
                                            file.
                                                    getOriginalFilename()
                                                    .lastIndexOf(File.separator)+1
                                    )
                    );

            if (Files.exists(Path.of(path))) {
                return null;
            }

            File saved = new File(path);
            file.transferTo(saved);

            return PictureBuilder.buildByFile(saved, true, Settings.get("output"), pictureService);

        } catch (IOException e) {
            throw new ImageProcessingException("An error happened while transfering temporary image file");
        } catch (com.drew.imaging.ImageProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
