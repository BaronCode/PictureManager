package com.picman.picman.SpringSettings;

import com.picman.picman.PicturesMgmt.Picture;
import com.picman.picman.PicturesMgmt.PictureBuilder;
import com.picman.picman.PicturesMgmt.PictureServiceImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
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
    public void updateImageDatabase() {
        File superDir = new File(Settings.get("output"));
        boolean upToDate = true;
        if (superDir.isDirectory()) {
            File[] fileList = superDir.listFiles();
            if (fileList != null) {
                List<Picture> all = pictureService.findAll();
                for (File f : fileList) {
                    if (f.isFile()) {
                        //searches db for entries with path equal to filename, if none is found, saves new entry
                        long i = all.stream().filter(p -> p.getPath().equals(f.getName().split("\\.")[0])).count();
                        if (i == 0) {
                            upToDate = false;
                            logger.warn("Detected change in {}, adding to database image {}", Settings.get("output"), f.getAbsolutePath());
                            Picture p = PictureBuilder.buildByFile(f, false, null);
                            Picture added = pictureService.addPicture(p);
                            logger.info("Added new {} Picture {}:\"{}\"", (added.isProtection() ? "protected" : "unprotected"), added.getId(), added.getPath());
                        }
                    }
                }
            }
            if (upToDate) {
                logger.info("All the files are up to date");
            }
        }
    }
}
