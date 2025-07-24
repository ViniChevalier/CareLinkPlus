package com.carelink.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @GetMapping("/maps-key")
    public ResponseEntity<String> getGoogleMapsApiKey() {
        String apiKey = System.getenv("GOOGLE_MAPS_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Google Maps API key not configured.");
        }
        return ResponseEntity.ok(apiKey);
    }
}
