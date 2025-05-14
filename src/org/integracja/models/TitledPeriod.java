package org.integracja.models;

import java.util.Date;

public class TitledPeriod {
    public Date start_date;
    public Date end_date;

    public String title;

    public TitledPeriod(Date start_date, Date end_date, String title) {
        this.start_date = start_date;
        this.end_date = end_date;
        this.title = title;
    }
}
