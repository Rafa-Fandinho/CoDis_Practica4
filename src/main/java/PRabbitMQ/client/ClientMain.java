package PRabbitMQ.client;

import PRabbitMQ.gui.Monitor;

import javax.swing.*;

public class ClientMain {

    public static void main(String[] argv) throws Exception {
        DataBuffer buffer = new DataBuffer();
        RRSubscriber subscriber = new RRSubscriber(buffer);
        SubscriptionManager manager = new SubscriptionManager(subscriber, buffer);
        SwingUtilities.invokeLater(() -> {
            Monitor monitor = new Monitor(buffer, manager::subscribe, manager::termino);
            manager.setStatusListener(monitor::setStatus);
            monitor.setVisible(true);
        });
    }
}
