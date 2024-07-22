package com.hh.consertreservation.support.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ParserUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String extractValueFromJson(String json, String key) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            return jsonNode.path(key).asText();
        } catch (IOException e) {
            log.error(e.getMessage());
            return "";
        }
    }
}
