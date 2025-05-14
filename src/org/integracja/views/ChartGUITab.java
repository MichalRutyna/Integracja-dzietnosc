package org.integracja.views;

import org.integracja.controllers.GUIController;
import org.integracja.models.IntegrationDataset;
import org.integracja.models.TitledPeriod;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.Layer;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.time.DateRange;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChartGUITab extends JPanel {
    private final JPanel side_panel;
    private JPanel chart_section;
    private JPanel checkbox_panel;

    // probably load all datasets on startup
    private Set<IntegrationDataset> loaded_datasets = new HashSet<>();

    private final Set<String> selectedRowKeys = new HashSet<>();
    private final Set<IntegrationDataset> selectedDatasets = new HashSet<>();
    private final Set<TitledPeriod> selectedPeriods = new HashSet<>();

    public ChartGUITab() {
        setLayout(new BorderLayout());

        add(createChartSection(), BorderLayout.CENTER);

        JPanel bottom_panel = createBottomPanel();
        add(bottom_panel, BorderLayout.SOUTH);

        side_panel = new JPanel();
        drawSidePanel();
        add(side_panel, BorderLayout.EAST);

        displayFilteredDatasets();
    }

    public void updateLoadedDatasets() {
        loaded_datasets = GUIController.getLoadedDatasets();
        displayFilteredDatasets();
        drawSidePanel();
    }

    private JComponent createChartSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        chart_section = panel;

        JScrollPane scroll_pane = new JScrollPane(panel);
        scroll_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll_pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        return scroll_pane;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel temp_buttons = new JPanel();

        // temporary testing
//        JButton loadDataButton = new JButton("Load Data");
//        loadDataButton.addActionListener(new displayButtonActionListener(GUIController.loadFertilityFromDatabase, "Fertility"));
//        loadDataButton.addActionListener(new displayButtonActionListener(GUIController.loadInflationFromDatabase, "Inflation"));
//        temp_buttons.add(loadDataButton);

        panel.add(temp_buttons);
        panel.add(createRegionPanel());
        panel.add(createPeriodsPanel());

        return panel;
    }

    private JComponent createPeriodsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        var loaded_periods = GUIController.loadPeriodsFromDatabase();
        for (TitledPeriod period : loaded_periods) {
            JCheckBox checkBox = new JCheckBox(period.title, false); // initially unchecked
            checkBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedPeriods.add(period);
                } else {
                    selectedPeriods.remove(period);
                }
                displayFilteredDatasets();
            });
            panel.add(checkBox);
        }

        if (loaded_periods.isEmpty()) {
            panel.add(new JLabel("No periods defined!"));
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        return scrollPane;
    }

    private JComponent createRegionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        checkbox_panel = panel;
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        panel.add(new JLabel("No regions"));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        return scrollPane;
    }

    private void drawSidePanel() {
        side_panel.removeAll();
        side_panel.setLayout(new BoxLayout(side_panel, BoxLayout.Y_AXIS));
        side_panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        for (IntegrationDataset dataset : loaded_datasets) {
            JCheckBox checkBox = new JCheckBox(dataset.name, false);
            checkBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedDatasets.add(dataset);
                } else {
                    selectedDatasets.remove(dataset);
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
        chart_section.removeAll();

        if (loaded_datasets.isEmpty()) {
            chart_section.add(new ChartPanel(null));
            return;
        }
        System.out.println(selectedDatasets);
        for (IntegrationDataset dataset : selectedDatasets) {
            JFreeChart chart = ChartFactory.createTimeSeriesChart(
                    "Data Chart", "Year", dataset.name, null);
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
                Date start = new GregorianCalendar(2000, Calendar.DECEMBER, 31).getTime();
                Date end = new GregorianCalendar(2024, Calendar.DECEMBER, 31).getTime();
                xAxis.setRange(new DateRange(start, end));
            }

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
//            var renderer = new DefaultXYItemRenderer();
//            plot.setRenderer(dataset.id, renderer);
            var chart_panel = new ChartPanel(chart);
            chart_section.add(chart_panel);

            displaySelectedPeriodsOnPlot(plot);
        }

        if (checkbox_panel.getComponentCount() == 1) {
            // maybe change to loading like periods?
            createCategoryCheckboxes(((IntegrationDataset)loaded_datasets.toArray()[0]).dataset);
        }
        chart_section.revalidate();
        chart_section.repaint();
        System.out.println(chart_section.getComponentCount());
    }

    private void displaySelectedPeriodsOnPlot(XYPlot plot) {
        for (TitledPeriod period : selectedPeriods) {
            displayTitledPeriodOnPlot(plot, period);
        }
    }

    private void displayTitledPeriodOnPlot(XYPlot plot, TitledPeriod period) {
        IntervalMarker intervalMarker = new IntervalMarker(period.start_date.getTime(), period.end_date.getTime());
        intervalMarker.setLabel(period.title);
        intervalMarker.setLabelAnchor(RectangleAnchor.BOTTOM);
        intervalMarker.setLabelTextAnchor(TextAnchor.BOTTOM_CENTER);
        intervalMarker.setPaint(new Color(222, 222, 255, 128));

        plot.addDomainMarker(intervalMarker, Layer.BACKGROUND);

    }

    private void createCategoryCheckboxes(TimeSeriesCollection dataset) {
        checkbox_panel.removeAll();
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
}
