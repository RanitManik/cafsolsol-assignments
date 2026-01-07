package com.ranitmanik.cafsolsol.service;

import com.ranitmanik.cafsolsol.model.ConfigSection;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConfigParser {

    private Map<String, ConfigSection> configSections = new HashMap<>();

    public void parseConfigFile(String filePath) throws IOException {
        configSections.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            ConfigSection currentSection = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }

                // Check if line is a section header (contains no '=' sign)
                if (!line.contains("=")) {
                    currentSection = new ConfigSection(line);
                    configSections.put(line, currentSection);
                } else if (currentSection != null) {
                    // Parse property line
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();

                        // Check if value contains commas (array property)
                        if (value.contains(",")) {
                            List<String> values = Arrays.stream(value.split(","))
                                    .map(String::trim)
                                    .collect(Collectors.toList());
                            currentSection.addArrayProperty(key, values);
                        } else {
                            currentSection.addProperty(key, value);
                        }
                    }
                }
            }
        }
    }

    public ConfigSection getConfigSection(String sectionName) {
        return configSections.get(sectionName);
    }

    public Map<String, ConfigSection> getAllSections() {
        return new HashMap<>(configSections);
    }

    public boolean sectionExists(String sectionName) {
        return configSections.containsKey(sectionName);
    }
}
