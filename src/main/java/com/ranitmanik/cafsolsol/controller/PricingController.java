package com.ranitmanik.cafsolsol.controller;

import com.ranitmanik.cafsolsol.service.PricingService;
import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    @Autowired private PricingService pricingService;

    private volatile boolean pricingDataLoaded = false;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadTSV(@RequestParam("file") MultipartFile file) {
        try {
            pricingService.loadPricingDataFromTSV(file.getInputStream());
            pricingDataLoaded = true;
            return ResponseEntity.ok("TSV file uploaded and processed successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading file: " + e.getMessage());
        }
    }

    @PostMapping("/load-default")
    public ResponseEntity<String> loadDefaultPricingData() {
        try {
            ClassPathResource resource = new ClassPathResource("pricing_data.tsv");
            pricingService.loadPricingDataFromTSV(resource.getInputStream());
            pricingDataLoaded = true;
            return ResponseEntity.ok("Default pricing data loaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error loading default pricing data: " + e.getMessage());
        }
    }

    @GetMapping("/price")
    public ResponseEntity<?> getPrice(
            @RequestParam(name = "skuId", required = true) String skuId,
            @RequestParam(name = "time", required = false) String time) {

        if (!pricingDataLoaded) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            "Pricing data not loaded. Please call /api/pricing/load-default or"
                                    + " /api/pricing/upload first");
        }

        if (!pricingService.skuExists(skuId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("SKU '" + skuId + "' not found");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("skuId", skuId);

        if (time != null && !time.isEmpty()) {
            try {
                LocalTime localTime = LocalTime.parse(time);
                Double price = pricingService.getPriceForSkuAtTime(skuId, localTime);
                response.put("time", time);
                response.put("price", price != null ? price : "NOT SET");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid time format. Use HH:mm format (e.g., 10:30)");
            }
        } else {
            Double price = pricingService.getPriceForSku(skuId);
            response.put("price", price != null ? price : "NOT SET");
        }

        return ResponseEntity.ok(response);
    }
}
