package com.picman.picman.PicturesMgmt;

import com.picman.picman.Exceptions.ImageProcessingException;
import com.picman.picman.SpringSettings.Settings;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Thumbs {
    public static void saveThumb(File original) {
        try {
            int extension = original.getName().split("\\.").length;
            BufferedImage src = ImageIO.read(original);
            BufferedImage thumb = Scalr.resize(src, 250);
            ImageIO.write(thumb, original.getName().split("\\.")[extension-1], new File(Settings.get("th_output").concat(original.getName())));
        } catch (IOException e) {
            throw new ImageProcessingException("Error while processing image thumbnail");
        }
    }

    public static void deleteThumb(String fname) {
        File image = new File(Settings.get("th_output").concat(fname));
        image.delete();
    }
}
