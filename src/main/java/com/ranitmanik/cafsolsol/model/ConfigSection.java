package com.ranitmanik.cafsolsol.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigSection {
    private String sectionName;
    private Map<String, String> properties;
    private Map<String, List<String>> arrayProperties;

    public ConfigSection(String sectionName) {
        this.sectionName = sectionName;
        this.properties = new HashMap<>();
        this.arrayProperties = new HashMap<>();
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void addProperty(String key, String value) {
        this.properties.put(key, value);
    }

    public Map<String, List<String>> getArrayProperties() {
        return arrayProperties;
    }

    public void addArrayProperty(String key, List<String> values) {
        this.arrayProperties.put(key, values);
    }
}
