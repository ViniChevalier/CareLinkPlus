package com.carelink.config;

import com.carelink.exception.BusinessLogicException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/maps-key")
    public ResponseEntity<String> getGoogleMapsApiKey() {
        String apiKey = System.getenv("GOOGLE_MAPS_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessLogicException("Google Maps API key not configured.");
        }
        return ResponseEntity.ok(apiKey);
    }
}
