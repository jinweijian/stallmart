package com.stallmart.support.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class JsonSupport {

    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };
    private static final TypeReference<List<Long>> LONG_LIST = new TypeReference<>() {
    };
    private static final TypeReference<Map<String, String>> STRING_MAP = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;

    public JsonSupport(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<String> stringList(String json) {
        return read(json, STRING_LIST, List.of());
    }

    public List<Long> longList(String json) {
        return read(json, LONG_LIST, List.of());
    }

    public Map<String, String> stringMap(String json) {
        return read(json, STRING_MAP, Map.of());
    }

    public <T> T read(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception exception) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
    }

    public String write(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
    }

    private <T> T read(String json, TypeReference<T> type, T fallback) {
        if (json == null || json.isBlank()) {
            return fallback;
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception exception) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
    }
}
