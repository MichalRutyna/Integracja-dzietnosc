package org.integracja.views;

import javax.swing.*;

public class MainGUI extends JFrame {
    public MainGUI() {
        setTitle("Data Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        JTabbedPane tabPanel = new JTabbedPane(SwingConstants.LEFT);

        JPanel page1 = new ChartGUITab();

        JPanel page2 = new DownloadGUITab();

        JPanel page3 = new JPanel();

        tabPanel.addTab("Graph data", page1);
        tabPanel.addTab("Download data", page2);
        tabPanel.addTab("Fetch data from database", page3);
//        tabPanel.addTab("Numerical data", page3);

        add(tabPanel);
    }
}
