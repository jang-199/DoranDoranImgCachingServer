package com.dorandoran.imgCachingServer.global.util;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class ResponseUtil {
    List<Map<String, ?>> makeResponseDto(HttpStatus httpStatus, String message, Object data) {
        Map<String, Integer> codeMap = Map.of("code",httpStatus.value());
        Map<String, String> messageMap = Map.of("message", message);
        Map<String, Object> dataMap = Map.of("data", data);

        return List.of(codeMap, messageMap, dataMap);
    }
}
