package com.ranitmanik.cafsolsol.service;

import com.ranitmanik.cafsolsol.model.PricingRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PricingService Tests")
class PricingServiceTest {

    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        pricingService = new PricingService();
    }

    @Test
    @DisplayName("Should load TSV data correctly")
    void testLoadTSVData() throws IOException {
        // Arrange
        String tsvData = "SkuID\tStartTime\tEndTime\tPrice\n" +
                "u00006541\t10:00\t10:15\t101\n" +
                "i00006111\t10:02\t10:05\t100";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(tsvData.getBytes());

        // Act
        pricingService.loadPricingDataFromTSV(inputStream);

        // Assert
        assertTrue(pricingService.skuExists("u00006541"));
        assertTrue(pricingService.skuExists("i00006111"));
    }

    @Test
    @DisplayName("Should return correct price when time is within range")
    void testGetPriceForSkuAtTimeInRange() throws IOException {
        // Arrange
        String tsvData = "SkuID\tStartTime\tEndTime\tPrice\n" +
                "u00006541\t10:00\t10:15\t101";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(tsvData.getBytes());
        pricingService.loadPricingDataFromTSV(inputStream);

        // Act
        Double price = pricingService.getPriceForSkuAtTime("u00006541", LocalTime.parse("10:03"));

        // Assert
        assertNotNull(price);
        assertEquals(101.0, price);
    }

    @Test
    @DisplayName("Should return NOT SET when time is before range")
    void testGetPriceForSkuAtTimeBeforeRange() throws IOException {
        // Arrange
        String tsvData = "SkuID\tStartTime\tEndTime\tPrice\n" +
                "u00006541\t10:00\t10:15\t101";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(tsvData.getBytes());
        pricingService.loadPricingDataFromTSV(inputStream);

        // Act
        Double price = pricingService.getPriceForSkuAtTime("u00006541", LocalTime.parse("09:55"));

        // Assert
        assertNull(price);
    }

    @Test
    @DisplayName("Should return NOT SET when time is after range")
    void testGetPriceForSkuAtTimeAfterRange() throws IOException {
        // Arrange
        String tsvData = "SkuID\tStartTime\tEndTime\tPrice\n" +
                "u00006541\t10:00\t10:15\t101";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(tsvData.getBytes());
        pricingService.loadPricingDataFromTSV(inputStream);

        // Act
        Double price = pricingService.getPriceForSkuAtTime("u00006541", LocalTime.parse("10:15"));

        // Assert
        assertNull(price);
    }

    @Test
    @DisplayName("Should handle multiple price ranges for same SKU")
    void testGetPriceForSkuWithMultipleRanges() throws IOException {
        // Arrange
        String tsvData = "SkuID\tStartTime\tEndTime\tPrice\n" +
                "u00006541\t10:00\t10:15\t101\n" +
                "u00006541\t10:05\t10:10\t99";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(tsvData.getBytes());
        pricingService.loadPricingDataFromTSV(inputStream);

        // Act & Assert
        Double price1 = pricingService.getPriceForSkuAtTime("u00006541", LocalTime.parse("10:03"));
        assertEquals(101.0, price1);

        Double price2 = pricingService.getPriceForSkuAtTime("u00006541", LocalTime.parse("10:07"));
        assertEquals(99.0, price2);
    }

    @Test
    @DisplayName("Should return null for non-existent SKU")
    void testGetPriceForNonExistentSku() throws IOException {
        // Arrange
        String tsvData = "SkuID\tStartTime\tEndTime\tPrice\n" +
                "u00006541\t10:00\t10:15\t101";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(tsvData.getBytes());
        pricingService.loadPricingDataFromTSV(inputStream);

        // Act
        Double price = pricingService.getPriceForSkuAtTime("nonExistent", LocalTime.parse("10:03"));

        // Assert
        assertNull(price);
    }

    @Test
    @DisplayName("Should return first price when no time specified")
    void testGetPriceForSkuWithoutTime() throws IOException {
        // Arrange
        String tsvData = "SkuID\tStartTime\tEndTime\tPrice\n" +
                "u00006541\t10:00\t10:15\t101";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(tsvData.getBytes());
        pricingService.loadPricingDataFromTSV(inputStream);

        // Act
        Double price = pricingService.getPriceForSku("u00006541");

        // Assert
        assertNotNull(price);
        assertEquals(101.0, price);
    }

    @Test
    @DisplayName("Should retrieve all pricing data")
    void testGetAllPricingData() throws IOException {
        // Arrange
        String tsvData = "SkuID\tStartTime\tEndTime\tPrice\n" +
                "u00006541\t10:00\t10:15\t101\n" +
                "i00006111\t10:02\t10:05\t100";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(tsvData.getBytes());
        pricingService.loadPricingDataFromTSV(inputStream);

        // Act
        Map<String, List<PricingRecord>> allData = pricingService.getAllPricingData();

        // Assert
        assertEquals(2, allData.size());
        assertTrue(allData.containsKey("u00006541"));
        assertTrue(allData.containsKey("i00006111"));
    }

    @Test
    @DisplayName("Should verify SKU existence")
    void testSkuExists() throws IOException {
        // Arrange
        String tsvData = "SkuID\tStartTime\tEndTime\tPrice\n" +
                "u00006541\t10:00\t10:15\t101";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(tsvData.getBytes());
        pricingService.loadPricingDataFromTSV(inputStream);

        // Act & Assert
        assertTrue(pricingService.skuExists("u00006541"));
        assertFalse(pricingService.skuExists("nonExistent"));
    }

    @Test
    @DisplayName("Should handle start time inclusive and end time exclusive")
    void testTimeRangeBoundary() throws IOException {
        // Arrange
        String tsvData = "SkuID\tStartTime\tEndTime\tPrice\n" +
                "u00006541\t10:00\t10:15\t101";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(tsvData.getBytes());
        pricingService.loadPricingDataFromTSV(inputStream);

        // Act & Assert
        // Start time should be included
        Double priceAtStart = pricingService.getPriceForSkuAtTime("u00006541", LocalTime.parse("10:00"));
        assertEquals(101.0, priceAtStart);

        // End time should be excluded
        Double priceAtEnd = pricingService.getPriceForSkuAtTime("u00006541", LocalTime.parse("10:15"));
        assertNull(priceAtEnd);
    }
}
