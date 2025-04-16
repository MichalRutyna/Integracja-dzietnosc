package org.integracja;

import org.jfree.chart.ChartFactory;
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

    public static void main(String[] args) throws IOException, InterruptedException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int year = 1999; year < 2024; year++) {
            HashMap<String, Float> wartosci = ApiSDPInteractor.getFormattedData(ApiSDPInteractor.Wymiar.WOJEWODZTWA, year);
            for (String woj : wartosci.keySet()) {
                dataset.addValue((Number)wartosci.get(woj), woj, year);
            }
            Thread.sleep(200);
        }
        HashMap<String, Float> wartosci = ApiSDPInteractor.getFormattedData(ApiSDPInteractor.Wymiar.WOJEWODZTWA, 2023);
        System.out.println(wartosci.size());

//        dataset.addValue(200, "Sales", "January");
//        dataset.addValue(150, "Sales", "February");
//        dataset.addValue(180, "Sales", "March");
//        dataset.addValue(260, "Sales", "April");
//        dataset.addValue(300, "Sales", "May");

        JFreeChart chart = ChartFactory.createLineChart(
                "Współczynnik dzietności",
                "Rok",
                "Dzietność",
                dataset);

        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame frame = new JFrame();
        frame.setSize(800, 600);
        frame.setContentPane(chartPanel);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }
}