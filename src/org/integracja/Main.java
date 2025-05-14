package org.integracja;

import org.integracja.api_interactors.ApiSDPInteractor;
import org.integracja.views.MainGUI;

import javax.swing.*;
import java.io.IOException;
import java.util.Set;


public class Main {

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
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
//                    UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception ignored) {
            System.err.println("Look and feel not working");
        }
        SwingUtilities.invokeLater(() -> {
            MainGUI app = new MainGUI();
            app.setVisible(true);
        });
    }
}