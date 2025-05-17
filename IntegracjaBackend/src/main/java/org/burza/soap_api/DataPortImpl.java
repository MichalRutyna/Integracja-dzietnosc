package org.burza.soap_api;

import com.example.generated.DataPort;
import com.example.generated.GetRegionalDataRequest;
import com.example.generated.GetRegionalDataResponse;
import com.example.generated.ObjectFactory;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.math.BigInteger;

@WebService(
        serviceName = "DataService",
        portName = "DataPort",
        endpointInterface = "com.example.generated.DataPort",
        targetNamespace = "http://example.com/data-service")
public class DataPortImpl implements DataPort {
    ObjectFactory factory = new ObjectFactory();

    @WebMethod
    public GetRegionalDataResponse getRegionalData(GetRegionalDataRequest parameters) {
        GetRegionalDataResponse response = factory.createGetRegionalDataResponse();
        var test_obj = factory.createRegionYearValueObj();
        test_obj.setRegion("Poland");
        test_obj.setYear(BigInteger.valueOf(2020));
        test_obj.setValue(33.21);
        response.getResult().add(test_obj);
        return response;
    }
}
