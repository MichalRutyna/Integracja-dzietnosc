package org.burza;

import org.burza.api_interactors.ApiBDLInteractor;
import org.burza.api_interactors.ApiSDPInteractor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class DownloadController {
    public static ArrayList<RegionYearValueObj> downloadFertility(int start_year, int end_year) {
        ApiSDPInteractor.zmienna_id = 589;
        ApiSDPInteractor.przekroj_id = 155;
        ApiSDPInteractor.okres_id = 282;
        var wymiar = ApiSDPInteractor.Wymiar.WOJEWODZTWA;
        return downloadGeneralSDP(start_year, end_year, wymiar);
    }

    public static ArrayList<RegionYearValueObj> downloadInflation(int start_year, int end_year) {
        ApiBDLInteractor.variable_id = 217230;
        return downloadGeneralBDL(start_year, end_year);
    }

    private static ArrayList<RegionYearValueObj> downloadGeneralBDL(int start_year, int end_year) {
        ArrayList<RegionYearValueObj> result = new ArrayList<>();
        ArrayList<Integer> years = new ArrayList<>();
        for (int year = start_year; year < end_year; year++) {
            years.add(year);
        }
        HashMap<String, HashMap<Integer, Double>> data = ApiBDLInteractor.get_data(years);
        if (data == null) {
            System.err.println("BDL download failed");
            return null;
        }
        return DataConverter.convertBDLDataToObjList(data);
    }

    private static ArrayList<RegionYearValueObj> downloadGeneralSDP(int start_year, int end_year, ApiSDPInteractor.Wymiar wymiar) {
        ArrayList<RegionYearValueObj> result = new ArrayList<>();
        for (int year = start_year; year < end_year; year++) {
            HashMap<String, Float> data = ApiSDPInteractor.getFormattedData(wymiar, year);
            result.addAll(DataConverter.convertSDPDataToObjList(data, year));
        }
        return result;
    }
}
