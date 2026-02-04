package com.picman.picman.SpringSettings;

import org.springframework.stereotype.Component;

@Component
public class Settings {
    private static SettingsService settings;

    public Settings(SettingsService ss) {
        settings = ss;
    }

    public static String get(String key) {
        return settings.get(key);
    }

    public static void update(String key, String value) {
        settings.update(key, value);
    }
}
