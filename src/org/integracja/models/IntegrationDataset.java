package org.integracja.models;

import org.jfree.data.category.DefaultCategoryDataset;

public class IntegrationDataset {
    public int id;
    public String name;
    public String description;

    public DefaultCategoryDataset dataset;

    private static int max_id = -1;

    public IntegrationDataset(String name, String description, DefaultCategoryDataset dataset) {
        this.name = name;
        this.description = description;
        this.dataset = dataset;

        max_id++;
        this.id = max_id;
    }
}
