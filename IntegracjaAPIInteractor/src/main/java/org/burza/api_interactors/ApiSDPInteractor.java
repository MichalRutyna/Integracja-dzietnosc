package org.burza.api_interactors;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

    private static JSONArray _getJSONArrayFromRequest(String request_url) throws IOException, InterruptedException {
        HttpResponse<String> response = _getResponseFromString(request_url);
        if (response == null) return null;
        return new JSONArray(response.body());
    }

    private static JSONArray _getJSONArrayFromRequestViaObject(String request_url) throws IOException, InterruptedException {
        HttpResponse<String> response = _getResponseFromString(request_url);
        if (response == null) return null;
        JSONObject data = new JSONObject(response.body());

        try {
            return data.getJSONArray("data");
        }
        catch(org.json.JSONException e) {
            System.err.println("JSONException: " + e.getMessage());
            return null;
        }
    }

    private static HttpResponse<String> _getResponseFromString(String request_url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(request_url))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("Error: Received HTTP status " + response.statusCode() + ": " + response.body() + "\n" + request_url);
            return null;
        }
        return response;
    }

    /**
     * Get raw data of positions
     * @return raw data
     * @throws IOException
     * @throws InterruptedException
     */
    private static JSONArray _getPositionData() throws IOException, InterruptedException {
        /*
        Niesformatowane dane dla pozycji
         */
        String request_string =
                "https://api-sdp.stat.gov.pl/api/variable/variable-section-position?" +
                        "id-przekroj="+przekroj_id+
                        "&lang=pl";

        return _getJSONArrayFromRequest(request_string);
    }


    /**
     * Get raw data of a variable
     * @param rok
     * @return raw data
     * @throws IOException
     * @throws InterruptedException
     */
    private static JSONArray _getData(Integer rok) throws IOException, InterruptedException {
        String request_string =
                "https://api-sdp.stat.gov.pl/api/variable/variable-data-section?" +
                        "id-zmienna="+zmienna_id+
                        "&id-przekroj="+przekroj_id+
                        "&id-rok="+rok+
                        "&id-okres="+okres_id+
                        "&page-size=5000&page=0&lang=pl";

        return  _getJSONArrayFromRequestViaObject(request_string);
    }

    /**
     * Get translation hashmap for position ids
     * @return hashmap id-pozycja=nazwa-województwa
     * @throws IOException
     * @throws InterruptedException
     */
    public static HashMap<Integer, String> getPostionNames() throws IOException, InterruptedException {
        JSONArray pozycje = _getPositionData();
        HashMap<Integer, String> pozycje_nazwy = new HashMap<>();
        for (int i = 0; i < pozycje.length(); i++) {
            JSONObject pozycja = pozycje.getJSONObject(i);
            // tyko Polska, województwa, powiaty albo Polska, województwa
            var wymiar_id = pozycja.getInt("id-wymiar");
            if (wymiar_id != 10 && wymiar_id != 4) {
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
            String name = pozycja.getString("nazwa-pozycja");
            name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
            pozycje_nazwy.put(pozycja.getInt("id-pozycja"), name);
        }
        return pozycje_nazwy;
    }

    /**
     * Request data and format positions using downloaded dictionary
     * @param wymiar statyczny enum klasy
     * @param rok
     * @return hashmapa nazwa-pozycji=wartosc ("Lubelskie"=1.03)
     */
    public static HashMap<String, Float> getFormattedData(Wymiar wymiar, Integer rok) {
        JSONArray dane = null;
        try {
            dane = _getData(rok);
        } catch (IOException | InterruptedException e) {
            System.out.println("Error in variable "+zmienna_id+" data request: " + e.getMessage());
            throw new RuntimeException(e);
        }
        if (dane == null) {
            return new HashMap<>();
        }

        HashMap<Integer, String> pozycje_nazwy;
        try {
            pozycje_nazwy = ApiSDPInteractor.getPostionNames();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error in positions data request: " + e.getMessage());
            throw new RuntimeException(e);
        }

        HashMap<String, Float> wartosci = new HashMap<>();
        for (int i = 0; i < dane.length(); i++) {
            JSONObject instancja = dane.getJSONObject(i);
            // bez podzialu na miasto i wieś, tylko ogółem
            var wymiar2 = instancja.optInt("id-pozycja-2");
            if (wymiar2 != 0 && wymiar2 != 6655092 && wymiar2 != 4801797) {
                continue;
            }
            int pozycja = instancja.optInt("id-pozycja-1");
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


    /**
     *  Get a list of suitable variables for consideration, not part of buisness logic
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static HashSet<String> getSuitableVariables(Set<Integer> okresy, Set<Integer> przekroje) throws IOException, InterruptedException {
        String request_string =
                "https://api-sdp.stat.gov.pl/api/variable/variable-sections-periods?" +
                "page-size=20000&page=0&lang=pl";
        JSONArray data;
        try {
             data = _getJSONArrayFromRequestViaObject(request_string);
        }
        catch (org.json.JSONException e) {
            System.err.println("JSONException: " + e.getMessage());
            return null;
        }


        HashSet<String> suitable_variables = new HashSet<>();
        for (int i = 0, size = data.length(); i < size; i++) {
            JSONObject instance = data.getJSONObject(i);

            // filtering
            if (przekroje != null) {
                int przekroj = instance.getInt("id-przekroj");
                if (!przekroje.contains(przekroj)) {
                    continue;
                }
            }
            if (okresy != null) {
                int okres = instance.getInt("id-okres");
                if (!okresy.contains(okres)) {
                    continue;
                }
            }

            suitable_variables.add(instance.getString("nazwa-zmienna"));
        }
        return suitable_variables;
    }



}
