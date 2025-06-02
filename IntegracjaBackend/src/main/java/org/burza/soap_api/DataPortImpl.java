package org.burza.soap_api;

import com.example.generated.*;
import jakarta.annotation.Resource;
import jakarta.jws.HandlerChain;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import org.burza.database.DatabaseInteractor;

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

    @Resource
    WebServiceContext context;

    ObjectFactory factory = new ObjectFactory();

    @WebMethod
    public GetRegionalDataResponse getRegionalData(GetRegionalDataRequest parameters) {
        GetRegionalDataResponse response = factory.createGetRegionalDataResponse();

        List<HelperModel> dataToUse = List.of();

        if (parameters.getDataset() != null && !parameters.getDataset().isEmpty()) {
            dataToUse = DatabaseInteractor.get(parameters.getDataset().toLowerCase());
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
        var datasets = DatabaseInteractor.getAvailableDatasets();
        if (datasets.isEmpty()) {
            System.err.println("No datasets available");
            throw new RuntimeException("No datasets available");
        }
        response.getDatasets().addAll(datasets);
        System.out.println("Server responding with available datasets: " + datasets);
        return response;
    }

    @WebMethod
    public SaveDataResponse saveData(SaveDataRequest request) {
        System.out.println("Recieved save request: " + request);
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

        System.out.println("Saving " + models.size() + " records to dataset: " + request.getDataset());
        DatabaseInteractor.save(models);
        response.setMessage("Saved " + models.size() + " records to dataset: " + request.getDataset());
        return response;
    }

}
