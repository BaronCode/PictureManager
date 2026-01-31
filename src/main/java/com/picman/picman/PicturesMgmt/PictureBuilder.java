package com.picman.picman.PicturesMgmt;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.picman.picman.SpringSettings.PicmanSettings;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
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

    public static Picture buildByFile(File f, boolean savingNew) {
        Picture p = null;
        byte[] fname = new byte[]{0};
        MessageDigest sha256;

        try {
            sha256 = MessageDigest.getInstance("SHA-256");
            fname = sha256.digest(Files.readAllBytes(f.toPath()));
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.error("Something went wrong while hashing file");
        }

        String substring = f.getName().substring(f.getName().lastIndexOf('.') + 1).toLowerCase();
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(f);
            ExifIFD0Directory ifd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            ExifSubIFDDirectory subifd = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            p = new Picture(
                    String.format("%064x", new BigInteger(1, fname)),
                    substring,
                    LocalDateTime.now(),
                    false,
                    new BigDecimal(Math.round((float) f.length() /1024)),
                    subifd.getDateOriginal().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    subifd.getInteger(ExifIFD0Directory.TAG_EXIF_IMAGE_HEIGHT),
                    subifd.getInteger(ExifIFD0Directory.TAG_EXIF_IMAGE_WIDTH),
                    new BigDecimal(subifd.getDoubleObject(ExifSubIFDDirectory.TAG_FNUMBER)),
                    subifd.getInteger(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT),
                    Math.toIntExact(subifd.getRational(ExifSubIFDDirectory.TAG_EXPOSURE_TIME).getNumerator()),
                    Math.toIntExact(subifd.getRational(ExifSubIFDDirectory.TAG_EXPOSURE_TIME).getDenominator()),
                    subifd.getInteger(ExifSubIFDDirectory.TAG_FOCAL_LENGTH).shortValue(),
                    ifd0.getString(ExifIFD0Directory.TAG_MODEL)
            );

        } catch (NullPointerException | ImageProcessingException | IOException imr) {
            logger.warn("Could not read metadata of file {}, falling back to null metadata values", f.getName());

            p = new Picture();
            p.setPath(String.format("%064x", new BigInteger(1, fname)));
            p.setExt(substring);
            p.setDateadded(LocalDateTime.now());
            p.setProtection(false);

        } finally {
            if (savingNew) {
                File g = new File(
                        new PicmanSettings().getDefaultFileOutput()
                                .concat(File.separator)
                                .concat(p.getPath())
                                .concat(".")
                                .concat(p.getExt())
                );
                f.renameTo(g);
            }
        }
        return p;
    }
}
