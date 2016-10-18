/**
 * Created by yay on 18.10.2016.
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @see http://stackoverflow.com/a/15715096/230513
 * @see http://stackoverflow.com/a/11949899/230513
 */
public class GeneticDynamicCharting {


    ArrayBlockingQueue<ChromosomeData> sharedBlockingQueue;
    private static final int N = 128;
    private static final Random random = new Random();
    private int n = 1;

    public GeneticDynamicCharting(ArrayBlockingQueue<ChromosomeData> sharedBlockingQueue) {
        this.sharedBlockingQueue = sharedBlockingQueue;
    }

    private void display() {
        JFrame f = new JFrame("TabChart");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JPanel jtp = new JPanel();
        jtp.add(createPane());
        f.add(jtp, BorderLayout.CENTER);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private ChartPanel createPane() {
        final XYSeries series = new XYSeries("Salesman MinFitness");

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        new Timer(0, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                series.add((int) series.getItemCount(), getMinFitness());
            }
        }).start();
        JFreeChart chart = ChartFactory.createXYLineChart("SalesmanPath", "X",
                "Y", dataset, PlotOrientation.VERTICAL, true, true, true);
        return new ChartPanel(chart) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(480, 240);
            }
        };
    }

    private double getMinFitness() {
        ChromosomeData chromosomeData = sharedBlockingQueue.poll();
        if(chromosomeData == null) {
            try {
                Thread.sleep(1);
                return getMinFitness();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        return chromosomeData.getMinFitnessValue();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ArrayBlockingQueue<ChromosomeData> sharedBlockingQueue = new ArrayBlockingQueue<ChromosomeData>(100);
                GeneticProducer producer = new GeneticProducer(new SalesmanPopulation(), sharedBlockingQueue);
                new Thread(producer).start();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                new GeneticDynamicCharting(sharedBlockingQueue).display();
            }
        });
    }
}
