package org.integracja;

import org.w3c.dom.Document;
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

public class ApiBDLInteractor {
    public static int variable_id = 217230;


    static HttpClient client = HttpClient.newHttpClient();

    public static void _get_data() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://bdl.stat.gov.pl/api/v1/data/by-variable/"+variable_id+"?unit-level=2&format=xml&page-size=100"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("Error: Received HTTP status " + response.statusCode());
            return;
        }
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
            return;
        } catch (SAXException e) {
            System.err.println("XML parsing error: " + e.getMessage());
            return;
        }

        System.out.println(doc.getDocumentElement().getNodeName());
        String someValue = doc.getElementsByTagName("tagName").item(0).getTextContent();
        System.out.println("Value: " + someValue);
    }
}
