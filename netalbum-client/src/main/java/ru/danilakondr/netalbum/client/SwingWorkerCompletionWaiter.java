package ru.danilakondr.netalbum.client;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

class SwingWorkerCompletionWaiter implements PropertyChangeListener {
    private JDialog dialog;

    public SwingWorkerCompletionWaiter(JDialog dialog) {
        this.dialog = dialog;
    }

    public void propertyChange(PropertyChangeEvent event) {
        if ("state".equals(event.getPropertyName())
                && SwingWorker.StateValue.DONE == event.getNewValue()) {
            dialog.setVisible(false);
            dialog.dispose();
        }
    }
}