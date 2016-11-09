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
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class Main {
    private static void test() {
        // write your code here
        ArrayBlockingQueue<ChromosomeData> sharedBlockingQueue = new ArrayBlockingQueue(100);

        GeneticProducer producer = new GeneticProducer(new SalesmanPopulation(), sharedBlockingQueue);
        new Thread(producer).start();
        int generations = 0;
        final XYSeries minFitnessXySeries = new XYSeries("Min Fitness");
        final XYSeries maxFitnessXySeries = new XYSeries("Max Fitness");
        final XYSeries averageFitnessXySeries = new XYSeries("Average Fitness");
        ChromosomeData chromosomeData = null;
        while (generations < 3000) {
            chromosomeData = sharedBlockingQueue.poll();
            if (chromosomeData != null) {
                generations++;
                minFitnessXySeries.add(generations, chromosomeData.getMinFitnessValue());
                maxFitnessXySeries.add(generations, chromosomeData.getMaxFitnessValue());
                averageFitnessXySeries.add(generations, chromosomeData.getAverageFitnessValue());
            }
        }
        List<Number> bestGene = chromosomeData.getMinFittest().getGene();
        final XYSeries bestPath = new XYSeries("Best path", false);
        for (Number n : bestGene) {
            bestPath.add(SalesmanPopulation.getCity(n.intValue()).getX(), SalesmanPopulation.getCity(n.intValue()).getY());
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
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
        renderer.setUseOutlinePaint(true);
        p.setRenderer(0, renderer);

        try {
            ChartUtilities.saveChartAsPNG(new File("fitnessData.png"), jFreeChart, 640, 480);
            ChartUtilities.saveChartAsPNG(new File("bestPath.png"), pathJFreeChart, 480, 480);
        } catch (IOException e) {

        }
        producer.shutdown();

    }

    private static void test2() {
        // Define a function to plot
        Mapper mapper = new Mapper() {
            public double f(double x, double y) {
                return 100 * Math.sin(x / 10) * Math.cos(y / 20) * x;
            }
        };

        // Define range and precision for the function to plot
        Range range = new Range(-0.1f, 0.15f);
        int steps = 20;

        // Create a surface drawing that function
        Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(false);
        surface.setWireframeColor(Color.BLACK);

        // Create a chart and add the surface
        Chart chart = new AWTChart(Quality.Advanced);
        chart.add(surface);
        chart.open("Jzy3d Demo", 600, 600);
    }

    public static void main(String[] args) {
        test2();
    }
}
