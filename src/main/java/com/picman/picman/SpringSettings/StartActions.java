package com.picman.picman.SpringSettings;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.picman.picman.PicturesMgmt.Picture;
import com.picman.picman.PicturesMgmt.PictureBuilder;
import com.picman.picman.PicturesMgmt.PictureServiceImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
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

    //@EventListener(ApplicationReadyEvent.class)
    public void generateThumbs() throws IOException {
        File in = new File("/mnt/drive166gb/picmanph/");
        File out = new File("/mnt/drive166gb/picmanph/thumbs/");
        out.mkdirs();

        for (File f : in.listFiles()) {
            if (!f.isFile()) continue;

            BufferedImage src = ImageIO.read(f);
            if (src == null) continue;

            int orientation = 1;
            try {
                Metadata m = ImageMetadataReader.readMetadata(f);
                ExifIFD0Directory d = m.getFirstDirectoryOfType(ExifIFD0Directory.class);
                if (d != null && d.containsTag(ExifIFD0Directory.TAG_ORIENTATION))
                    orientation = d.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            } catch (Exception ignored) {}

            BufferedImage img = orient(src, orientation);
            int w = img.getWidth();
            int h = img.getHeight();

            BufferedImage result;

            if (h > w) {
                int y = (h - w) / 2;
                BufferedImage cropped = img.getSubimage(0, y, w, w);
                result = resizeExact(cropped, 250, 250);
            } else {
                int nh = (int) ((double) h / w * 250);
                result = resizeExact(img, 250, nh);
            }

            BufferedImage rgb = new BufferedImage(
                    result.getWidth(),
                    result.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g = rgb.createGraphics();
            g.drawImage(result, 0, 0, null);
            g.dispose();

            String name = f.getName();
            int dot = name.lastIndexOf('.');
            String ext = dot > 0 ? name.substring(dot + 1).toLowerCase() : "jpg";
            if (!hasWriter(ext)) ext = "jpg";

            ImageIO.write(rgb, ext, new File(out, name));
        }
    }

    BufferedImage resizeExact(BufferedImage src, int w, int h) {
        BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dst.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return dst;
    }

    BufferedImage orient(BufferedImage img, int o) {
        int w = img.getWidth(), h = img.getHeight();
        AffineTransform tx = new AffineTransform();
        int nw = w, nh = h;

        switch (o) {
            case 3 -> { tx.translate(w, h); tx.rotate(Math.PI); }
            case 6 -> { tx.translate(h, 0); tx.rotate(Math.PI / 2); nw = h; nh = w; }
            case 8 -> { tx.translate(0, w); tx.rotate(-Math.PI / 2); nw = h; nh = w; }
            default -> { return img; }
        }

        BufferedImage dst = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dst.createGraphics();
        g.drawImage(img, tx, null);
        g.dispose();
        return dst;
    }

    boolean hasWriter(String ext) {
        Iterator<?> it = ImageIO.getImageWritersByFormatName(ext);
        return it.hasNext();
    }
}