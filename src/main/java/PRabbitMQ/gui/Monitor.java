package PRabbitMQ.gui;

import PRabbitMQ.client.DataBuffer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.IntConsumer;

public class Monitor extends JFrame { //Crea a interfaz gráfica

    private final DataBuffer dataBuffer;
    private final IntConsumer subscripcion;
    private final Runnable peche;
    private JLabel statusLabel;
    private JSpinner durSpinner;
    private boolean pechado;
    private RRGraph grafico;

    public Monitor(DataBuffer dataBuffer, IntConsumer subscripcion, Runnable peche) {
        this.dataBuffer = dataBuffer;
        this.subscripcion = subscripcion;
        this.peche = peche;
        setTitle("Monitor RR");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        initializeComponents();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                pechar();
            }
        });
    }

    private void initializeComponents(){
        setLayout(new BorderLayout(10,10));
        //Cabeceira
        JPanel cabeceira = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("Monitor RR");
        titulo.setFont(new Font("Times New Roman", Font.BOLD, 24));
        statusLabel = new JLabel("Desconectado");
        statusLabel.setForeground(Color.GRAY);
        cabeceira.add(titulo,BorderLayout.WEST);
        cabeceira.add(statusLabel, BorderLayout.EAST);
        cabeceira.setBorder(BorderFactory.createEmptyBorder(15,15,5,15));
        add(cabeceira, BorderLayout.NORTH);
        //Gráfico
        grafico = new RRGraph(dataBuffer);
        add(grafico, BorderLayout.CENTER);
        //Botóns
        JPanel controis = new JPanel(new FlowLayout(FlowLayout.CENTER,15,10));
        JLabel duracion = new JLabel("Duracion (s)");
        durSpinner = new JSpinner(new SpinnerNumberModel(60, 1, 86400, 1)); //Poñemos un máximo de un día, por exemplo
        durSpinner.setPreferredSize(new Dimension(90,30));
        JButton subscribeButton = new JButton("Subscribirse/Renovar");
        subscribeButton.addActionListener(e -> subscribir());
        JButton closeButton = new JButton("Pechar");
        closeButton.addActionListener(e-> pechar());
        controis.add(duracion);
        controis.add(durSpinner);
        controis.add(subscribeButton);
        controis.add(closeButton);

        add(controis, BorderLayout.SOUTH);
    }

    private void subscribir(){
        int segundos = (Integer) durSpinner.getValue();
        subscripcion.accept(segundos);
    }

    public void setStatus(String status){
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(status);
            if(status.equals("Subscripción activa")) {
                statusLabel.setForeground(new Color(0,150,70));
            }
            else {
                statusLabel.setForeground(Color.GRAY);
            }
        });
    }

    private void pechar() {
        if(pechado){
            return;
        }
        pechado = true;
        grafico.deter();
        peche.run();
        dispose();
    }

}
