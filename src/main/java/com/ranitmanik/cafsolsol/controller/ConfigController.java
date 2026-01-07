package com.ranitmanik.cafsolsol.controller;

import com.ranitmanik.cafsolsol.model.ConfigSection;
import com.ranitmanik.cafsolsol.service.ConfigParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Autowired
    private ConfigParser configParser;

    private volatile boolean configLoaded = false;

    @PostMapping("/load")
    public ResponseEntity<String> loadConfig() {
        try {
            ClassPathResource resource = new ClassPathResource("config.txt");
            configParser.parseConfigFile(resource.getFile().getAbsolutePath());
            configLoaded = true;
            return ResponseEntity.ok("Configuration loaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error loading configuration: " + e.getMessage());
        }
    }

    @GetMapping("/section/{sectionName}")
    public ResponseEntity<?> getConfigSection(@PathVariable String sectionName) {
        if (!configLoaded) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Configuration not loaded. Please call /api/config/load first");
        }

        ConfigSection section = configParser.getConfigSection(sectionName);
        if (section == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Section '" + sectionName + "' not found");
        }

        Map<String, Object> response = new HashMap<>();
        response.putAll(section.getProperties());
        response.putAll(section.getArrayProperties());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sections")
    public ResponseEntity<?> getAllSections() {
        if (!configLoaded) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Configuration not loaded. Please call /api/config/load first");
        }

        Map<String, Object> response = new HashMap<>();
        for (Map.Entry<String, ConfigSection> entry : configParser.getAllSections().entrySet()) {
            ConfigSection section = entry.getValue();
            Map<String, Object> sectionData = new HashMap<>();
            sectionData.putAll(section.getProperties());
            sectionData.putAll(section.getArrayProperties());
            response.put(entry.getKey(), sectionData);
        }

        return ResponseEntity.ok(response);
    }
}
