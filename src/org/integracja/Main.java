package org.integracja;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.io.IOException;


public class Main {

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
        CategoryAxis categoryAxis = (CategoryAxis) plot.getDomainAxis();
        rangeAxis.setAutoRange(true);
        rangeAxis.setAutoRangeIncludesZero(false);
        JFrame frame = new JFrame();
        frame.setSize(800, 600);
        frame.setContentPane(chartPanel);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        DefaultCategoryDataset dataset = DatasetCreators.getFertilitySingleRegionDataset("Lubelskie");
        createChartFromDataset(dataset, "Lubelskie", "Rok", "Dzietność");
//
//        DefaultCategoryDataset dataset2 = getInflationAllRegionsDataset();
//        createChartFromDataset(dataset2, "Inflation", "Year", "Inflation");

//        for (String o : ApiSDPInteractor.getSuitableVariables(null, Set.of(282))) {
//            System.out.println(o);
//        }
        System.out.println(ApiSDPInteractor.getPostionNames());
        DefaultCategoryDataset dataset2 = DatasetCreators.getGeneralSDPVariableDataset(354, 2, 282, ApiSDPInteractor.Wymiar.WOJEWODZTWA);
        createChartFromDataset(dataset2, "Krzewy", "Year", "Krzewy");
    }
}