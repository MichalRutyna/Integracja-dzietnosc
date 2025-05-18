package org.burza.soap_api;

import com.example.generated.*;

import jakarta.annotation.Resource;
import jakarta.jws.HandlerChain;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import java.math.BigInteger;


@WebService(
        serviceName = "DataService",
        portName = "DataPort",
        endpointInterface = "com.example.generated.DataPort",
        targetNamespace = "http://example.com/data-service")
@HandlerChain(file = "handler-chain.xml")
public class DataPortImpl implements DataPort {

    private static final HelperModel[] mock_data = {
            new HelperModel("North", BigInteger.valueOf(2020), 150.5),
            new HelperModel("North", BigInteger.valueOf(2021), 165.2),
            new HelperModel("North", BigInteger.valueOf(2022), 180.7),

            new HelperModel("South", BigInteger.valueOf(2020), 120.3),
            new HelperModel("South", BigInteger.valueOf(2021), 125.8),
            new HelperModel("South", BigInteger.valueOf(2022), 140.2),

            new HelperModel("East", BigInteger.valueOf(2020), 200.1),
            new HelperModel("East", BigInteger.valueOf(2021), 210.5),
            new HelperModel("East", BigInteger.valueOf(2022), 225.8)
    };

    @Resource
    WebServiceContext context;

    ObjectFactory factory = new ObjectFactory();

    @WebMethod
    public GetRegionalDataResponse getRegionalData(GetRegionalDataRequest parameters) {
        GetRegionalDataResponse response = factory.createGetRegionalDataResponse();
        for (HelperModel obj : mock_data) {
            var test_obj = factory.createRegionYearValueObj();
            test_obj.setRegion(obj.region);
            test_obj.setYear(obj.year);
            test_obj.setValue(obj.value);
            response.getResult().add(test_obj);
        }
        System.out.println("Server responding");
        return response;
    }
}
