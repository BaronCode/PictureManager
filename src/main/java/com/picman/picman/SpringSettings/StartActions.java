package com.picman.picman.SpringSettings;

import com.picman.picman.PicturesMgmt.Picture;
import com.picman.picman.PicturesMgmt.PictureServiceImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class StartActions {
    private final PictureServiceImplementation pictureService;
    private final Logger logger;

    public StartActions(PictureServiceImplementation psi) {
        pictureService = psi;
        logger = LoggerFactory.getLogger(StartActions.class);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void checkPicmanSettings() {
        PicmanSettings pms = new PicmanSettings();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void updateImageDatabase() {
        PicmanSettings pms = new PicmanSettings();
        File superDir = new File(pms.getDefaultFileOutput());
        boolean upToDate = true;
        if (superDir.isDirectory()) {
            File[] fileList = superDir.listFiles();
            if (fileList != null) {
                List<Picture> all = pictureService.findAll();
                for (File f : fileList) {
                    long i = all.stream().filter(p->p.getPath().equals(f.getName())).count();
                    if (i == 0) {
                        upToDate = false;
                        logger.warn("Detected change in {}, adding to database image {}", pms.getDefaultFileOutput(), f.getAbsolutePath());
                        Picture p = new Picture();
                        p.setPath(f.getName()); p.setDateadded(LocalDateTime.now()); p.setProtection(false);
                        Picture added = pictureService.addPicture(p);
                        logger.info("Added new {} Picture {}:\"{}\"", (added.isProtection()?"protected":"unprotected"), added.getId(), added.getPath());
                    }
                }
            }
            if (upToDate) {
                logger.info("All the files are up to date");
            }
        }
    }
}
