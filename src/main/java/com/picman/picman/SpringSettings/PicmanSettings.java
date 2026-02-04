package com.picman.picman.SpringSettings;

import com.picman.picman.Exceptions.InvalidPicmanSettingException;
import com.picman.picman.Exceptions.PicmanSettingsNotFoundException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Getter @Slf4j @Deprecated
public final class PicmanSettings {
    private String defaultFileOutput;
    private String defaultOrganizationName;
    private int superAdminID;
    private Set<Integer> technicalUsersIDs;
    private final Logger logger = LoggerFactory.getLogger(PicmanSettings.class);

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
                            case "defaultFileOutput" -> {
                                defaultFileOutput = opclass[1];
                                File dfa = new File(defaultFileOutput);
                                if (!dfa.isDirectory() || !dfa.exists()) {
                                    logger.error("[FATAL] Default file output folder {} does not exist!", defaultFileOutput);
                                    //throw new InvalidPicmanSettingException("[FATAL] Default file output folder " + defaultFileOutput + " does not exist!");
                                }
                            }
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
            throw new PicmanSettingsNotFoundException("File picmansettings.pman not found!");
        }
    }
}
