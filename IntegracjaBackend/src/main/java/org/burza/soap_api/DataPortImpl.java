package org.burza.soap_api;

import com.example.generated.*;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import java.math.BigInteger;


@WebService(
        serviceName = "DataService",
        portName = "DataPort",
        endpointInterface = "com.example.generated.DataPort",
        targetNamespace = "http://example.com/data-service")
@HandlerChain(file = "handler-chain.xml")
public class DataPortImpl implements DataPort {

    private static final RegionYearValueObj[] mock_data = {
            new RegionYearValueObj("North", BigInteger.valueOf(2020), 150.5),
            new RegionYearValueObj("North", BigInteger.valueOf(2021), 165.2),
            new RegionYearValueObj("North", BigInteger.valueOf(2022), 180.7),

            new RegionYearValueObj("South", BigInteger.valueOf(2020), 120.3),
            new RegionYearValueObj("South", BigInteger.valueOf(2021), 125.8),
            new RegionYearValueObj("South", BigInteger.valueOf(2022), 140.2),

            new RegionYearValueObj("East", BigInteger.valueOf(2020), 200.1),
            new RegionYearValueObj("East", BigInteger.valueOf(2021), 210.5),
            new RegionYearValueObj("East", BigInteger.valueOf(2022), 225.8)
    };

    @Resource
    WebServiceContext context;

    ObjectFactory factory = new ObjectFactory();

    @WebMethod
    public GetRegionalDataResponse getRegionalData(GetRegionalDataRequest parameters) {
        GetRegionalDataResponse response = factory.createGetRegionalDataResponse();
        for (RegionYearValueObj obj : mock_data) {
            var test_obj = factory.createRegionYearValueObj();
            test_obj.setRegion(obj.getRegion());
            test_obj.setYear(obj.getYear());
            test_obj.setValue(obj.getValue());
            response.getResult().add(test_obj);
        }
        System.out.println("Server responding");
        return response;
    }
}
