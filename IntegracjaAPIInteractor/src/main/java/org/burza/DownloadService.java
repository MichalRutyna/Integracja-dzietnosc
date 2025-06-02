package org.burza;

import com.example.generated.SaveDataResponse;
import org.burza.models.Dataset;
import org.burza.models.RegionYearValueObj;
import org.burza.models.responses.DownloadStatusResponse;
import org.burza.soap_client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.*;
import java.util.concurrent.*;

@Service
public class DownloadService {
    private final Map<UUID, Integer> taskProgress = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Autowired
    Client client;

    public UUID startTask(String dataset) {
        Set<String> allowed_datasets = AllowedDatasets.getAllowedDatasets();
        UUID taskId = UUID.randomUUID();
        taskProgress.put(taskId, 0);

        if (!allowed_datasets.contains(dataset)) {
            return null;
        }

        Future<Void> future = executor.submit(() -> {
            ArrayList<RegionYearValueObj> data;
            Optional<Dataset> t = AllowedDatasets.getDatasetByName(dataset);
            Dataset datasetObj = t.orElseThrow();
            data = DownloadController.downloadDataset(datasetObj, value -> taskProgress.put(taskId, value/2));

            System.out.println(data);
            client.postData(dataset, data);

            taskProgress.put(taskId, 100);
            return null;
        });
        return taskId;
    }

    public DownloadStatusResponse getProgress(UUID taskId) {
        Integer progress = taskProgress.get(taskId);
        if (progress == null) {
            throw new NoSuchElementException("Task not found");
        }
        String status;
        if (progress < 50) {
            status = "Downloading";
        }
        else if (progress < 100) {
            status = "Uploading to database";
        }
        else if (progress == 100) {
            status = "Finished";
        }
        else {
            status = "Error";
        }
        return new DownloadStatusResponse(status, progress);
    }
}
