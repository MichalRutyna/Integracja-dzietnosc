package org.integracja.views;

import org.integracja.controllers.GUIController;
import org.integracja.models.IntegrationDataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ChartGUITab extends JPanel {
    private ChartPanel chartPanel;

    private JPanel bottom_panel;
    private JPanel side_panel;

    // probably load all datasets on startup
    private ArrayList<IntegrationDataset> loaded_datasets = new ArrayList<>();

    private Set<String> selectedRowKeys = new HashSet<>();
    private Set<Integer> selectedDatasets = new HashSet<>();

    public ChartGUITab() {
        setLayout(new BorderLayout());

        chartPanel = new ChartPanel(null);
        displayFilteredDatasets();
        add(chartPanel, BorderLayout.CENTER);

        bottom_panel = createButtonPanel();
        add(bottom_panel, BorderLayout.SOUTH);

        side_panel = new JPanel();
        createSidePanel();
        add(side_panel, BorderLayout.EAST);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();

        // temporary testing
        JButton loadDataButton = new JButton("Load Data");
        loadDataButton.addActionListener(new displayButtonActionListener(GUIController.loadFertilityFromDatabase, "Fertility"));
        loadDataButton.addActionListener(new displayButtonActionListener(GUIController.loadInflationFromDatabase, "Inflation"));
        panel.add(loadDataButton);

        return panel;
    }
    private void createSidePanel() {
        side_panel.removeAll();
        side_panel.setLayout(new BoxLayout(side_panel, BoxLayout.Y_AXIS));
        side_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        for (IntegrationDataset dataset : loaded_datasets) {
            System.out.println(dataset.name);
            JCheckBox checkBox = new JCheckBox(dataset.name, false);
            checkBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedDatasets.add(dataset.id);
                } else {
                    selectedDatasets.remove(dataset.id);
                }
                displayFilteredDatasets();
            });
            side_panel.add(checkBox);
        }

        if (loaded_datasets.isEmpty()) {
            side_panel.add(new JLabel("No datasets loaded!"));
        }

        side_panel.revalidate();
        side_panel.repaint();
    }

    private void displayFilteredDatasets() {
        JFreeChart chart = ChartFactory.createLineChart(
                "Data Chart", "Year", "Value", null);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

        // automatic scaling
        rangeAxis.setAutoRange(true);
        rangeAxis.setAutoRangeIncludesZero(false);

        if (loaded_datasets.isEmpty()) {
            chartPanel.setChart(chart);
            return;
        }

        for (IntegrationDataset dataset : loaded_datasets) {
            if (!selectedDatasets.contains(dataset.id)) { continue; }
            DefaultCategoryDataset filtered_dataset = null;
            try {
                filtered_dataset = (DefaultCategoryDataset) dataset.dataset.clone();
            } catch (CloneNotSupportedException e) {
                System.err.println("Clone not supported");
                return;
            } catch (NullPointerException e) {
                System.err.println("Dataset contains no data");
                return;
            }

            for (Object rowKey : dataset.dataset.getRowKeys()) {
                if (!selectedRowKeys.contains((String) rowKey)) {
                    filtered_dataset.removeRow((String) rowKey);
                }
            }

            var renderer = new LineAndShapeRenderer();
            plot.setDataset(dataset.id, filtered_dataset);
            plot.setRenderer(dataset.id, renderer);

        }
        chartPanel.removeAll();
        chartPanel.setChart(chart);

        createCategoryCheckboxes(loaded_datasets.getFirst().dataset);
    }

    private void createCategoryCheckboxes(DefaultCategoryDataset dataset) {
        for (Object rowKey : dataset.getRowKeys()) {
            JCheckBox checkBox = new JCheckBox((String) rowKey, false); // initially unchecked
            checkBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedRowKeys.add((String) rowKey);
                } else {
                    selectedRowKeys.remove((String) rowKey);
                }
                displayFilteredDatasets();
            });
            bottom_panel.add(checkBox);
        }
        bottom_panel.revalidate();
        bottom_panel.repaint();
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
            ImageIcon spinnerIcon = new ImageIcon("src/org/integracja/spinner_trans.gif");
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
                    loaded_datasets.add(new IntegrationDataset(title, "", dataset));
                    return dataset;
                }

                @Override
                protected void done() {
                    try {
//                        displaySuccessMessage(title + " data download successful");
                        System.out.println("Dataset loaded");
                        displayFilteredDatasets();
                        createSidePanel();
                    } catch (Exception ex) {
                        System.err.println("An error occured while downloading: " + ex.getMessage() + ", cause: " + ex.getCause());
                        JOptionPane.showMessageDialog(ChartGUITab.this, "Error loading dataset", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
}
