package com.picman.picman.Exceptions;

/**
 * Responds to a generic problem in the picmansettings.pman file.
 * @see com.picman.picman.SpringSettings.PicmanSettings
 */
public class InvalidPicmanSettingException extends RuntimeException {
    public InvalidPicmanSettingException(String cause) {
        super(cause);
    }
}