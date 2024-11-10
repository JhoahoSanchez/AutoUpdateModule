package com.sideralsoft.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonUtils() {
    }

    public static ObjectMapper getMapper() {
        return objectMapper;
    }

    public static String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public static <T> T fromJson(String json, Class<T> clase) throws JsonProcessingException {
        return objectMapper.readValue(json, clase);
    }

    public static <T> List<T> fromJsonToList(String json, Class<T> clase) throws JsonProcessingException {
        return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clase));
    }

    public static <T> T fromJsonFile(File file, Class<T> clase) throws IOException {
        return objectMapper.readValue(file, clase);
    }

    public static <T> List<T> fromJsonFileToList(File file, Class<T> clazz) throws IOException {
        return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    public static JsonNode parseJson(String json) throws JsonProcessingException {
        return objectMapper.readTree(json);
    }

    public static <T> T fromJsonNode(JsonNode node, Class<T> clase) throws JsonProcessingException {
        return objectMapper.treeToValue(node, clase);
    }

    public static void toJsonFile(Object object, File file) throws IOException {
        objectMapper.writeValue(file, object);
    }

}
