package com.picman.picman.UserMgmt;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class PrivilegesConverter implements AttributeConverter<Set<Character>, String> {

    @Override
    public String convertToDatabaseColumn(Set<Character> attribute) {
        if (attribute == null || attribute.isEmpty()) return "";
        return attribute.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(""));
    }

    @Override
    public Set<Character> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return Set.of();
        return dbData.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toSet());
    }
}