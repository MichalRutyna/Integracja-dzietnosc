package org.burza.soap_api;

import com.example.generated.*;
import jakarta.annotation.Resource;
import jakarta.jws.HandlerChain;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@WebService(
        serviceName = "DataService",
        portName = "DataPort",
        endpointInterface = "com.example.generated.DataPort",
        targetNamespace = "http://example.com/data-service")
@HandlerChain(file = "handler-chain.xml")
public class DataPortImpl implements DataPort {

    public static final List<String> AVAILABLE_DATASETS = Arrays.asList(
        "population", 
        "gdp", 
        "unemployment", 
        "inflation"
    );

    private static final List<HelperModel> populationData = DatabaseInteractor.get("population");
    private static final List<HelperModel> gdpData = DatabaseInteractor.get("gdp");
    private static final List<HelperModel> unemploymentData = DatabaseInteractor.get("unemployment");
    private static final List<HelperModel> inflationData = DatabaseInteractor.get("inflation");

    @Resource
    WebServiceContext context;

    ObjectFactory factory = new ObjectFactory();

    @WebMethod
    public GetRegionalDataResponse getRegionalData(GetRegionalDataRequest parameters) {
        GetRegionalDataResponse response = factory.createGetRegionalDataResponse();

        List<HelperModel> dataToUse = gdpData;
        
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

    @WebMethod
    public SaveDataResponse saveData(SaveDataRequest request) {
        SaveDataResponse response = factory.createSaveDataResponse();

        List<RegionYearValueObj> items = request.getData();
        ArrayList<HelperModel> models = new ArrayList<>();

        for (RegionYearValueObj item : items) {
            HelperModel model = new HelperModel();
            model.region = item.getRegion();
            model.year = item.getYear();
            model.value = item.getValue();
            model.dataset = request.getDataset();
            models.add(model);
        }

        DatabaseInteractor.save(models);

        response.setMessage("Saved " + models.size() + " records to dataset: " + request.getDataset());
        return response;
    }

}
