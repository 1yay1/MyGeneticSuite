/**
 * Created by yay on 18.10.2016.
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.*;
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
    private final ArrayBlockingQueue<ChromosomeData> sharedBlockingQueue;
    private final List<Population> populationList;
    private final Map<String, Population> idPopulationMap;


    public GeneticDynamicCharting(List<Population> populationList) {
        this.populationList = populationList;
        this.sharedBlockingQueue = new ArrayBlockingQueue<>(100);
        this.idPopulationMap = new HashMap<>();
        for (Population p : populationList) {
            idPopulationMap.put(p.getId(), p);
        }
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
        Map<String, Map<String, XYSeries>> idXYseriesMap = new HashMap<>();

        idPopulationMap.forEach((id, p) -> {
            Map<String, XYSeries> tempMap = new HashMap<>();
            tempMap.put("min", new XYSeries(String.format("Salesman %s MinFitness", id)));
            tempMap.put("max", new XYSeries(String.format("Salesman %s MaxFitness", id)));
            tempMap.put("avg", new XYSeries(String.format("Salesman %s AvgFitness", id)));
            idXYseriesMap.put(id, tempMap);
        });

        XYSeriesCollection dataset = new XYSeriesCollection();
        idXYseriesMap.forEach((id, map) -> {
            dataset.addSeries(map.get("min"));
            dataset.addSeries(map.get("max"));
            dataset.addSeries(map.get("avg"));

        });


        new Timer(1000 / 60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = 0;
                int maxNewData = 100;
                while (i < maxNewData) {
                    ChromosomeData cd = getChromosomeData();
                    if (cd == null) break;
                    //if (idXYseriesMap.containsKey(cd.getId())) {
                    Map<String, XYSeries> map = idXYseriesMap.get(cd.getId());
                    XYSeries min = map.get("min");
                    XYSeries max = map.get("max");
                    XYSeries avg = map.get("avg");

                    min.add((int) min.getItemCount(), cd.getMinFitnessValue());
                    max.add((int) max.getItemCount(), cd.getMaxFitnessValue());
                    avg.add((int) avg.getItemCount(), cd.getAverageFitnessValue());
                    //}
                    i++;
                }
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
        return sharedBlockingQueue.poll();
    }

    public void start() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (Population p : populationList) {
                    new Thread(new GeneticProducer(p, sharedBlockingQueue)).start();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
                display();
            }
        });
    }
}
