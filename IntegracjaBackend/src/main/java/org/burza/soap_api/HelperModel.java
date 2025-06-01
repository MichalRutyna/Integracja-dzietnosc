package org.burza.soap_api;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigInteger;

@Entity
public class HelperModel {
    protected String region;
    protected BigInteger year;
    protected double value;
    protected String dataset;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public HelperModel(String region, BigInteger year, double value, String dataset) {
        this.region = region;
        this.year = year;
        this.value = value;
        this.dataset = dataset;
    }

    public HelperModel() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
