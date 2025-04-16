package org.integracja;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class ApiSDPInteractor {
    static HttpClient client = HttpClient.newHttpClient();

    public enum Wymiar {
        POLSKA,
        WOJEWODZTWA,
        POWIATY,
    }

    public static Integer zmienna_id = 589;
    public static Integer przekroj_id = 155;
    public static Integer okres_id = 282;

    private static JSONArray _getPositionData() {
        /*
        Niesformatowane dane dla pozycji
         */
        HttpRequest pozycje_request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-sdp.stat.gov.pl/api/variable/variable-section-position?id-przekroj="+ przekroj_id +"&lang=pl"))
                .build();
        String pozycje_response = client.sendAsync(pozycje_request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .join();

        JSONArray pozycje = new JSONArray(pozycje_response);
        return pozycje;
    }

    private static JSONArray _getData(Integer rok) {
        /*
        Niesformatowane dane
         */
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-sdp.stat.gov.pl/api/variable/variable-data-section?id-zmienna="+zmienna_id+"&id-przekroj="+przekroj_id+"&id-rok="+rok+"&id-okres="+okres_id+"&page-size=5000&page=0&lang=pl"))
                .build();
        String data_response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .join();

        JSONObject data = new JSONObject(data_response);

        if (data.optString("status").equals("404")) {
            return null;
        }

        try {
            return data.getJSONArray("data");
        }
        catch(org.json.JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static HashMap<Integer, String> getPostionNames() {
        /*
        Zwraca hashmape id-pozycja=nazwa-województwa
         */
        JSONArray pozycje = _getPositionData();
        HashMap<Integer, String> pozycje_nazwy = new HashMap<>();
        for (int i = 0; i < pozycje.length(); i++) {
            JSONObject pozycja = pozycje.getJSONObject(i);
            // tyko Polska, województwa, powiaty
            if (pozycja.getInt("id-wymiar") != 10) {
                continue;
            }
//            // Tylko województwa
//            if (!pozycja.getString("symbol").endsWith("00000000") ) {
//                continue;
//            }
//            // Bez POLSKA
//            if (Objects.equals(pozycja.getString("nazwa-pozycja"), "POLSKA")) {
//                continue;
//            }
            String nazwa = pozycja.getString("nazwa-pozycja");
            nazwa = nazwa.substring(0,1).toUpperCase() + nazwa.substring(1).toLowerCase();
            pozycje_nazwy.put(pozycja.getInt("id-pozycja"), nazwa);
        }
        return pozycje_nazwy;
    }

    /**
     * @param wymiar statyczny enum klasy
     * @param rok
     * @return hashmapa nazwa-pozycji=wartosc
     */
    public static HashMap<String, Float> getFormattedData(Wymiar wymiar, Integer rok) {
        JSONArray dane = _getData(rok);
        if (dane == null) {
            return new HashMap<>();
        }
        HashMap<Integer, String> pozycje_nazwy = ApiSDPInteractor.getPostionNames();
        HashMap<String, Float> wartosci = new HashMap<>();

        for (int i = 0; i < dane.length(); i++) {
            JSONObject instancja = dane.getJSONObject(i);
            // bez podzialu na miasto i wieś, tylko ogółem
            if (instancja.getInt("id-pozycja-2") != 6655092) {
                continue;
            }
            int pozycja = instancja.getInt("id-pozycja-1");
            String pozycja_nazwa = pozycje_nazwy.get(pozycja);

            // jeśli dane dla polski
            if (pozycja_nazwa.equals("Polska")) {
                if (wymiar == Wymiar.POLSKA) {
                    wartosci.put("Polska", instancja.getFloat("wartosc"));
                    return wartosci;
                }
            } else if (pozycja_nazwa.startsWith("Powiat")) {
                if (wymiar == Wymiar.POWIATY) {
                    wartosci.put(pozycja_nazwa, instancja.getFloat("wartosc"));
                }
            } else {
                if (wymiar == Wymiar.WOJEWODZTWA) {
                    wartosci.put(pozycja_nazwa, instancja.getFloat("wartosc"));
                }
            }
        }
        return wartosci;
    }
}
