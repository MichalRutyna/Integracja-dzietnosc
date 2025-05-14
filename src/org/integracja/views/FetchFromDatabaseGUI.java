package org.integracja.views;

import org.integracja.controllers.GUIController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class FetchFromDatabaseGUI extends JPanel {
    private final JPanel buttons_panel;
    private final JPanel status_panel;

    public FetchFromDatabaseGUI() {
        setLayout(new BorderLayout());

        buttons_panel = new JPanel();
        String[] fetchable = GUIController.getFetchableDatasets();
        for (String dataset : fetchable) {
            var btn = new JButton(dataset);
            btn.addActionListener(new fetchButtonActionListener(dataset));
            btn.setMargin(new Insets(10, 10, 10, 10));
            buttons_panel.add(btn);
        }
        add(buttons_panel, BorderLayout.CENTER);

        status_panel = new JPanel();
        status_panel.setLayout(new BorderLayout());
        add(status_panel, BorderLayout.SOUTH);
    }


    private class fetchButtonActionListener implements ActionListener {
        String title;

        private static int loading = 0;

        public fetchButtonActionListener(String title) {
            this.title = title;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ImageIcon spinnerIcon = new ImageIcon("src/org/integracja/spinner_trans.gif");
            JLabel loadingLabel = new JLabel("Loading...", spinnerIcon, SwingConstants.CENTER);
            loadingLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            loadingLabel.setVerticalTextPosition(SwingConstants.BOTTOM);


            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    loading += 1;
                    status_panel.removeAll();
                    status_panel.add(loadingLabel, BorderLayout.CENTER);
                    status_panel.revalidate();
                    status_panel.repaint();
                    GUIController.fetchDataset(title);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        System.out.println("Dataset " + title + " loaded");
                        loading -= 1;
                        if (loading == 0) {
                            status_panel.removeAll();
                            status_panel.add(new JLabel("Loading of " + title + " complete!"), BorderLayout.CENTER);
                            status_panel.revalidate();
                            status_panel.repaint();
                        }
                    } catch (Exception ex) {
                        System.err.println("An error occurred while downloading: " + ex.getMessage() + ", trace: " + Arrays.toString(ex.getStackTrace()));
                        JOptionPane.showMessageDialog(FetchFromDatabaseGUI.this, "Error loading dataset", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
}
