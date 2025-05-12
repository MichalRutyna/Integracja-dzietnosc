package org.integracja;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.io.IOException;
import java.util.Set;


public class Main {

    public static void createChartFromDataset(DefaultCategoryDataset dataset, String title, String category_label, String value_label) {
        JFreeChart chart = ChartFactory.createLineChart(
                title,
                category_label,
                value_label,
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(chart);
    }

    public static void createFrameFromChart(ChartPanel chartPanel) {
        JFrame frame = new JFrame("Test chart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        frame.setContentPane(chartPanel);
        frame.setLocationRelativeTo(null); // center on screen
        frame.setVisible(true);
    }

    public static void suitableVariables() throws IOException, InterruptedException {
//        for (String o : ApiSDPInteractor.getSuitableVariables(Set.of(282), null)) {
        for (String o : ApiSDPInteractor.getSuitableVariables(Set.of(282), Set.of(2, 155))) {
            System.out.println(o);
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
//        DefaultCategoryDataset dataset = DatasetCreators.getFertilityAllRegionsDataset(2000);
//        createChartFromDataset(dataset, "Lubelskie", "Rok", "Dzietność");
////
////        DefaultCategoryDataset dataset2 = getInflationAllRegionsDataset();
////        createChartFromDataset(dataset2, "Inflation", "Year", "Inflation");
//
//        DefaultCategoryDataset dataset2 = DatasetCreators.getGeneralSDPVariableDataset(282, 2, 282, ApiSDPInteractor.Wymiar.WOJEWODZTWA, 2010);
//        createChartFromDataset(dataset2, "Krzewy", "Year", "Krzewy");

//        suitableVariables();
        SwingUtilities.invokeLater(() -> {
            ChartWithButton app = new ChartWithButton();
            app.setVisible(true);
        });
    }
}