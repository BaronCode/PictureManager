package com.picman.picman.Exceptions;

/**
 * Responds exclusively to an error happened while processing an image that's being uploaded.
 * Can also fire if something happens while processing a zip file.
 * @see com.picman.picman.Endpoints.ImageEdit
 */
public class ImageProcessingException extends IllegalStateException {
    public ImageProcessingException(String cause) {
        super(cause);
    }
}
