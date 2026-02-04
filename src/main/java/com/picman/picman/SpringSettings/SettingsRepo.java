package com.picman.picman.SpringSettings;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class SettingsRepo {

    private final JdbcTemplate jdbc;

    public SettingsRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map<String, String> findAll() {
        return jdbc.query(
                "SELECT key, value FROM settings",
                rs -> {
                    Map<String, String> map = new HashMap<>();
                    while (rs.next()) {
                        map.put(rs.getString("key"), rs.getString("value"));
                    }
                    return map;
                }
        );
    }

    public void update(String key, String value) {
        jdbc.update("UPDATE settings SET value = ? WHERE key = ?", value, key);
    }
}
