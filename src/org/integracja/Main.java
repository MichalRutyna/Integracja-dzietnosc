package org.integracja;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;


public class Main {

    final static Integer ZMIENNA = 589; // Współczynnik dzietności
    final static Integer PRZEKROJ = 155; // Polska, województwa, powiaty; Charakter miejscowości
    // Druga opcja to 429 - Polska, makroregiony, regiony, podregiony; Charakter miejscowości
    final static Integer OKRES = 282; // Roczny

    public static DefaultCategoryDataset getFertilityAllRegionsDataset() throws InterruptedException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int year = 1999; year < 2024; year++) {
            HashMap<String, Float> wartosci = ApiSDPInteractor.getFormattedData(ApiSDPInteractor.Wymiar.WOJEWODZTWA, year);
            for (String woj : wartosci.keySet()) {
                dataset.addValue((Number)wartosci.get(woj), woj, year);
            }
            Thread.sleep(200);
        }
        return dataset;
    }

    public static DefaultCategoryDataset getFertilitySingleRegionDataset(String region_name) throws InterruptedException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int year = 1999; year < 2024; year++) {
            HashMap<String, Float> wartosci = ApiSDPInteractor.getFormattedData(ApiSDPInteractor.Wymiar.WOJEWODZTWA, year);
            dataset.addValue((Number)wartosci.get(region_name), region_name, year);
            Thread.sleep(200);
        }
        return dataset;
    }

    public static void createChartFromDataset(DefaultCategoryDataset dataset, String title, String category_label, String value_label) {
        JFreeChart chart = ChartFactory.createLineChart(
                title,
                category_label,
                value_label,
                dataset
        );
        ChartPanel chartPanel = new ChartPanel(chart);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRange(true);
        rangeAxis.setAutoRangeIncludesZero(false);
        JFrame frame = new JFrame();
        frame.setSize(800, 600);
        frame.setContentPane(chartPanel);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws InterruptedException {
        DefaultCategoryDataset dataset = getFertilitySingleRegionDataset("Lubelskie");
        createChartFromDataset(dataset, "Lubelskie", "Rok", "Dzietność");
    }
}