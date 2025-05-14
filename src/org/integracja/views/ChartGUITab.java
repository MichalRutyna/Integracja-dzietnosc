package org.integracja.views;

import org.integracja.controllers.GUIController;
import org.integracja.models.IntegrationDataset;
import org.integracja.models.TitledPeriod;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.ui.Layer;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.time.DateRange;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChartGUITab extends JPanel {
    private final ChartPanel chartPanel;

    private final JPanel bottom_panel;
    private final JPanel side_panel;
    private JPanel checkbox_panel;

    // probably load all datasets on startup
    private final ArrayList<IntegrationDataset> loaded_datasets = new ArrayList<>();

    private final Set<String> selectedRowKeys = new HashSet<>();
    private final Set<Integer> selectedDatasets = new HashSet<>();

    public ChartGUITab() {
        setLayout(new BorderLayout());

        chartPanel = new ChartPanel(null);
        displayFilteredDatasets();
        add(chartPanel, BorderLayout.CENTER);

        bottom_panel = createBottomPanel();
        add(bottom_panel, BorderLayout.SOUTH);

        side_panel = new JPanel();
        initializeSidePanel();
        add(side_panel, BorderLayout.EAST);

        displayFilteredDatasets();
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel temp_buttons = new JPanel();
        // temporary testing
        JButton loadDataButton = new JButton("Load Data");
        loadDataButton.addActionListener(new displayButtonActionListener(GUIController.loadFertilityFromDatabase, "Fertility"));
        loadDataButton.addActionListener(new displayButtonActionListener(GUIController.loadInflationFromDatabase, "Inflation"));
        temp_buttons.add(loadDataButton);

        JButton period = new JButton("Display markers");
        period.addActionListener(_ ->
                displayTitledPeriodOnPlot(chartPanel.getChart().getCategoryPlot(), new TitledPeriod(2012, 2014, "Koniec świata")));
        temp_buttons.add(period);

        panel.add(temp_buttons);
        panel.add(createCheckBoxPanel());

        return panel;
    }

    private JComponent createCheckBoxPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        checkbox_panel = panel;
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 20, 5));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        return scrollPane;
    }

    private void initializeSidePanel() {
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
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Data Chart", "Year", "Value", null);
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis xAxis = (DateAxis) plot.getDomainAxis();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

        // automatic scaling
        rangeAxis.setAutoRange(true);
        rangeAxis.setAutoRangeIncludesZero(false);

        xAxis.setAutoRange(true);
        xAxis.setDateFormatOverride(new SimpleDateFormat("yyyy"));

        if (plot.getSeriesCount() == 0) {
            xAxis.setAutoRangeMinimumSize(6);
            Date start = new GregorianCalendar(2000, Calendar.JANUARY, 1).getTime();
            Date end = new GregorianCalendar(2024, Calendar.DECEMBER, 31).getTime();
            xAxis.setRange(new DateRange(start, end));
        }

        if (loaded_datasets.isEmpty()) {
            chartPanel.setChart(chart);
            return;
        }

        for (IntegrationDataset dataset : loaded_datasets) {
            if (!selectedDatasets.contains(dataset.id)) { continue; }
            TimeSeriesCollection filtered_dataset;
            try {
                filtered_dataset = (TimeSeriesCollection) dataset.dataset.clone();
            } catch (CloneNotSupportedException e) {
                System.err.println("Clone not supported");
                return;
            } catch (NullPointerException e) {
                System.err.println("Dataset contains no data");
                return;
            }

            for (Object rowKey : dataset.dataset.getSeries()) {
                TimeSeries series = (TimeSeries) rowKey;
                if (!selectedRowKeys.contains(series.getKey().toString())) {
                    filtered_dataset.removeSeries(filtered_dataset.getSeriesIndex(series.getKey()));
                }
            }
            plot.setDataset(dataset.id, filtered_dataset);

            var renderer = new DefaultXYItemRenderer();
            plot.setRenderer(dataset.id, renderer);

        }
        chartPanel.removeAll();
        chartPanel.setChart(chart);

        createCategoryCheckboxes(loaded_datasets.getFirst().dataset);
    }

    private void displayTitledPeriodOnPlot(CategoryPlot plot, TitledPeriod period) {
        CategoryMarker phaseMarker = new CategoryMarker(period.year_start);
        phaseMarker.setLabel(period.title);
        phaseMarker.setLabelAnchor(RectangleAnchor.BOTTOM);
        phaseMarker.setLabelTextAnchor(TextAnchor.CENTER);
        phaseMarker.setLabelOffset(new RectangleInsets(0, 0, 5.0, 0));
        phaseMarker.setPaint(Color.GRAY);
        phaseMarker.setStroke(new BasicStroke(1.5f));
        plot.addDomainMarker(phaseMarker, Layer.BACKGROUND);
    }

    private void createCategoryCheckboxes(TimeSeriesCollection dataset) {

        for (Object rowKey : dataset.getSeries()) {
            TimeSeries series = (TimeSeries) rowKey;
            JCheckBox checkBox = new JCheckBox(series.getKey().toString(), false); // initially unchecked
            checkBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedRowKeys.add(series.getKey().toString());
                } else {
                    selectedRowKeys.remove(series.getKey().toString());
                }
                displayFilteredDatasets();
            });
            checkbox_panel.add(checkBox);
        }
        checkbox_panel.revalidate();
        checkbox_panel.repaint();
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

            new SwingWorker<TimeSeriesCollection, Void>() {
                @Override
                protected TimeSeriesCollection doInBackground() {
                    TimeSeriesCollection dataset;
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
                        initializeSidePanel();
                    } catch (Exception ex) {
                        System.err.println("An error occurred while downloading: " + ex.getMessage() + ", trace: " + Arrays.toString(ex.getStackTrace()));
                        JOptionPane.showMessageDialog(ChartGUITab.this, "Error loading dataset", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
}
