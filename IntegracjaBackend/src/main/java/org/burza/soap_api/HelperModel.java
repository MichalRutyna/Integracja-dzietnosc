package org.burza.soap_api;

import java.math.BigInteger;

public class HelperModel {
    protected String region;
    protected BigInteger year;
    protected double value;

    public HelperModel(String region, BigInteger year, double value) {
        this.region = region;
        this.year = year;
        this.value = value;
    }
}
