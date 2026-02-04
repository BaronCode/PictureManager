package com.picman.picman.SpringSettings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SettingsService {

    private ObjectMapper mapper;
    private Map<String, String> cache;
    private final SettingsRepo repository;

    public SettingsService(SettingsRepo repo) {
        repository = repo;
    }

    @PostConstruct
    void load() {
        cache = repository.findAll();
    }

    public String get(String key) {
        return cache.get(key);
    }

    public List<String> getList(String key) throws JsonProcessingException {
        return mapper.readValue(
                cache.get(key),
                new TypeReference<>() {}
        );
    }

    public void update(String key, String value) {
        repository.update(key, value);
    }

    public void reload() {
        cache = repository.findAll();
    }
}
