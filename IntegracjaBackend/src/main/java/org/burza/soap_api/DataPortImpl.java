package org.burza.soap_api;

import com.example.generated.*;

import jakarta.annotation.Resource;
import jakarta.jws.HandlerChain;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;


@WebService(
        serviceName = "DataService",
        portName = "DataPort",
        endpointInterface = "com.example.generated.DataPort",
        targetNamespace = "http://example.com/data-service")
@HandlerChain(file = "handler-chain.xml")
public class DataPortImpl implements DataPort {

    private static final List<String> AVAILABLE_DATASETS = Arrays.asList(
        "population", 
        "gdp", 
        "unemployment", 
        "inflation"
    );

    private static final HelperModel[] populationData = {
            new HelperModel("North", BigInteger.valueOf(2020), 1500000.0),
            new HelperModel("North", BigInteger.valueOf(2021), 1520000.0),
            new HelperModel("North", BigInteger.valueOf(2022), 1550000.0),

            new HelperModel("South", BigInteger.valueOf(2020), 2000000.0),
            new HelperModel("South", BigInteger.valueOf(2021), 2050000.0),
            new HelperModel("South", BigInteger.valueOf(2022), 2100000.0),

            new HelperModel("East", BigInteger.valueOf(2020), 1200000.0),
            new HelperModel("East", BigInteger.valueOf(2021), 1210000.0),
            new HelperModel("East", BigInteger.valueOf(2022), 1225000.0)
    };

    private static final HelperModel[] gdpData = {
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

    private static final HelperModel[] unemploymentData = {
            new HelperModel("North", BigInteger.valueOf(2020), 5.2),
            new HelperModel("North", BigInteger.valueOf(2021), 4.8),
            new HelperModel("North", BigInteger.valueOf(2022), 4.5),

            new HelperModel("South", BigInteger.valueOf(2020), 6.1),
            new HelperModel("South", BigInteger.valueOf(2021), 5.9),
            new HelperModel("South", BigInteger.valueOf(2022), 5.7),

            new HelperModel("East", BigInteger.valueOf(2020), 4.5),
            new HelperModel("East", BigInteger.valueOf(2021), 4.3),
            new HelperModel("East", BigInteger.valueOf(2022), 4.0)
    };

    private static final HelperModel[] inflationData = {
            new HelperModel("North", BigInteger.valueOf(2020), 2.1),
            new HelperModel("North", BigInteger.valueOf(2021), 3.5),
            new HelperModel("North", BigInteger.valueOf(2022), 7.8),

            new HelperModel("South", BigInteger.valueOf(2020), 2.3),
            new HelperModel("South", BigInteger.valueOf(2021), 3.7),
            new HelperModel("South", BigInteger.valueOf(2022), 8.1),

            new HelperModel("East", BigInteger.valueOf(2020), 1.9),
            new HelperModel("East", BigInteger.valueOf(2021), 3.2),
            new HelperModel("East", BigInteger.valueOf(2022), 7.5)
    };

    @Resource
    WebServiceContext context;

    ObjectFactory factory = new ObjectFactory();

    @WebMethod
    public GetRegionalDataResponse getRegionalData(GetRegionalDataRequest parameters) {
        GetRegionalDataResponse response = factory.createGetRegionalDataResponse();
        
        // Default to GDP data if no dataset is specified
        HelperModel[] dataToUse = gdpData;
        
        // If a dataset was specified, use the appropriate one
        if (parameters.getDataset() != null && !parameters.getDataset().isEmpty()) {
            switch (parameters.getDataset().toLowerCase()) {
                case "population":
                    dataToUse = populationData;
                    break;
                case "gdp":
                    dataToUse = gdpData;
                    break;
                case "unemployment":
                    dataToUse = unemploymentData;
                    break;
                case "inflation":
                    dataToUse = inflationData;
                    break;
                default:
                    // Use default dataset
                    break;
            }
        }
        
        for (HelperModel obj : dataToUse) {
            var dataObj = factory.createRegionYearValueObj();
            dataObj.setRegion(obj.region);
            dataObj.setYear(obj.year);
            dataObj.setValue(obj.value);
            response.getResult().add(dataObj);
        }
        
        System.out.println("Server responding with dataset: " + 
                          (parameters.getDataset() != null ? parameters.getDataset() : "default"));
        return response;
    }

    @WebMethod
    public GetAvailableDatasetsResponse getAvailableDatasets(GetAvailableDatasetsRequest parameters) {
        GetAvailableDatasetsResponse response = factory.createGetAvailableDatasetsResponse();
        response.getDatasets().addAll(AVAILABLE_DATASETS);
        System.out.println("Server responding with available datasets");
        return response;
    }
}
