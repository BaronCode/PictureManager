package com.picman.picman.Exceptions;

/**
 * Responds to a configuration value in picmansettings.pman not being recognized by the program.
 * @see com.picman.picman.SpringSettings.PicmanSettings
 */
public class PicmanSettingsNotFoundException extends RuntimeException {
    public PicmanSettingsNotFoundException(String cause) { super(cause); }
}