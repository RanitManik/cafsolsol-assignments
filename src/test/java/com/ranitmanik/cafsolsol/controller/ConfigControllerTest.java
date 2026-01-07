package com.ranitmanik.cafsolsol.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.ranitmanik.cafsolsol.model.ConfigSection;
import com.ranitmanik.cafsolsol.service.ConfigParser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Config Controller Tests")
class ConfigControllerTest {

    private ConfigParser configParser;
    private File testConfigFile;

    @BeforeEach
    void setUp() throws IOException {
        configParser = new ConfigParser();
        testConfigFile = File.createTempFile("test_config", ".txt");
        testConfigFile.deleteOnExit();

        // Create test config
        String configContent =
                "Gateway\n"
                        + "endpoint = https://xyz.in\n"
                        + "certurl = https://cloud.internalportal.com\n"
                        + "download loc =  /home/user/temp\n"
                        + "\n"
                        + "CXO\n"
                        + "endpont = http://internal.cxo.com\n"
                        + "broker = http://cxobroker.in\n"
                        + "topic = test_cxo_topic, test_cxo_topic_1\n"
                        + "\n"
                        + "Order Service\n"
                        + "broker = https://orbroker.in\n"
                        + "topic = test_os_topic_1, test_os_topic_2";

        writeConfigToFile(configContent);
        configParser.parseConfigFile(testConfigFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Should load Gateway section successfully")
    void testLoadGatewaySection() {
        ConfigSection section = configParser.getConfigSection("Gateway");
        assertNotNull(section);
        assertEquals("https://xyz.in", section.getProperties().get("endpoint"));
    }

    @Test
    @DisplayName("Should load Order Service section with topics array")
    void testLoadOrderServiceSection() {
        ConfigSection section = configParser.getConfigSection("Order Service");
        assertNotNull(section);
        assertEquals("https://orbroker.in", section.getProperties().get("broker"));
        assertEquals(2, section.getArrayProperties().get("topic").size());
    }

    @Test
    @DisplayName("Should load CXO section with topics array")
    void testLoadCXOSection() {
        ConfigSection section = configParser.getConfigSection("CXO");
        assertNotNull(section);
        assertEquals("http://cxobroker.in", section.getProperties().get("broker"));
        assertEquals(2, section.getArrayProperties().get("topic").size());
    }

    @Test
    @DisplayName("Should return null for non-existent section")
    void testNonExistentSection() {
        ConfigSection section = configParser.getConfigSection("NonExistent");
        assertNull(section);
    }

    @Test
    @DisplayName("Should retrieve all sections")
    void testGetAllSections() {
        assertEquals(3, configParser.getAllSections().size());
        assertTrue(configParser.sectionExists("Gateway"));
        assertTrue(configParser.sectionExists("CXO"));
        assertTrue(configParser.sectionExists("Order Service"));
    }

    private void writeConfigToFile(String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testConfigFile))) {
            writer.write(content);
        }
    }
}
