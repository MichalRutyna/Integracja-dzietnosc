package org.integracja;

import org.jfree.data.category.DefaultCategoryDataset;

import java.io.IOException;
import java.util.*;

public class DatasetCreators {
    public static final int TIMEOUT_MS = 200;

    // Shortcuts for basic datasets
    public static DefaultCategoryDataset getFertilityAllRegionsDataset(int start_year) throws InterruptedException {
        return getGeneralSDPVariableDataset(589, 155, 282, ApiSDPInteractor.Wymiar.WOJEWODZTWA, start_year);
    }

    public static DefaultCategoryDataset getFertilitySingleRegionDataset(String region_name) throws InterruptedException {
        return getSingleRegionGeneralSDPVariableDataset(589, 155, 282, ApiSDPInteractor.Wymiar.WOJEWODZTWA, region_name, 2000);
    }


    public static DefaultCategoryDataset getInflationAllRegionsDataset() throws InterruptedException, IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        HashMap<String, HashMap<Integer, Double>> data = ApiBDLInteractor.get_data();

        for (Map.Entry<String, HashMap<Integer, Double>> unitEntry : data.entrySet()) {
            String unitName = unitEntry.getKey();
            HashMap<Integer, Double> yearValueMap = unitEntry.getValue();
//            for (Map.Entry<Integer, Double> yearEntry : yearValueMap.entrySet()) {
//                Integer year = yearEntry.getKey();
//                Double value = yearEntry.getValue();
//                dataset.addValue(value, unitName, year);
//            }
            List<Integer> sortedYears = new ArrayList<>(yearValueMap.keySet());
            Collections.sort(sortedYears);

            for (Integer year : sortedYears) {
                Double value = yearValueMap.get(year);
                dataset.addValue(value, unitName, year);
            }
        }

        return dataset;
    }

    public static DefaultCategoryDataset getGeneralSDPVariableDataset(int zmienna, int przekroj, int okres, ApiSDPInteractor.Wymiar wymiar, int start_year) throws InterruptedException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        ApiSDPInteractor.zmienna_id = zmienna;
        ApiSDPInteractor.przekroj_id = przekroj;
        ApiSDPInteractor.okres_id = okres;
        for (int year = start_year; year < 2024; year++) {
            HashMap<String, Float> wartosci = ApiSDPInteractor.getFormattedData(wymiar, year);
            for (String woj : wartosci.keySet()) {
                dataset.addValue((Number) wartosci.get(woj), woj, year);
            }
            Thread.sleep(TIMEOUT_MS);
        }
        return dataset;
    }

    public static DefaultCategoryDataset getSingleRegionGeneralSDPVariableDataset(int zmienna, int przekroj, int okres, ApiSDPInteractor.Wymiar wymiar, String region_name, int start_year) throws InterruptedException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        ApiSDPInteractor.zmienna_id = zmienna;
        ApiSDPInteractor.przekroj_id = przekroj;
        ApiSDPInteractor.okres_id = okres;
        for (int year = start_year; year < 2024; year++) {
            HashMap<String, Float> wartosci = ApiSDPInteractor.getFormattedData(ApiSDPInteractor.Wymiar.WOJEWODZTWA, year);
            dataset.addValue((Number) wartosci.get(region_name), region_name, year);
            Thread.sleep(200);
        }
        return dataset;
    }
}
