package org.integracja.controllers;

import org.integracja.DatasetCreators;
import org.integracja.api_interactors.ApiBDLInteractor;
import org.integracja.api_interactors.ApiSDPInteractor;
import org.integracja.models.IntegrationDataset;
import org.integracja.models.TitledPeriod;
import org.jfree.chart.title.Title;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.util.*;

public class GUIController {
    private static Set<IntegrationDataset> loaded_datasets = new HashSet<>();

    public interface Callback {
        void call(int value);
    }

    /**
     * Makes requests to the API for the data, then loads it into the database.
     */
    public interface DownloadIntoDatabaseFunction {
        void download(Callback progess_callback);
    }

    public static DownloadIntoDatabaseFunction downloadFertility = (callback -> {
        ApiSDPInteractor.zmienna_id = 589;
        ApiSDPInteractor.przekroj_id = 155;
        ApiSDPInteractor.okres_id = 282;
        int start_year = 2000;
        int end_year = 2024;
        for (int year = start_year; year < end_year; year++) {
//            HashMap<String, Float> data = ApiSDPInteractor.getFormattedData(ApiSDPInteractor.Wymiar.WOJEWODZTWA, year);
            // insert row into database, alternatively collect together
            // SDP allows requests only for one year
//            if (data == null) {
//                continue;
//            }

            callback.call((int) Math.ceil((double) 1 / (end_year - start_year) * 100));
            try {
                Thread.sleep(200); // sleep to stay within API rate limits
            } catch (InterruptedException ignored) {}
        }
    });

    public static DownloadIntoDatabaseFunction downloadInflation = (callback -> {
        ApiBDLInteractor.variable_id = 217230;
//        HashMap<String, HashMap<Integer, Double>> data = ApiBDLInteractor.get_data();
        callback.call(100);
    });

    public static String[] getFetchableDatasets() {
        // for testing, replace with a query to the database
        return new String[]{"Inflation", "Fertility"};
    }

    public static void fetchDataset(String dataset_name) {
        // all temporary, replace with a query
        TimeSeriesCollection dataset;
        String name;
        String description = "";
        switch (dataset_name) {
            case "Inflation":
                 dataset = DatasetCreators.getInflationAllRegionsDataset();
                 name = "Inflation";
                 break;
             case "Fertility":
                 dataset = DatasetCreators.getFertilityAllRegionsDataset(2000);
                 name = "Fertility";
                 break;
             default:
                 return;
        }
        loaded_datasets.add(new IntegrationDataset(name, description, dataset));
    }

    public static ArrayList<TitledPeriod> loadPeriodsFromDatabase() {
        ArrayList<TitledPeriod> periods = new ArrayList<>();

        //for testing
        periods.add(new TitledPeriod(
                new GregorianCalendar(2012, Calendar.JANUARY, 1).getTime(),
                new GregorianCalendar(2015, Calendar.DECEMBER, 31).getTime(),
                "Koniec świata"));
        periods.add(new TitledPeriod(
                new GregorianCalendar(2022, Calendar.JANUARY, 1).getTime(),
                new GregorianCalendar(2022, Calendar.DECEMBER, 31).getTime(),
                "Drugi koniec świata"));

        periods.add(new TitledPeriod(
                new GregorianCalendar(2004, Calendar.APRIL, 18).getTime(),
                new GregorianCalendar(2005, Calendar.APRIL, 2).getTime(),
                "Moje urodziny do śmierci JP2"));

        return periods;
    }

    public static Set<IntegrationDataset> getLoadedDatasets() {
        return loaded_datasets;
    }
}
