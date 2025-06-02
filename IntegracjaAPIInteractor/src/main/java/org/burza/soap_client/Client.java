package org.burza.soap_client;

import com.example.generated.*;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;


public class Client extends WebServiceGatewaySupport {

    public SaveDataResponse postData(String country) {
        SaveDataRequest request = new SaveDataRequest();

        SaveDataResponse response = (SaveDataResponse) getWebServiceTemplate()
            .marshalSendAndReceive(
                "http://localhost:8080/data-service", 
                request,
                new HeaderModifier()
            );

        return response;
    }

}