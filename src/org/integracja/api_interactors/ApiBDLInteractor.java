package org.integracja.api_interactors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class ApiBDLInteractor {
    public static int variable_id = 217230;


    static HttpClient client = HttpClient.newHttpClient();

    public static HashMap<String, HashMap<Integer, Double>> get_data(){
        HttpResponse<String> response;
        try {
            response = _get_xml_doc();
        } catch (IOException e) {
            System.err.println("Error in Get_xml_doc: " + e.getMessage() + " " + e.getCause());
            return null;
        } catch (InterruptedException ignored) { return null; }
        if (response == null) { return null; }

        Document doc;
        try {
             doc = _parse_xml_data(response);
        } catch (IOException e) {
            System.err.println("Error in Get_xml_doc: " + e.getMessage() + " " + e.getCause());
            return null;
        }
        if (doc == null) { return null; }

        return _format_data(doc);
    }

    private static HashMap<String, HashMap<Integer, Double>> _format_data(Document doc) {
        HashMap<String, HashMap<Integer, Double>> data = new HashMap<>();

        NodeList unitDataList = doc.getElementsByTagName("unitData");
        for (int i = 0; i < unitDataList.getLength(); i++) {
            HashMap<Integer, Double> unitDataMap = new HashMap<>();
            Element unitData = (Element) unitDataList.item(i);
            String unitName = unitData.getElementsByTagName("name").item(0).getTextContent();

            NodeList yearValList = unitData.getElementsByTagName("yearVal");

            for (int j = 0; j < yearValList.getLength(); j++) {
                Element yearVal = (Element) yearValList.item(j);
                String yearStr = yearVal.getElementsByTagName("year").item(0).getTextContent();
                String valStr = yearVal.getElementsByTagName("val").item(0).getTextContent();

                try {
                    unitDataMap.put(Integer.parseInt(yearStr), Double.parseDouble(valStr));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number for year " + yearStr + ": " + valStr);
                }
            }
            System.out.println(unitDataMap);

            data.put(unitName, unitDataMap);
        }
        return data;
    }

    private static Document _parse_xml_data(HttpResponse<String> response) throws IOException {
        Document doc;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input = new ByteArrayInputStream(response.body().getBytes());
            doc = builder.parse(input);
            doc.getDocumentElement().normalize();
        }
        catch (ParserConfigurationException e) {
            System.err.println("Error in XML parser configuration: " + e.getMessage());
            return null;
        } catch (SAXException e) {
            System.err.println("XML parsing error: " + e.getMessage());
            return null;
        }
        return doc;
    }

    private static HttpResponse<String> _get_xml_doc() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://bdl.stat.gov.pl/api/v1/data/by-variable/"+variable_id+"?unit-level=2&format=xml&page-size=100"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("Error: Received HTTP status " + response.statusCode());
            return null;
        }
        return response;
    }

    public static void print_data() throws InterruptedException, IOException {
        HashMap<String, HashMap<Integer, Double>> data = get_data();
        assert data != null;

        for (Map.Entry<String, HashMap<Integer, Double>> unitEntry : data.entrySet()) {
            String unitName = unitEntry.getKey();
            HashMap<Integer, Double> yearValueMap = unitEntry.getValue();
            System.out.println("Unit: " + unitName);

            for (Map.Entry<Integer, Double> yearEntry : yearValueMap.entrySet()) {
                Integer year = yearEntry.getKey();
                Double value = yearEntry.getValue();
                System.out.println("\tYear: " + year + " => Value: " + value);
            }
        }
    }
}
