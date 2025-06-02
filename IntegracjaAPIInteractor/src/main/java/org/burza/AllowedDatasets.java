package org.burza;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.burza.models.Dataset;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AllowedDatasets {

    private static List<Dataset> loadDatasets() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(
                new ClassPathResource("allowed_datasets.json").getInputStream(),
                mapper.getTypeFactory().constructCollectionType(List.class, Dataset.class)
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to load allowed datasets", e);
        }
    }

    public static Set<String> getAllowedDatasets() {
        return loadDatasets().stream()
            .map(Dataset::getName)
            .collect(Collectors.toSet());
    }

    public static Optional<Dataset> getDatasetByName(String name) {
        return loadDatasets().stream()
            .filter(d -> d.getName().equals(name))
            .findFirst();
    }

    public static List<Dataset> getAllDatasets() {
        return loadDatasets();
    }
}