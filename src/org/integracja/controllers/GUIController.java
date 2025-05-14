package org.integracja.controllers;

import org.integracja.DatasetCreators;
import org.integracja.api_interactors.ApiBDLInteractor;
import org.integracja.api_interactors.ApiSDPInteractor;
import org.integracja.models.TitledPeriod;
import org.jfree.chart.title.Title;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.TimeSeriesCollection;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;

public class GUIController {
    public interface Callback {
        void call(int value);
    }

    /**
     * Makes requests to the API for the data, then loads it into the database.
     */
    public interface DownloadIntoDatabaseFunction {
        void download(Callback progess_callback);
    }

    /**
     * Load the data from the database and return it for display
     * Consideration: load into a model, then display the model?
     * @return Loaded data
     */
    public interface GetDatasetFunction {
        TimeSeriesCollection getDataset();
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

    public static GetDatasetFunction loadFertilityFromDatabase = () -> {
        TimeSeriesCollection dataset = null;

        // for testing
        dataset = DatasetCreators.getFertilityAllRegionsDataset(2000);
        return dataset;
    };

    public static GetDatasetFunction loadInflationFromDatabase = () -> {
        TimeSeriesCollection dataset = null;

        // for testing
        dataset = DatasetCreators.getInflationAllRegionsDataset();
        return dataset;
    };

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

        return periods;
    }
}
