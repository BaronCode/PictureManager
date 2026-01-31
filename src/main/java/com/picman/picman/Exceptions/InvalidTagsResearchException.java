package com.picman.picman.Exceptions;


public class InvalidTagsResearchException extends FieldNotFoundException {
    public InvalidTagsResearchException(String cause) {
        super(cause);
    }
}