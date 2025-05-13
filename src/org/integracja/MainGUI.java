package org.integracja;

import org.integracja.gui.ChartGUITab;
import org.integracja.gui.DownloadGUITab;

import javax.swing.*;

public class MainGUI extends JFrame {
    MainGUI() {
        setTitle("Data Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        JTabbedPane tabPanel = new JTabbedPane(SwingConstants.LEFT);

        JPanel page1 = new ChartGUITab();

        JPanel page2 = new DownloadGUITab();

        JPanel page3 = new JPanel();
        page3.add(new JLabel("This is Tab 3"));

        tabPanel.addTab("Graph data", page1);
        tabPanel.addTab("Download data", page2);
//        tabPanel.addTab("Numerical data", page3);

        add(tabPanel);
    }
}
