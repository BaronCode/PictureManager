package com.picman.picman.PicturesMgmt;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
public class PictureBuilder {
    private static final Logger logger = LoggerFactory.getLogger(PictureBuilder.class);

    public static Picture buildByFile(File f, boolean savingNew, String directory, PictureServiceImplementation ps) throws ImageProcessingException {
        Picture p;
        byte[] fname;
        MessageDigest sha256;

        try {
            sha256 = MessageDigest.getInstance("SHA-256");
            fname = sha256.digest(Files.readAllBytes(f.toPath()));
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.error("Something went wrong while hashing file");
            return null;
        }

        String substring = f.getName().substring(f.getName().lastIndexOf('.') + 1).toLowerCase();
        String name = String.format("%064x", new BigInteger(1, fname));

        if (ps.existsByPath(name)) {
            if (f.delete()) {
                return null;
            } else throw new ImageProcessingException("Error while processing image " + name);

        }

        // Generic attributes setting
        p = new Picture();
        p.setPath(String.format("%064x", new BigInteger(1, fname)));
        p.setExt(substring);
        p.setDateadded(LocalDateTime.now());
        p.setProtection(false);
        p.setSizekb(new BigDecimal(Math.round((float) f.length() / 1024)));

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(f);
            ExifIFD0Directory ifd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            ExifSubIFDDirectory subifd = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            BufferedImage buff = ImageIO.read(f);
            if (ifd0 == null || subifd == null) {
                // Image data fallback mechanism
                p.setWidth(buff.getWidth());
                p.setHeight(buff.getHeight());

            } else {
                // Full metadata initialization
                p.setShotat(subifd.getDateOriginal().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                p.setHeight(
                        subifd.getInteger(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT) != null ?
                                subifd.getInteger(ExifIFD0Directory.TAG_EXIF_IMAGE_HEIGHT) :
                                buff.getHeight()
                );
                p.setWidth(
                        subifd.getInteger(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH) != null ?
                                subifd.getInteger(ExifIFD0Directory.TAG_EXIF_IMAGE_WIDTH) :
                                buff.getWidth()
                );
                p.setAperture(new BigDecimal(subifd.getDoubleObject(ExifSubIFDDirectory.TAG_FNUMBER)));
                p.setIso(subifd.getInteger(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
                p.setExposurenum(Math.toIntExact(subifd.getRational(ExifSubIFDDirectory.TAG_EXPOSURE_TIME).getNumerator()));
                p.setExposureden(Math.toIntExact(subifd.getRational(ExifSubIFDDirectory.TAG_EXPOSURE_TIME).getDenominator()));
                p.setFocallength(subifd.getInteger(ExifSubIFDDirectory.TAG_FOCAL_LENGTH).shortValue());
                p.setCameramodel(ifd0.getString(ExifIFD0Directory.TAG_MODEL));
            }
        } catch (NullPointerException | ImageProcessingException | IOException imr) {
            logger.warn("Could not read metadata of file {}, leaving null metadata values", f.getName());
        } finally {
            if (savingNew && directory != null) {
                File g = new File(
                        directory
                                .concat(p.getPath())
                                .concat(".")
                                .concat(p.getExt())
                );
                if (f.renameTo(g)) {
                    Thumbs.saveThumb(g);
                }

            }
        }
        return p;
    }
}
