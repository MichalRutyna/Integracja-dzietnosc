package org.burza;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataConverter {
    public static ArrayList<RegionYearValueObj> convertBDLDataToObjList(HashMap<String, HashMap<Integer, Double>> data) {
        ArrayList<RegionYearValueObj> result = new ArrayList<>();

        for (Map.Entry<String, HashMap<Integer, Double>> unitEntry : data.entrySet()) {
            String region = unitEntry.getKey();
            HashMap<Integer, Double> yearValueMap = unitEntry.getValue();

            for (Map.Entry<Integer, Double> yearEntry : yearValueMap.entrySet()) {
                Integer year = yearEntry.getKey();
                Double value = yearEntry.getValue();

                result.add(new RegionYearValueObj(region, year, value));
            }
        }
        return result;
    }

    public static ArrayList<RegionYearValueObj> convertSDPDataToObjList(HashMap<String, Float> data, Integer year) {
        ArrayList<RegionYearValueObj> result = new ArrayList<>();
        for (Map.Entry<String, Float> unitEntry : data.entrySet()) {
            String region = unitEntry.getKey();
            Float value = unitEntry.getValue();

            result.add(new RegionYearValueObj(region, year, (double) value));
        }
        return result;
    }
}
