package PRabbitMQ.gui;

import PRabbitMQ.client.DataBuffer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RRGraph  extends JPanel {  //Clase responsable de representar o gráfico

    private final DataBuffer dataBuffer;
    private final XYSeries series;
    private final Timer timer;

    public RRGraph(DataBuffer dataBuffer) {
        this.dataBuffer = dataBuffer;
        //Aproveitamos o constructor para xerar a xanela gráfica
        //Creamos a serie temporal
        series = new XYSeries("RR", false, true);
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        //Creamos o gráfico
        JFreeChart chart = ChartFactory.createXYLineChart("Intervalor RR", "Tempo (s)", "Intervalo RR (s)", dataset, PlotOrientation.VERTICAL, false, true, false);
        //Configuramos o seu aspecto
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        //Engadimos o gráfico ao JPanel
        ChartPanel chartPanel = new ChartPanel(chart);
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
        //E, finalmente, iniciamos a actualización periódica
        timer = new Timer (250, e -> actualizarGrafico());
        timer.start();
        actualizarGrafico();
    }

    private void actualizarGrafico() {
        ArrayList<Double> puntos = dataBuffer.getBuffer();
        series.clear();
        for(int i=0; i< puntos.size(); i++){
            series.add(i-puntos.size()+1, puntos.get(i));
        }
    }

    public void deter() {
        timer.stop();
    }

}
