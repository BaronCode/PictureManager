package com.picman.picman.PicturesMgmt;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.picman.picman.Exceptions.ImageProcessingException;
import com.picman.picman.SpringSettings.Settings;
import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;

public class Thumbs {
    public static void saveThumb(File original, Void v) {
        try {
            int extension = original.getName().split("\\.").length;
            BufferedImage src = ImageIO.read(original);
            BufferedImage thumb = Scalr.resize(src, 250);
            ImageIO.write(thumb, original.getName().split("\\.")[extension-1], new File(Settings.get("th_output").concat(original.getName())));
        } catch (IOException e) {
            throw new ImageProcessingException("Error while processing image thumbnail");
        }
    }

    public static void ajaja(File original) {
        DataInputStream dis;
        try {
            dis = new DataInputStream(new BufferedInputStream(new FileInputStream(original)));
            byte[] data = IOUtils.toByteArray(dis);
            int extension = original.getName().split("\\.").length;
            BufferedImage src = ImageIO.read(new ByteArrayInputStream(data));

            Metadata metadata = ImageMetadataReader.readMetadata(original);

            int orientation = 1;
            ExifIFD0Directory ifd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            if (ifd0 != null && ifd0.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                orientation = ifd0.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            }

            src = applyOrientation(src, orientation);

            BufferedImage thumb = Scalr.resize(src, 250);
            ImageIO.write(thumb, original.getName().split("\\.")[extension - 1], new File("/mnt/drive166gb/picmanph/thumbs/".concat(original.getName())));
        } catch (IOException | com.drew.imaging.ImageProcessingException | MetadataException e) {
            throw new ImageProcessingException("Error while processing image thumbnail");
        }
    }

    public static void saveThumb(File original) {
        DataInputStream dis;
        try {
            dis = new DataInputStream(new BufferedInputStream(new FileInputStream(original)));
            byte[] data = IOUtils.toByteArray(dis);
            int extension = original.getName().split("\\.").length;
            BufferedImage src = ImageIO.read(new ByteArrayInputStream(data));

            Metadata metadata = ImageMetadataReader.readMetadata(original);

            int orientation = 1;
            ExifIFD0Directory ifd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            if (ifd0 != null && ifd0.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                orientation = ifd0.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            }

            src = applyOrientation(src, orientation);

            BufferedImage resized = Scalr.resize(
                    src,
                    Scalr.Method.QUALITY,
                    Scalr.Mode.FIT_TO_WIDTH,
                    250
            );

            if (resized.getHeight() < 250) {
                resized = Scalr.resize(
                        src,
                        Scalr.Method.QUALITY,
                        Scalr.Mode.FIT_TO_HEIGHT,
                        250
                );
            }

            BufferedImage thumb = Scalr.crop(
                    resized,
                    (resized.getWidth() - 250) / 2,
                    (resized.getHeight() - 250) / 2,
                    250,
                    250
            );

            ImageIO.write(thumb, original.getName().split("\\.")[extension-1], new File("/mnt/drive166gb/picmanph/thumbs/".concat(original.getName())));
        } catch (IOException | com.drew.imaging.ImageProcessingException | MetadataException e) {
            throw new ImageProcessingException("Error while processing image thumbnail");
        }
    }

    public static void deleteThumb(String fname) {
        File image = new File(Settings.get("th_output").concat(fname));
        image.delete();
    }

    private static BufferedImage applyOrientation(BufferedImage img, int orientation) {
        AffineTransform tx = new AffineTransform();

        switch (orientation) {
            case 3 -> tx.rotate(Math.PI, img.getWidth() / 2.0, img.getHeight() / 2.0);
            case 6 -> {
                tx.rotate(Math.PI / 2, img.getWidth() / 2.0, img.getHeight() / 2.0);
                tx.translate(0, -img.getHeight());
            }
            case 8 -> {
                tx.rotate(-Math.PI / 2, img.getWidth() / 2.0, img.getHeight() / 2.0);
                tx.translate(-img.getWidth(), 0);
            }
            default -> {
                return img; // orientation = 1
            }
        }

        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BICUBIC);
        return op.filter(img, null);
    }

}
