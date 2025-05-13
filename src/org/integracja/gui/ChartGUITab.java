package org.integracja.gui;

import org.integracja.DatasetCreators;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.HashSet;
import java.util.Set;

public class ChartGUITab extends JPanel {
    private ChartPanel chartPanel;
    private DefaultCategoryDataset mDataset;

    private JPanel panel;

    private Set<String> selectedRowKeys = new HashSet<>();

    public ChartGUITab() {
        // Initialize
        chartPanel = new ChartPanel(null);
        displayFilteredDataset(null);

        // UI Layout
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
        panel = createButtonPanel();
        add(panel, BorderLayout.SOUTH);

    }

    private void displayFilteredDataset(DefaultCategoryDataset dataset) {
        DefaultCategoryDataset filtered_dataset = null;
        try {
            filtered_dataset = (DefaultCategoryDataset) dataset.clone();
        } catch (CloneNotSupportedException e) {
            System.err.println("Clone not supported");
            return;
        } catch (NullPointerException ignored) {}

        if (dataset != null && filtered_dataset != null) {
            for (Object rowKey : dataset.getRowKeys()) {
                if (!selectedRowKeys.contains((String) rowKey)) {
                    filtered_dataset.removeRow((String) rowKey);
                }
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

        if (filtered_dataset == null) {return;}
        for (Object rowKey : dataset.getRowKeys()) {
            JCheckBox checkBox = new JCheckBox((String) rowKey, false); // initially unchecked
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
//        loadDataButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                loadChartData();
//            }
//        });
        loadDataButton.addActionListener(new displayButtonActionListener(GUIController.loadFertilityFromDatabase, "Fertility"));

        panel.add(loadDataButton);
        return panel;
    }

    private class displayButtonActionListener implements ActionListener {
        GUIController.GetDatasetFunction getDatasetFunction;
        String title;

        public displayButtonActionListener(GUIController.GetDatasetFunction getDatasetFunction, String title) {
            this.getDatasetFunction = getDatasetFunction;
            this.title = title;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
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
//                    displaySuccessMessage(" ");
                    dataset = getDatasetFunction.getDataset();
                    mDataset = dataset;
                    return dataset;
                }

                @Override
                protected void done() {
                    try {
//                        displaySuccessMessage(title + " data download successful");
                        System.out.println("Dataset loaded");
                        displayFilteredDataset(get());
                    } catch (Exception ex) {
                        System.err.println("An error occured while downloading: " + ex.getMessage() + ", cause: " + ex.getCause());
                        JOptionPane.showMessageDialog(ChartGUITab.this, "Error loading dataset", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
}
