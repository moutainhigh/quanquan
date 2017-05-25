package com.circle.core.elastic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Created by cxx on 15-7-7.
 */
@SuppressWarnings("unused")
public class Json {
    private static ObjectMapper objectMapper = null;
    private static Logger logger = LoggerFactory.getLogger(Json.class);

    static {
        objectMapper = new ObjectMapper();
    }

    /**
     * this is parse one object to json string.
     */
    public static String json(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            logger.error("json errot", e);
            return null;
        }
    }

    public static Object jsonParser(String json, Class<?> cla) {
        try {
            return objectMapper.readValue(json, cla);
        } catch (IOException e) {
            logger.error("jsonParser errot", e);
        }
        return null;
    }

    public static JsonNode jsonParser(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            logger.error("JsonReader errot", e);
            return null;
        }
    }
}
