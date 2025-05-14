package org.integracja;

import org.integracja.api_interactors.ApiBDLInteractor;
import org.integracja.api_interactors.ApiSDPInteractor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.IOException;
import java.util.*;

/**
 * Temporary class for testing, the actual app should get data from a database
 */
public class DatasetCreators {
    public static final int TIMEOUT_MS = 200;

    // Shortcuts for basic datasets
    public static TimeSeriesCollection getFertilityAllRegionsDataset(int start_year) {
        return getGeneralSDPVariableDataset(589, 155, 282, ApiSDPInteractor.Wymiar.WOJEWODZTWA, start_year);
    }

    public static DefaultCategoryDataset getFertilitySingleRegionDataset(String region_name) {
        return getSingleRegionGeneralSDPVariableDataset(589, 155, 282, ApiSDPInteractor.Wymiar.WOJEWODZTWA, region_name, 2000);
    }


    public static TimeSeriesCollection getInflationAllRegionsDataset(){
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        HashMap<String, HashMap<Integer, Double>> data = ApiBDLInteractor.get_data();

        for (Map.Entry<String, HashMap<Integer, Double>> unitEntry : data.entrySet()) {
            String unitName = unitEntry.getKey();
            HashMap<Integer, Double> yearValueMap = unitEntry.getValue();
            TimeSeries series = new TimeSeries(unitName);

            for (Map.Entry<Integer, Double> yearEntry : yearValueMap.entrySet()) {
                Integer year = yearEntry.getKey();
                Double value = yearEntry.getValue();
                series.add(new Year(year), value);
            }
            dataset.addSeries(series);
        }

        return dataset;
    }

    public static TimeSeriesCollection getGeneralSDPVariableDataset(int zmienna, int przekroj, int okres, ApiSDPInteractor.Wymiar wymiar, int start_year){
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        ApiSDPInteractor.zmienna_id = zmienna;
        ApiSDPInteractor.przekroj_id = przekroj;
        ApiSDPInteractor.okres_id = okres;
        HashMap<String, TimeSeries> series = new HashMap<>();
        for (int year = start_year; year < 2024; year++) {
            HashMap<String, Float> wartosci = ApiSDPInteractor.getFormattedData(wymiar, year);
            for (String woj : wartosci.keySet()) {
                if (!series.containsKey(woj)) {
                    var empty = new TimeSeries(woj);
                    series.put(woj, empty);
                    dataset.addSeries(empty);
                }
                series.get(woj).add(new Year(year), wartosci.get(woj));
            }
            try {
                Thread.sleep(TIMEOUT_MS);
            } catch (InterruptedException ignored) {}
        }
//        for (TimeSeries full_series: series.values()) {
//            dataset.addSeries(full_series);
//        }
        return dataset;
    }

    public static DefaultCategoryDataset getSingleRegionGeneralSDPVariableDataset(int zmienna, int przekroj, int okres, ApiSDPInteractor.Wymiar wymiar, String region_name, int start_year) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        ApiSDPInteractor.zmienna_id = zmienna;
        ApiSDPInteractor.przekroj_id = przekroj;
        ApiSDPInteractor.okres_id = okres;
        for (int year = start_year; year < 2024; year++) {
            HashMap<String, Float> wartosci = ApiSDPInteractor.getFormattedData(ApiSDPInteractor.Wymiar.WOJEWODZTWA, year);
            dataset.addValue((Number) wartosci.get(region_name), region_name, year);
            try {
                Thread.sleep(TIMEOUT_MS);
            } catch (InterruptedException ignored) {}
        }
        return dataset;
    }
}
