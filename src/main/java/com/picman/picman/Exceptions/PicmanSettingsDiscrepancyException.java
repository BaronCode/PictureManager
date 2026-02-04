package com.picman.picman.Exceptions;

/**
 * Responds to a mismatch in picmansettings.pman file and saved data.
 * @see com.picman.picman.SpringSettings.PicmanSettings
 */
public class PicmanSettingsDiscrepancyException extends IllegalStateException {
    public PicmanSettingsDiscrepancyException(String cause) {
        super(cause);
    }
}
