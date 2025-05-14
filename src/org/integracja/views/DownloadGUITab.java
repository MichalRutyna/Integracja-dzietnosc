package org.integracja.views;

import org.integracja.controllers.GUIController;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DownloadGUITab extends JPanel {
    private final JProgressBar progressBar;
    private final JLabel successLabel;

    public DownloadGUITab() {
        setLayout(new BorderLayout(10, 10));

        JPanel buttonGroupsPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel group1 = new JPanel(new GridLayout(3, 1, 5, 5));
        group1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "SDP API",
                TitledBorder.LEFT, TitledBorder.TOP));

        group1.add(createDownloadButton(GUIController.downloadFertility, "Fertility"));

        JPanel group2 = new JPanel(new GridLayout(3, 1, 5, 5));
        group2.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "BDL API",
                TitledBorder.LEFT, TitledBorder.TOP));

        group2.add(createDownloadButton(GUIController.downloadInflation, "Inflation"));

        buttonGroupsPanel.add(group1);
        buttonGroupsPanel.add(group2);

        successLabel = new JLabel(" ");
        successLabel.setHorizontalAlignment(SwingConstants.CENTER);
        successLabel.setForeground(new Color(0, 128, 0));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        JPanel lowerPanel = new JPanel(new BorderLayout(5, 5));
        lowerPanel.add(successLabel, BorderLayout.NORTH);
        lowerPanel.add(progressBar, BorderLayout.SOUTH);

        add(buttonGroupsPanel, BorderLayout.CENTER);
        add(lowerPanel, BorderLayout.SOUTH);
    }

    private JButton createDownloadButton(GUIController.DownloadIntoDatabaseFunction download_function, String data_title) {
        JButton b = new JButton(data_title);
        b.addActionListener(new downloadButtonActionListener(download_function, data_title));
        return b;
    }



    /**
     * A listener for all the download buttons
     */
    private class downloadButtonActionListener implements ActionListener {
        GUIController.DownloadIntoDatabaseFunction download_function;
        String title;

        public downloadButtonActionListener(GUIController.DownloadIntoDatabaseFunction download_function, String title) {
            this.download_function = download_function;
            this.title = title;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    DefaultCategoryDataset dataset = null;
                    // button for loading data from the database
                    progressBar.setValue(progressBar.getMinimum());
                    displaySuccessMessage(" ");
                    download_function.download(new ProgressCallback());
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        displaySuccessMessage(title + " data download successful");
                    } catch (Exception ex) {
                        System.err.println("An error occured while downloading: " + ex.getMessage() + ", cause: " + ex.getCause());
                        JOptionPane.showMessageDialog(DownloadGUITab.this, "Error downloading data", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    private void displaySuccessMessage(String message) {
        successLabel.setText(message);
    }

    // Public method to increment the progress bar
    public void incrementProgress(int amount) {
        int current = progressBar.getValue();
        int newValue = Math.min(current + amount, progressBar.getMaximum());
        progressBar.setValue(newValue);
    }

    public class ProgressCallback implements GUIController.Callback {
        @Override
        public void call(int value) {
            incrementProgress(value);
        }
    }


}
