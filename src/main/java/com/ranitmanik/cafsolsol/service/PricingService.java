package com.ranitmanik.cafsolsol.service;

import com.ranitmanik.cafsolsol.model.PricingRecord;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.*;

@Service
public class PricingService {

    private Map<String, List<PricingRecord>> pricingData = new HashMap<>();

    public void loadPricingDataFromTSV(InputStream inputStream) throws IOException {
        pricingData.clear();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue; // Skip header line
                }

                String[] columns = line.split("\t");
                if (columns.length == 4) {
                    String skuId = columns[0].trim();
                    LocalTime startTime = LocalTime.parse(columns[1].trim());
                    LocalTime endTime = LocalTime.parse(columns[2].trim());
                    double price = Double.parseDouble(columns[3].trim());

                    PricingRecord record = new PricingRecord(skuId, startTime, endTime, price);
                    pricingData.computeIfAbsent(skuId, k -> new ArrayList<>()).add(record);
                }
            }
        }
    }

    public Double getPriceForSkuAtTime(String skuId, LocalTime time) {
        if (!pricingData.containsKey(skuId)) {
            return null;
        }

        List<PricingRecord> records = pricingData.get(skuId);
        Double lastMatchingPrice = null;
        
        for (PricingRecord record : records) {
            if (record.isTimeInRange(time)) {
                lastMatchingPrice = record.getPrice();
            }
        }

        return lastMatchingPrice;
    }

    public Double getPriceForSku(String skuId) {
        if (!pricingData.containsKey(skuId)) {
            return null;
        }

        List<PricingRecord> records = pricingData.get(skuId);
        if (!records.isEmpty()) {
            return records.get(0).getPrice();
        }

        return null;
    }

    public Map<String, List<PricingRecord>> getAllPricingData() {
        return new HashMap<>(pricingData);
    }

    public boolean skuExists(String skuId) {
        return pricingData.containsKey(skuId);
    }
}
