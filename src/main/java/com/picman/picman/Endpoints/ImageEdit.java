package com.picman.picman.Endpoints;

import com.picman.picman.AssignationMgmt.Assignation;
import com.picman.picman.AssignationMgmt.AssignationServiceImplementation;
import com.picman.picman.CategoriesMgmt.Category;
import com.picman.picman.CategoriesMgmt.CategoryServiceImplementation;
import com.picman.picman.Exceptions.AccessDeniedException;
import com.picman.picman.Exceptions.FieldNotFoundException;
import com.picman.picman.Exceptions.ImageProcessingException;
import com.picman.picman.LoggingMgmt.Log;
import com.picman.picman.LoggingMgmt.LogServiceImplementation;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
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
    private final LogServiceImplementation logService;

    public ImageEdit(UserServiceImplementation usi, PictureServiceImplementation psi, CategoryServiceImplementation csi, AssignationServiceImplementation asi, JwtService js, SettingsService ss, LogServiceImplementation lsi) {
        userService = usi;
        pictureService = psi;
        categoryService = csi;
        assignationService = asi;
        jwtService = js;
        settings = ss;
        logger = LoggerFactory.getLogger(ImageEdit.class);
        logService = lsi;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/cn/gallery";
    }

    @GetMapping("/delete")
    public String delete(
            @RequestParam("pic-id") int id
    ) throws FieldNotFoundException {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        User u = userService.findByEmail(a.getName());
        Picture p = pictureService.getById(id);

        if (p == null) {
            throw new FieldNotFoundException("Picture with id" + id + "does not exist!");
        }
        if (p.isProtection() && !u.hasAuthorities('o', 's')) {
            Log l = new Log(LocalDateTime.now(), "/cn/i/delete?pic-id=" + id, "ImageEdit", u, "Tried to delete protected picture " + id);
            logService.save(l);
            throw new AccessDeniedException("Cannot delete protected picture!");
        }

        File pictureFile = new File(Settings.get("output") + p.getPath() + "." + p.getExt());
        if (pictureFile.isFile()) {
            if (pictureFile.delete()) {
                Thumbs.deleteThumb(p.getPath().concat(".").concat(p.getExt()));
                pictureService.deleteById(id);
                Log l = new Log(LocalDateTime.now(), "/cn/i/edit?pic-id=" + id, "ImageEdit", u, "Deleted picture " + id);
                logService.save(l);
            }
        } else {
            logger.warn("Something went wrong while deleting file {}", p.getPath());
        }
        return "redirect:/cn/gallery";
    }
    
    @GetMapping( "/edit")
    public String edit(
            @RequestParam("pic-id") int id,
            Model model
    ) throws FieldNotFoundException {
        Picture p = pictureService.getById(id);

        if (p == null) {
            throw new FieldNotFoundException("Picture with id" + id + "does not exist!");
        }

        List<Category> pictureCat = assignationService.getCategoriesByPictureId(p.getId());
        List<Category> allCategories = categoryService.findAll();

        model.addAttribute("pic", p);
        model.addAttribute("defaultPath", Settings.get("output"));
        model.addAttribute("pictureCategories", pictureCat);
        model.addAttribute("categories", allCategories);
        model.addAttribute("path", "/ image edit");
        return "cn/i/edit";
    }

    @PostMapping("/tagedit")
    @ResponseBody
    public Map<String, String> tagedit(
            @RequestParam("pic-id") String sid,
            @RequestParam("tag-id") String stagid,
            @RequestParam("remove") String sremove
    ) throws FieldNotFoundException {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        User u = userService.findByEmail(a.getName());

        int id = Integer.parseInt(sid);
        boolean remove = Boolean.parseBoolean(sremove);

        Picture p = pictureService.getById(id);
        if (p == null) {
            throw new FieldNotFoundException("Picture with id" + id + "does not exist!");
        }

        if (p.isProtection() && !u.hasAuthorities('o', 's')) {
            throw new AccessDeniedException("Access denied: user has not enough privileges!");
        }

        //mMMmMMmm i love ASSignations
        if (remove) {
            int tagid = Integer.parseInt(stagid);
            Assignation ass = new Assignation(p, categoryService.findById(tagid));
            assignationService.removeAssignation(ass);
            Log l = new Log(LocalDateTime.now(), "/cn/i/tagedit", "ImageEdit", u, "Removed tag " + tagid + " from picture " + id);
            logService.save(l);
        } else {
            int[] tags = Arrays.stream(stagid.split(",")).mapToInt(Integer::parseInt).toArray();
            for (int i : tags) {
                Assignation ass = new Assignation(p, categoryService.findById(i));
                assignationService.addAssignation(ass);
                Log l = new Log(LocalDateTime.now(), "/cn/i/tagedit", "ImageEdit", u, "Added tag(s) " + Arrays.toString(tags) + " to picture " + id);
                logService.save(l);
            }
        }

        return Map.of("redirect","/cn/i/edit?pic-id=".concat(String.valueOf(p.getId())));
    }

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, String> upload(
            @RequestPart(value = "file") MultipartFile file,
            @RequestPart(value = "photographer") String photographer
    ) {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        User u = userService.findByEmail(a.getName());

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
                Log l = new Log(LocalDateTime.now(), "/cn/i/upload", "ImageEdit", u, "Error while unwrapping zip file");
                logService.save(l);
            }

            files.forEach(f->{
                Picture p = handleFile(file);
                if (p == null) {
                    throw new ImageProcessingException("An error happened while processing the image");
                } else {
                    p.setPhotographer(photographer);
                    Picture saved = pictureService.addPicture(p);
                    Log l = new Log(LocalDateTime.now(), "/cn/i/upload", "ImageEdit", u, "Saved new picture " + saved.getId());
                    logService.save(l);
                }
            });
            return Map.of("redirect","/cn/gallery");
        } else {
            Picture p = handleFile(file);
            if (p == null) {
                throw new ImageProcessingException("An error happened while processing the image");
            } else {
                p.setPhotographer(photographer);
                Picture saved = pictureService.addPicture(p);
                Log l = new Log(LocalDateTime.now(), "/cn/i/upload", "ImageEdit", u, "Saved new picture " + saved.getId());
                logService.save(l);
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
