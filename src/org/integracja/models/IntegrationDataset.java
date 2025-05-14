package org.integracja.models;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.TimeSeriesCollection;

public class IntegrationDataset {
    public int id;
    public String name;
    public String description;

    public TimeSeriesCollection dataset;

    private static int max_id = -1;

    public IntegrationDataset(String name, String description, TimeSeriesCollection dataset) {
        this.name = name;
        this.description = description;
        this.dataset = dataset;

        max_id++;
        this.id = max_id;
    }

    @Override
    public String toString() {
        return "IntegrationDataset{" +
                "name='" + name + '\'' +
                '}';
    }
}
