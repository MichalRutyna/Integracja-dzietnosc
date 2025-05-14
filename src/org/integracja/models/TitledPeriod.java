package org.integracja.models;

public class TitledPeriod {
    public int year_start;
    public int year_end;

    public String title;

    public TitledPeriod(int year_start, int year_end, String title) {
        this.year_start = year_start;
        this.year_end = year_end;
        this.title = title;
    }
}
