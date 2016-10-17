import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {

    public static void main(String[] args) {
        // write your code here
        ArrayBlockingQueue<ChromosomeData> sharedBlockingQueue = new ArrayBlockingQueue<>(100);

        GeneticProducer producer = new GeneticProducer(new SalesmanPopulation(), sharedBlockingQueue);
        new Thread(producer).start();
        int generations = 0;
        final XYSeries minFitnessXySeries = new XYSeries("Min Fitness");
        final XYSeries maxFitnessXySeries = new XYSeries("Max Fitness");
        final XYSeries averageFitnessXySeries = new XYSeries("Average Fitness");
        ChromosomeData chromosomeData = null;
        while (generations<300) {
            chromosomeData = sharedBlockingQueue.poll();
            if (chromosomeData != null) {
                generations++;
                minFitnessXySeries.add(generations, chromosomeData.getMinFitnessValue());
                maxFitnessXySeries.add(generations,chromosomeData.getMaxFitnessValue());
                averageFitnessXySeries.add(generations, chromosomeData.getAverageFitness());
            }
        }
        int bestGene[] = chromosomeData.getMinFittest().getGene();
        final XYSeries bestPath = new XYSeries("Best path",false);
        for(int i: bestGene) {
            bestPath.add(SalesmanPopulation.CITY_HASH_MAP.get(i).getX(), SalesmanPopulation.CITY_HASH_MAP.get(i).getY());
        }

        final XYSeriesCollection fitnessDataset = new XYSeriesCollection();
        fitnessDataset.addSeries(averageFitnessXySeries);
        fitnessDataset.addSeries(minFitnessXySeries);
        fitnessDataset.addSeries(maxFitnessXySeries);
        JFreeChart jFreeChart = ChartFactory.createXYLineChart(
                "Default Salesman Settings",
                "Generation",
                "Data",
                fitnessDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                true
        );

        final XYSeriesCollection bestPathDataSet = new XYSeriesCollection();
        bestPathDataSet.addSeries(bestPath);
        JFreeChart pathJFreeChart = ChartFactory.createXYLineChart(
                "Path",
                "x",
                "y",
                bestPathDataSet,
                PlotOrientation.VERTICAL,
                true,
                true,
                true
        );

        XYPlot p = pathJFreeChart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true,true);
        renderer.setUseOutlinePaint(true);
        p.setRenderer(0,renderer);

        try {
            ChartUtilities.saveChartAsPNG(new File("fitnessData.png"), jFreeChart, 640, 480);
            ChartUtilities.saveChartAsPNG( new File("bestPath.png"), pathJFreeChart, 480, 480);
        } catch (IOException e) {

        }
        producer.shutdown();
    }
}
