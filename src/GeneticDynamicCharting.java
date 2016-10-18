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

public class GeneticDynamicCharting {
    ArrayBlockingQueue<ChromosomeData> sharedBlockingQueue;
    private static final Random random = new Random();

    public GeneticDynamicCharting(ArrayBlockingQueue<ChromosomeData> sharedBlockingQueue) {
        this.sharedBlockingQueue = sharedBlockingQueue;
    }

    private void display() {
        JFrame f = new JFrame("Genetic Chart");
        final JPanel jtp = new JPanel();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jtp.add(createPane());
        f.add(jtp, BorderLayout.CENTER);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private ChartPanel createPane() {
        final XYSeries minFitnessSeries = new XYSeries("Salesman MinFitness");
        final XYSeries maxFitnessSeries = new XYSeries("Salesman MaxFitness");
        final XYSeries averageFitnessSeries = new XYSeries("Salesman AverageFitness");
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(minFitnessSeries);
        dataset.addSeries(maxFitnessSeries);
        dataset.addSeries(averageFitnessSeries);

        new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChromosomeData cd = getChromosomeData();
                minFitnessSeries.add((int) minFitnessSeries.getItemCount(), cd.getMinFitnessValue());
                maxFitnessSeries.add((int) maxFitnessSeries.getItemCount(), cd.getMaxFitnessValue());
                averageFitnessSeries.add((int) averageFitnessSeries.getItemCount(), cd.getAverageFitness());

                Chromosome fittest = cd.getMinFittest();
            }
        }).start();
        JFreeChart chart = ChartFactory.createXYLineChart("SalesmanPath", "X",
                "Y", dataset, PlotOrientation.VERTICAL, true, true, true);
        return new ChartPanel(chart) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(720, 480);
            }
        };
    }

    private ChromosomeData getChromosomeData() {
        ChromosomeData chromosomeData = sharedBlockingQueue.poll();
        if(chromosomeData == null) {
            try {
                Thread.sleep(1);
                return getChromosomeData();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        return chromosomeData;
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
