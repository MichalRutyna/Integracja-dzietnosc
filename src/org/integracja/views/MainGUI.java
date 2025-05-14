package org.integracja.views;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame {
    public MainGUI() {
        setTitle("Data Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        JTabbedPane tabPanel = new JTabbedPane(SwingConstants.LEFT);

        Font biggerFont = new Font("Dialog", Font.PLAIN, 12);
        UIManager.put("TabbedPane.font", biggerFont);

        UIManager.put("TabbedPane.tabInsets", new Insets(10, 5, 10, 5)); // top, left, bottom, right
        SwingUtilities.updateComponentTreeUI(tabPanel);

        ChartGUITab page1 = new ChartGUITab();
        DownloadGUITab page2 = new DownloadGUITab();
        FetchFromDatabaseGUI page3 = new FetchFromDatabaseGUI();

        tabPanel.addChangeListener(e -> {
            if (tabPanel.getSelectedIndex() == 0) {
                page1.updateLoadedDatasets();
            }
        });

        tabPanel.addTab("Graph data", page1);
        tabPanel.addTab("Download data", page2);
        tabPanel.addTab("Fetch data from database", page3);
//        tabPanel.addTab("Numerical data", page3);

        add(tabPanel);
    }
}
