package com.ranitmanik.cafsolsol.service;

import static org.junit.jupiter.api.Assertions.*;

import com.ranitmanik.cafsolsol.model.ConfigSection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ConfigParser Tests")
class ConfigParserTest {

    private ConfigParser configParser;
    private File testConfigFile;

    @BeforeEach
    void setUp() throws IOException {
        configParser = new ConfigParser();
        // Create a temporary test config file
        testConfigFile = File.createTempFile("test_config", ".txt");
        testConfigFile.deleteOnExit();
    }

    @Test
    @DisplayName("Should parse configuration file with sections and properties")
    void testParseConfigFile() throws IOException {
        // Arrange
        String configContent =
                "Gateway\n"
                        + "endpoint = https://xyz.in\n"
                        + "certurl = https://cloud.internalportal.com\n"
                        + "download loc =  /home/user/temp\n"
                        + "\n"
                        + "CXO\n"
                        + "endpont = http://internal.cxo.com\n"
                        + "redirect url = \n"
                        + "broker = http://cxobroker.in\n"
                        + "topic = test_cxo_topic, test_cxo_topic_1\n"
                        + "\n"
                        + "Order Service\n"
                        + "broker = https://orbroker.in\n"
                        + "topic = test_os_topic_1, test_os_topic_2";

        writeConfigToFile(configContent);

        // Act
        configParser.parseConfigFile(testConfigFile.getAbsolutePath());

        // Assert
        assertTrue(configParser.sectionExists("Gateway"));
        assertTrue(configParser.sectionExists("CXO"));
        assertTrue(configParser.sectionExists("Order Service"));
    }

    @Test
    @DisplayName("Should retrieve Gateway section properties")
    void testGetGatewaySection() throws IOException {
        // Arrange
        String configContent =
                "Gateway\n"
                        + "endpoint = https://xyz.in\n"
                        + "certurl = https://cloud.internalportal.com\n"
                        + "download loc =  /home/user/temp";

        writeConfigToFile(configContent);
        configParser.parseConfigFile(testConfigFile.getAbsolutePath());

        // Act
        ConfigSection section = configParser.getConfigSection("Gateway");

        // Assert
        assertNotNull(section);
        assertEquals("https://xyz.in", section.getProperties().get("endpoint"));
        assertEquals("https://cloud.internalportal.com", section.getProperties().get("certurl"));
        assertEquals("/home/user/temp", section.getProperties().get("download loc"));
    }

    @Test
    @DisplayName("Should parse array properties with comma-separated values")
    void testParseArrayProperties() throws IOException {
        // Arrange
        String configContent =
                "Order Service\n"
                        + "broker = https://orbroker.in\n"
                        + "topic = test_os_topic_1, test_os_topic_2";

        writeConfigToFile(configContent);
        configParser.parseConfigFile(testConfigFile.getAbsolutePath());

        // Act
        ConfigSection section = configParser.getConfigSection("Order Service");

        // Assert
        assertNotNull(section);
        assertEquals("https://orbroker.in", section.getProperties().get("broker"));

        List<String> topics = section.getArrayProperties().get("topic");
        assertNotNull(topics);
        assertEquals(2, topics.size());
        assertEquals("test_os_topic_1", topics.get(0));
        assertEquals("test_os_topic_2", topics.get(1));
    }

    @Test
    @DisplayName("Should return null for non-existent section")
    void testGetNonExistentSection() throws IOException {
        // Arrange
        String configContent = "Gateway\n" + "endpoint = https://xyz.in";

        writeConfigToFile(configContent);
        configParser.parseConfigFile(testConfigFile.getAbsolutePath());

        // Act & Assert
        assertNull(configParser.getConfigSection("NonExistent"));
    }

    @Test
    @DisplayName("Should retrieve all sections")
    void testGetAllSections() throws IOException {
        // Arrange
        String configContent =
                "Gateway\n"
                        + "endpoint = https://xyz.in\n"
                        + "\n"
                        + "CXO\n"
                        + "broker = http://cxobroker.in";

        writeConfigToFile(configContent);
        configParser.parseConfigFile(testConfigFile.getAbsolutePath());

        // Act
        Map<String, ConfigSection> allSections = configParser.getAllSections();

        // Assert
        assertEquals(2, allSections.size());
        assertTrue(allSections.containsKey("Gateway"));
        assertTrue(allSections.containsKey("CXO"));
    }

    @Test
    @DisplayName("Should handle CXO section with multiple topics")
    void testParseCXOSection() throws IOException {
        // Arrange
        String configContent =
                "CXO\n"
                        + "endpont = http://internal.cxo.com\n"
                        + "broker = http://cxobroker.in\n"
                        + "topic = test_cxo_topic, test_cxo_topic_1";

        writeConfigToFile(configContent);
        configParser.parseConfigFile(testConfigFile.getAbsolutePath());

        // Act
        ConfigSection section = configParser.getConfigSection("CXO");

        // Assert
        assertNotNull(section);
        assertEquals("http://internal.cxo.com", section.getProperties().get("endpont"));
        assertEquals("http://cxobroker.in", section.getProperties().get("broker"));

        List<String> topics = section.getArrayProperties().get("topic");
        assertNotNull(topics);
        assertEquals(2, topics.size());
    }

    private void writeConfigToFile(String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testConfigFile))) {
            writer.write(content);
        }
    }
}
