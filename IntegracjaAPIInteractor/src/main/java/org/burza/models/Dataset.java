package org.burza.models;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Dataset {
    private String name;
    private String type;

    @JsonProperty("variable_id")
    private Integer variableId;

    @JsonProperty("zmienna_id")
    private Integer zmiennaId;

    @JsonProperty("przekroj_id")
    private Integer przekrojId;

    @JsonProperty("okres_id")
    private Integer okresId;

    // Getters
    public String getName() { return name; }
    public String getType() { return type; }
    public Integer getVariableId() { return variableId; }
    public Integer getZmiennaId() { return zmiennaId; }
    public Integer getPrzekrojId() { return przekrojId; }
    public Integer getOkresId() { return okresId; }
}