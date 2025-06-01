package org.burza;


import jakarta.xml.ws.Endpoint;
import org.burza.soap_api.Auth;
import org.burza.soap_api.DataPortImpl;



public class Main {

    public static void main(String[] args) {
        DataPortImpl implementor = new DataPortImpl();
        String address = "http://0.0.0.0:8080/data-service";

        try {
            Endpoint endpoint = Endpoint.create(implementor);
            endpoint.publish(address);
            System.out.println("Service running at " + address + "?wsdl");

            System.out.println("JWT token for testing: " + Auth.generateToken("test"));
        } catch (Exception e) {
            System.err.println("Failed to publish endpoint: " + e.getMessage());
            e.printStackTrace();
        }

    }
}