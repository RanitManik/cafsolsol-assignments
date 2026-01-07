package com.ranitmanik.cafsolsol.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.ranitmanik.cafsolsol.service.PricingService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Pricing Controller Tests")
class PricingControllerTest {

    private PricingService pricingService;

    @BeforeEach
    void setUp() throws IOException {
        pricingService = new PricingService();

        // Load sample pricing data
        String tsvData =
                "SkuID\tStartTime\tEndTime\tPrice\n"
                        + "u00006541\t10:00\t10:15\t101\n"
                        + "i00006111\t10:02\t10:05\t100\n"
                        + "u09099000\t10:00\t10:08\t5000\n"
                        + "t12182868\t10:00\t20:00\t87\n"
                        + "b98989000\t00:30\t07:00\t9128\n"
                        + "u00006541\t10:05\t10:10\t99\n"
                        + "t12182868\t14:00\t15:00\t92";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(tsvData.getBytes());
        pricingService.loadPricingDataFromTSV(inputStream);
    }

    @Test
    @DisplayName("Should return 101 when u00006541 at 10:03")
    void testScenario1() {
        Double price = pricingService.getPriceForSkuAtTime("u00006541", LocalTime.parse("10:03"));
        assertNotNull(price);
        assertEquals(101.0, price);
    }

    @Test
    @DisplayName("Should return NOT SET when u00006541 before 09:55")
    void testScenario2() {
        Double price = pricingService.getPriceForSkuAtTime("u00006541", LocalTime.parse("09:55"));
        assertNull(price);
    }

    @Test
    @DisplayName("Should return 99 when u00006541 at 10:05 (overlapping ranges)")
    void testScenario3() {
        Double price = pricingService.getPriceForSkuAtTime("u00006541", LocalTime.parse("10:05"));
        assertNotNull(price);
        assertEquals(99.0, price);
    }

    @Test
    @DisplayName("Should handle i00006111 with valid time")
    void testValidTimeInRange() {
        Double price = pricingService.getPriceForSkuAtTime("i00006111", LocalTime.parse("10:03"));
        assertNotNull(price);
        assertEquals(100.0, price);
    }

    @Test
    @DisplayName("Should handle time at start boundary (inclusive)")
    void testStartBoundaryInclusive() {
        Double price = pricingService.getPriceForSkuAtTime("u00006541", LocalTime.parse("10:00"));
        assertNotNull(price);
        assertEquals(101.0, price);
    }

    @Test
    @DisplayName("Should handle time at end boundary (exclusive)")
    void testEndBoundaryExclusive() {
        Double price = pricingService.getPriceForSkuAtTime("u00006541", LocalTime.parse("10:15"));
        assertNull(price);
    }

    @Test
    @DisplayName("Should return null for non-existent SKU")
    void testNonExistentSku() {
        Double price = pricingService.getPriceForSkuAtTime("nonExistent", LocalTime.parse("10:03"));
        assertNull(price);
    }

    @Test
    @DisplayName("Should verify SKU exists")
    void testSkuExists() {
        assertTrue(pricingService.skuExists("u00006541"));
        assertFalse(pricingService.skuExists("nonExistent"));
    }

    @Test
    @DisplayName("Should return first price when no time specified")
    void testGetPriceWithoutTime() {
        Double price = pricingService.getPriceForSku("u00006541");
        assertNotNull(price);
        assertEquals(101.0, price);
    }

    @Test
    @DisplayName("Should handle long time ranges like t12182868 (10:00-20:00)")
    void testLongTimeRange() {
        Double price = pricingService.getPriceForSkuAtTime("t12182868", LocalTime.parse("15:00"));
        assertNotNull(price);
        assertEquals(87.0, price);
    }
}
