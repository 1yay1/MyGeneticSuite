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
import org.jfree.chart.annotations.Annotation;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.Value;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class GeneticDynamicCharting {
    private final ArrayBlockingQueue<ChromosomeData> sharedBlockingQueue;
    private final List<Population> populationList;
    private final Map<String, Population> idPopulationMap;
    private final int maxGenerations;

    public GeneticDynamicCharting(List<Population> populationList, int maxGenerations) {
        this.populationList = populationList;
        this.sharedBlockingQueue = new ArrayBlockingQueue<>(100);
        this.idPopulationMap = new HashMap<>();
        for (Population p : populationList) {
            idPopulationMap.put(p.getId(), p);
        }
        this.maxGenerations = maxGenerations;
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
    static int i = 0;
    private ChartPanel createPane() {
        Map<String, Map<String, XYSeries>> idXYseriesMap = new HashMap<>();
        Map<String, XYTextAnnotation> idAnnotationMap = new HashMap<>();

        idPopulationMap.forEach((id, p) -> {
            Map<String, XYSeries> tempMap = new HashMap<>();
            tempMap.put("min", new XYSeries(String.format("%s MinFitness", id))) ;
            tempMap.put("max", new XYSeries(String.format("%s MaxFitness", id))) ;
            tempMap.put("avg", new XYSeries(String.format("%s AvgFitness", id)));
            idXYseriesMap.put(id, tempMap);

            XYTextAnnotation annotation = new XYTextAnnotation(String.format("%s MinFitness", id),0,0);
            idAnnotationMap.put(id,annotation);
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
                    XYTextAnnotation annotation = idAnnotationMap.get(cd.getId());
                    annotation.setText(String.format("%s MinFitnes: %s",cd.getId(),Double.toString(cd.getMinFitnessValue())));
                    annotation.setX(min.getItemCount());
                    annotation.setY(cd.getMinFitnessValue());

                    min.add((int) min.getItemCount(), cd.getMinFitnessValue());
                    max.add((int) max.getItemCount(), cd.getMaxFitnessValue());
                    avg.add((int) avg.getItemCount(), cd.getAverageFitnessValue());
                    //}
                    i++;
                }
            }
        }).start();
        JFreeChart chart = ChartFactory.createXYLineChart("SalesmanPath", "X",
                "Y", dataset, PlotOrientation.VERTICAL, true, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        ValueAxis rangeAxis = plot.getRangeAxis();
        ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setRange(new Range(0,10000));
        for(XYTextAnnotation a: idAnnotationMap.values()){
            plot.addAnnotation(a);
        }


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
                    new Thread(new GeneticProducer(maxGenerations, p, sharedBlockingQueue)).start();
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
