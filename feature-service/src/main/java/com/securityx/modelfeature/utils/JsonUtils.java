package com.securityx.modelfeature.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class JsonUtils {
    private static ObjectMapper mapper = new ObjectMapper();
    private static Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

    public static <T> Optional<T> getObjectFromJson(String str, Class<T> classOfT) {
        try {
            return Optional.of(mapper.readValue(str, classOfT));
        } catch (IOException e) {
            LOGGER.error("Error reading json-string => " + e);
        }
        return Optional.empty();
    }

    public static <T> Optional<T> getObjectFromJson(byte[] arr, Class<T> classOfT){
        try {
            return Optional.of(mapper.readValue(arr, classOfT));
        } catch (IOException e) {
            LOGGER.error("Error reading json byte-array => " + e);
        }
        return Optional.empty();
    }

    public static Optional<String> toJsonString(Object src) {
        ObjectWriter objectWriter = mapper.writer();
        try {
                return Optional.of(objectWriter.writeValueAsString(src));
        } catch (JsonProcessingException e) {
            LOGGER.error("Error converting to json => " + e);
        }
        return Optional.empty();
    }
}
