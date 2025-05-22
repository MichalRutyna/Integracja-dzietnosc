package org.burza;

import org.burza.api_interactors.ApiBDLInteractor;
import org.burza.api_interactors.ApiSDPInteractor;
import org.burza.models.RegionYearValueObj;

import java.util.ArrayList;
import java.util.HashMap;

public class DownloadController {
    public interface Callback {
        void call(int value);
    }

    public static ArrayList<RegionYearValueObj> downloadFertility(Callback progress_callback) {
        ApiSDPInteractor.zmienna_id = 589;
        ApiSDPInteractor.przekroj_id = 155;
        ApiSDPInteractor.okres_id = 282;
        var wymiar = ApiSDPInteractor.Wymiar.WOJEWODZTWA;
        int start_year = 2000;
        int end_year = 2024;
        return downloadGeneralSDP(start_year, end_year, wymiar, progress_callback);
    }

    public static ArrayList<RegionYearValueObj> downloadInflation(Callback progress_callback) {
        ApiBDLInteractor.variable_id = 217230;
        int start_year = 2010;
        int end_year = 2024;
        return downloadGeneralBDL(start_year, end_year, progress_callback);
    }

    private static ArrayList<RegionYearValueObj> downloadGeneralBDL(int start_year, int end_year, Callback progress_callback) {
        ArrayList<RegionYearValueObj> result = new ArrayList<>();
        ArrayList<Integer> years = new ArrayList<>();
        for (int year = start_year; year < end_year; year++) {
            years.add(year);
        }
        HashMap<String, HashMap<Integer, Double>> data = ApiBDLInteractor.get_data(years);
        System.out.println("Downloaded");
        if (data == null) {
            System.err.println("BDL download failed");
            return null;
        }
        progress_callback.call(100);
        return DataConverter.convertBDLDataToObjList(data);
    }

    private static ArrayList<RegionYearValueObj> downloadGeneralSDP(int start_year, int end_year, ApiSDPInteractor.Wymiar wymiar, Callback progress_callback) {
        ArrayList<RegionYearValueObj> result = new ArrayList<>();
        for (int year = start_year; year < end_year; year++) {
            //HashMap<String, Float> data = ApiSDPInteractor.getFormattedData(wymiar, year);
            //result.addAll(DataConverter.convertSDPDataToObjList(data, year));
            try {
                Thread.sleep(200); // sleep to stay within API rate limits
            } catch (InterruptedException ignored) {}
            System.out.println((int) Math.ceil((double)(year+1 - start_year) / (end_year - start_year) * 100));
            progress_callback.call((int) Math.ceil((double) (year+1 - start_year) / (end_year - start_year) * 100));
        }
        return result;
    }
}
