package org.integracja;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.HashSet;
import java.util.Set;

public class ChartWithButton extends JFrame {
    private ChartPanel chartPanel;
    private DefaultCategoryDataset mDataset;

    private JPanel panel;

    private Set<String> selectedRowKeys = new HashSet<>();

    public ChartWithButton() {
        setTitle("Data Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        // Initialize
        chartPanel = new ChartPanel(null);
        displayDataset(null);

        // UI Layout
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
        panel = createButtonPanel();
        add(panel, BorderLayout.SOUTH);

    }

    private void displayDataset(CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createLineChart(
                "Data Chart", "Category", "Value", dataset);

        // automatic scaling
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        //CategoryAxis categoryAxis = (CategoryAxis) plot.getDomainAxis();
        rangeAxis.setAutoRange(true);
        rangeAxis.setAutoRangeIncludesZero(false);

        chartPanel.removeAll();
        chartPanel.setChart(chart);
    }

    private void displayFilteredDataset(DefaultCategoryDataset dataset) {
        DefaultCategoryDataset filtered_dataset = null;
        try {
            filtered_dataset = (DefaultCategoryDataset) dataset.clone();
        } catch (CloneNotSupportedException e) {
            System.err.println("Clone not supported");
            return;
        }

        for (Object rowKey : dataset.getRowKeys()) {
            if (!selectedRowKeys.contains((String) rowKey)) {
                filtered_dataset.removeRow((String) rowKey);
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Data Chart", "Category", "Value", filtered_dataset);

        // automatic scaling
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        //CategoryAxis categoryAxis = (CategoryAxis) plot.getDomainAxis();
        rangeAxis.setAutoRange(true);
        rangeAxis.setAutoRangeIncludesZero(false);

        chartPanel.removeAll();
        chartPanel.setChart(chart);

        for (Object rowKey : dataset.getRowKeys()) {
            JCheckBox checkBox = new JCheckBox((String) rowKey, false); // initially checked
            checkBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedRowKeys.add((String) rowKey);
                } else {
                    selectedRowKeys.remove((String) rowKey);
                }
                if (mDataset != null) {
                    displayFilteredDataset(mDataset);
                }
                else {
                    System.out.println("Dataset is null");
                }
            });
            panel.add(checkBox);
        }
        panel.revalidate();
        panel.repaint();
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        JButton loadDataButton = new JButton("Load Data");

        // Button action
        loadDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadChartData();
            }
        });

        panel.add(loadDataButton);
        return panel;
    }

    private void loadChartData() {
        ImageIcon spinnerIcon = new ImageIcon("src/org/integracja/spinner.gif");
        JLabel loadingLabel = new JLabel("Loading...", spinnerIcon, SwingConstants.CENTER);
        loadingLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        loadingLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

        chartPanel.setChart(null);
        chartPanel.setLayout(new BorderLayout());
        chartPanel.add(loadingLabel, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();

        new SwingWorker<DefaultCategoryDataset, Void>() {
            @Override
            protected DefaultCategoryDataset doInBackground() {
                DefaultCategoryDataset dataset = null;
                try {
                    dataset = DatasetCreators.getFertilityAllRegionsDataset(2000);
                    mDataset = dataset;
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage() + " in getting dataset");
                }
                return dataset;
            }

            @Override
            protected void done() {
                try {
                    displayFilteredDataset(get());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(chartPanel, "Error loading chart", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
