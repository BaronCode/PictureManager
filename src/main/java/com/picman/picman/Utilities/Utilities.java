package com.picman.picman.Utilities;

import com.picman.picman.Exceptions.FieldNotFoundException;
import com.picman.picman.PicturesMgmt.Picture;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@Slf4j
public class Utilities {
    private static final Logger logger = LoggerFactory.getLogger(Utilities.class);

    public static boolean p_deletable(Picture p, Set<Character> privileges) {
        return !p.isProtection() || privileges.contains('o');
    }
    public static boolean checkPermissions(Set<Character> privileges, char[] contains) {
        for (char c : contains) {
            if (privileges.contains(c)) {
                return true;
            }
        }
        return false;
    }
    public static boolean p_nexists(Picture p, long id) throws FieldNotFoundException {
        if (p == null) {
            logger.error("Picture with id {} does not exist!", id);
            return true;
        }
        return false;
    }
}
