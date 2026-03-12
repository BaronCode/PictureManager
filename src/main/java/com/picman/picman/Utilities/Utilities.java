package com.picman.picman.Utilities;

import com.picman.picman.Exceptions.InvalidFormParamException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class Utilities {
    public static final class Constants {
        public static final Set<Character> privileges = new LinkedHashSet<>(List.of('o', 'u', 's', 'w', 'd', 'r'));
    }

    private static final Logger logger = LoggerFactory.getLogger(Utilities.class);

    public static int testUserID(String toBeTested) throws InvalidFormParamException {
        int id;
        try {
            id = Integer.parseInt(toBeTested);
        } catch (NumberFormatException nfe) {
            throw new InvalidFormParamException("User ID not valid");
        }
        return id;
    }
    public static String testUsername(String toBeTested) throws InvalidFormParamException {
        if (toBeTested == null) {
            throw new InvalidFormParamException("Username cannot be null");
        }
        if (toBeTested.isBlank() || toBeTested.length()>30) {
            throw new InvalidFormParamException("Username not valid");
        }
        if (!toBeTested.matches("^[A-Za-z0-9]*$")) {
            throw new InvalidFormParamException("Username contains illegal characters");
        }
        return toBeTested;
    }
    public static String testEmail(String toBeTested) throws InvalidFormParamException {
        if (toBeTested == null) {
            throw new InvalidFormParamException("Email cannot be null");
        }
        if (toBeTested.isBlank() || toBeTested.length()>255) {
            throw new InvalidFormParamException("Email not valid");
        }
        if (!toBeTested.matches("^[A-Za-z0-9._-]{5,254}@[A-Za-z0-9._-]{5,254}$") || toBeTested.contains("..")) {
            throw new InvalidFormParamException("Username contains illegal characters");
        }
        return toBeTested;
    }
    public static char testPrivilege(String toBeTested) throws InvalidFormParamException {
        if (toBeTested == null) {
            throw new InvalidFormParamException("Role cannot be null");
        }
        if (toBeTested.isBlank() || toBeTested.length()>1 || !toBeTested.matches("^[a-z]$")) {
            throw new InvalidFormParamException("Role not valid");
        }
        char toBeTestedChar = toBeTested.toCharArray()[0];
        if (!Constants.privileges.contains(toBeTestedChar)) {
            throw new InvalidFormParamException("Role does not exist");
        }
        return toBeTestedChar;
    }
    public static String testOrganization(String toBeTested) throws InvalidFormParamException {
        if (toBeTested == null) {
            throw new InvalidFormParamException("Organization cannot be null");
        }
        if (toBeTested.isBlank() || toBeTested.length()>128) {
            throw new InvalidFormParamException("Organization not valid");
        }
        if (!toBeTested.matches("^[A-Za-z0-9.@_-]{5,128}$")) {
            throw new InvalidFormParamException("Username contains illegal characters");
        }
        return toBeTested;
    }
    public static boolean testGenericBoolean(String toBeTested) throws InvalidFormParamException {
        if (toBeTested == null) {
            throw new InvalidFormParamException("Assignment cannot be null");
        }
        if (!toBeTested.equalsIgnoreCase("true") && !toBeTested.equalsIgnoreCase("false")) {
            throw new InvalidFormParamException("Assignment not valid");
        }
        return Boolean.parseBoolean(toBeTested);
    }

}
