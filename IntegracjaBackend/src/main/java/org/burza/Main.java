package org.burza;


import org.burza.soap_api.DataPortImpl;

import javax.xml.ws.Endpoint;


public class Main {
    public static void main(String[] args) {
        Endpoint.publish("http://localhost:8080/data-service", new DataPortImpl());
        System.out.println("Service running at http://localhost:8080/data-service?wsdl");
    }
}