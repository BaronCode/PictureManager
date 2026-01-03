package com.picman.picman.SpringSettings;

import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public final class PicmanSettings {
    private String defaultFileOutput;
    private String defaultOrganizationName;
    private int superAdminID;
    private Set<Integer> technicalUsersIDs;

    public PicmanSettings() {
        parseSettings();
    }

    private void parseSettings() {
        try {
            File settings = new File(new File("src/main/java/com/picman/picman/SpringSettings/picmansettings.pman").getAbsolutePath());
            Scanner sc = new Scanner(settings);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (!line.startsWith(":::")) {
                    if (line.startsWith("%")) {
                        String[] opclass = line.substring(1).split("=");
                        switch (opclass[0]) {
                            case "defaultFileOutput" -> defaultFileOutput = opclass[1];
                            case "organizationName" -> defaultOrganizationName = opclass[1];
                            case "superAdminID" -> superAdminID = Integer.parseInt(opclass[1]);
                            case "registeredSupportIDs" -> {
                                technicalUsersIDs = new HashSet<>();
                                Set<String> as = Arrays.stream(opclass[1].split(",")).collect(Collectors.toSet());
                                as.forEach(i->technicalUsersIDs.add(Integer.parseInt(i)));
                            }
                            default -> throw new InvalidPicmanSettingException("Unrecognized setting");
                        }
                    } else throw new InvalidPicmanSettingException("Invalid line");
                }
            }
        } catch (FileNotFoundException fnf) {
            System.out.println(fnf.getMessage());
        }
    }

    public static class InvalidPicmanSettingException extends RuntimeException {
        public InvalidPicmanSettingException(String cause) {
            super(cause);
        }
    }
    public static class PicmanSettingsDiscrepancyException extends IllegalStateException {
        public PicmanSettingsDiscrepancyException(String cause) {
            super(cause);
        }
    }
}
