/**
 * Created by yay on 18.10.2016.
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Class that takes care of all the GUI charting.
 *
 */
public class GeneticDynamicCharting {
    protected final ArrayBlockingQueue<ChromosomeData> sharedBlockingQueue;
    protected final List<Population> populationList;
    protected final Map<String, Population> idPopulationMap;
    protected final Map<String, Map<String, XYSeries>> idXYseriesMap;
    protected final Map<String, XYTextAnnotation> idAnnotationMap;

    protected final int maxGenerations;
    public static final int SIXTY_TIMES_PER_SECOND = 1000 / 60;

    public GeneticDynamicCharting(List<Population> populationList, int maxGenerations) {
        this.populationList = populationList;
        this.maxGenerations = maxGenerations;

        this.sharedBlockingQueue = new ArrayBlockingQueue<>(100);
        this.idPopulationMap = new HashMap<>();
        for (Population p : populationList) {
            idPopulationMap.put(p.getId(), p);
        }
        this.idAnnotationMap = new HashMap<>();
        this.idXYseriesMap = new HashMap<>();
    }

    /**
     * Renders the chart
     */
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

    /**
     * Creates the XYSeriesCollection for the JFree chart.
     * Here everything that actually gets plotted is created.
     * @return
     */
    private XYSeriesCollection setupSeriesColletion() {
        idPopulationMap.forEach((id, p) -> {
            Map<String, XYSeries> tempMap = new HashMap<>();
            tempMap.put("min", new XYSeries(String.format("%s MinFitness", id)));
            tempMap.put("max", new XYSeries(String.format("%s MaxFitness", id)));
            tempMap.put("avg", new XYSeries(String.format("%s AvgFitness", id)));
            idXYseriesMap.put(id, tempMap);

            XYTextAnnotation annotation = new XYTextAnnotation(String.format("%s MinFitness", id), 0, 0);
            idAnnotationMap.put(id, annotation);
        });

        XYSeriesCollection dataset = new XYSeriesCollection();
        idXYseriesMap.forEach((id, map) -> {
            dataset.addSeries(map.get("min"));
            dataset.addSeries(map.get("max"));
            dataset.addSeries(map.get("avg"));

        });
        return dataset;
    }

    /**
     * Sets up the Timer that updates the chart by grabbing new data from the sharedBlockingQueue
     * @return
     */
    protected Timer setupTimer() {
        return new Timer(SIXTY_TIMES_PER_SECOND, (e) -> {
            int i = 0;
            int maxNewData = 100;
            while (i < maxNewData) {
                ChromosomeData cd = sharedBlockingQueue.poll();
                if (cd == null) break;
                Map<String, XYSeries> map = idXYseriesMap.get(cd.getId());
                XYSeries min = map.get("min");
                XYSeries max = map.get("max");
                XYSeries avg = map.get("avg");
                XYTextAnnotation annotation = idAnnotationMap.get(cd.getId());
                annotation.setText(String.format("%s MinFitnes: %s", cd.getId(), Double.toString(cd.getMinFitnessValue())));
                annotation.setX(min.getItemCount());
                annotation.setY(cd.getMinFitnessValue());

                min.add((int) min.getItemCount(), cd.getMinFitnessValue());
                max.add((int) max.getItemCount(), cd.getMaxFitnessValue());
                avg.add((int) avg.getItemCount(), cd.getAverageFitnessValue());
                i++;
            }
        }
        );
    }

    /**
     * Creates the ChartPanel that displays the chart.
     * Here the setupSeriesColletion, setupTimer, setupChart methods are called.
     * @return
     */
    protected ChartPanel createPane() {
        XYSeriesCollection seriesCollection = setupSeriesColletion();
        setupTimer().start();

        JFreeChart chart = setupChart(seriesCollection);
        return new ChartPanel(chart) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(720, 480);
            }
        };
    }

    /**
     * Method to setup the JFree Chart object.
     * @param seriesCollection the XYSeriesCollection that holds all the data
     * @return
     */
    protected JFreeChart setupChart(XYSeriesCollection seriesCollection) {
        JFreeChart chart = ChartFactory.createXYLineChart("Genetic", "X",
                "Y", seriesCollection, PlotOrientation.VERTICAL, true, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();

        ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setRange(new Range(0, 10000));
        for (XYTextAnnotation a : idAnnotationMap.values()) {
            plot.addAnnotation(a);
        }
        return chart;
    }

    /**
     * Starts the GUI.
     * Sets up the GeneticProducers for each Population Object int eh populationList.
     * displays the chart.
     */
    public void start() {
        EventQueue.invokeLater(() -> {
            for (Population p : populationList) {
                new Thread(new GeneticProducer(maxGenerations, p, sharedBlockingQueue)).start();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            display();
        });
    }
}
