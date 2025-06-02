package org.burza.soap_client;

import com.example.generated.*;
import org.burza.models.RegionYearValueObj;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import java.math.BigInteger;
import java.util.ArrayList;


public class Client extends WebServiceGatewaySupport {

    ObjectFactory factory = new ObjectFactory();

    public SaveDataResponse postData(String dataset, ArrayList<RegionYearValueObj> data) {
        SaveDataRequest request = new SaveDataRequest();

        request.setDataset(dataset);

        for (RegionYearValueObj obj : data) {
            var dataObj = factory.createRegionYearValueObj();
            dataObj.setRegion(obj.region);
            dataObj.setYear(BigInteger.valueOf(obj.year));
            dataObj.setValue(obj.value);
            request.getData().add(dataObj);
        }
        System.out.println("Sending data to db");
        SaveDataResponse response = (SaveDataResponse) getWebServiceTemplate()
            .marshalSendAndReceive(
                "http://localhost:8080/data-service",
                request,
                new HeaderModifier()
            );

        System.out.println("Response from soap: " + response.getMessage());
        return response;
    }

}