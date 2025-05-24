package org.burza.rest_api;

import org.burza.DownloadController;
import org.burza.models.RegionYearValueObj;
import org.burza.models.responses.DownloadStatusResponse;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.*;

@Service
public class DownloadService {
    private final Map<UUID, Integer> taskProgress = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();


    public Set<String> getAllowedDatasets() {
        return Set.of("Inflation", "Fertility");
    }

    public UUID startTask(String dataset) {
        Set<String> allowed_datasets = getAllowedDatasets();
        UUID taskId = UUID.randomUUID();
        taskProgress.put(taskId, 0);

        if (!allowed_datasets.contains(dataset)) {
            return null;
        }

        Future<Void> future = executor.submit(() -> {
            ArrayList<RegionYearValueObj> data;
            switch (dataset) {
                case "Fertility" -> data = DownloadController.downloadFertility(value -> taskProgress.put(taskId, value/2)); // it's 50% progress
                case "Inflation" -> data = DownloadController.downloadInflation(value -> taskProgress.put(taskId, value/2));
                default -> throw new NoSuchElementException("This endpoint doesn't support such dataset but it passed filtering");

            }
            // TODO Push to database, if done by many requests, please update progress, else:
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
