package com.saveethageotag.utils;

import com.saveethageotag.driver.DriverManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ApiUtility {
    private static final Logger logger = LogManager.getLogger(ApiUtility.class);

    public static int getResponseStatusCode(String url) {
        logger.info("Executing API GET health check on URL: {}", url);
        if (DriverManager.isMock()) {
            logger.info("Simulation Mode: Simulating API status code. Returning 200 OK");
            return 200;
        }

        try {
            Response response = RestAssured.get(url);
            return response.getStatusCode();
        } catch (Exception e) {
            logger.error("API check failed: {}", e.getMessage());
            return 500;
        }
    }

    public static Map<String, Object> postJsonRequest(String url, Map<String, Object> body) {
        logger.info("Executing API POST request on: {} with body keys: {}", url, body.keySet());
        if (DriverManager.isMock()) {
            logger.info("Simulation Mode: Simulating API POST. Returning mocked JSON payload.");
            Map<String, Object> mockResponse = new HashMap<>();
            mockResponse.put("status", "success");
            mockResponse.put("id", "GP-12345");
            mockResponse.put("code", 200);
            return mockResponse;
        }

        try {
            Response response = RestAssured.given()
                    .contentType("application/json")
                    .body(body)
                    .post(url);
            return response.as(Map.class);
        } catch (Exception e) {
            logger.error("API POST request failed: {}", e.getMessage());
            return new HashMap<>();
        }
    }
}
