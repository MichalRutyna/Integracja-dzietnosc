package org.burza.models;

import net.bytebuddy.dynamic.loading.InjectionClassLoader;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class RegionYearValueObj {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    public String region;
    public Integer year;
    public Double value;

    public RegionYearValueObj(Long id, String region, Integer year, Double value) {
        this.id = id;
        this.region = region;
        this.year = year;
        this.value = value;
    }
}
