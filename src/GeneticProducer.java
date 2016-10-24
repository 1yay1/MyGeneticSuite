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
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by yay on 16.10.2016.
 */
public class GeneticProducer implements Runnable {
    private volatile boolean running; //running boolean for the run method
    private final ArrayBlockingQueue<ChromosomeData> sharedBlockingQueue;
    private final Population population;
    private static final int DEFAULT_MAX_GENERATIONS = 10000;
    int maxGenerations;
    private final Double maxFitness;

    public GeneticProducer(int maxGenerations, Double maxFitness, Population pop, ArrayBlockingQueue<ChromosomeData> sharedBlockingQueue) {
        this.sharedBlockingQueue = sharedBlockingQueue;
        this.population = pop;
        this.maxGenerations = maxGenerations;
        this.maxFitness = maxFitness;
    }

    public GeneticProducer(int maxGenerations, Population pop, ArrayBlockingQueue<ChromosomeData> sharedBlockingQueue) {
        this(maxGenerations, null, pop, sharedBlockingQueue);
    }

    public GeneticProducer(Population pop, ArrayBlockingQueue<ChromosomeData> sharedBlockingQueue) {
        this(DEFAULT_MAX_GENERATIONS, pop, sharedBlockingQueue);
    }

    /**
     * Overwritten run method of the Runnable interface.
     * Offers all {@link Chromosome} Objects of the {@link Population} Object to the shared blocking queue.
     * Then it evolves the population another generation.
     */
    @Override
    public void run() {

        this.running = true;
        int i = 0;
        /*SalesmanPopulation.CITY_HASH_MAP.forEach((v,k) -> {
            System.out.println(v.toString() + ":" +k.toString());
        });*/
        ChromosomeData chromosomeData = null;
        while (running && i < maxGenerations) {
            chromosomeData = new ChromosomeData(population.getId(), population.getChromosomeList(), i);
            if (sharedBlockingQueue.offer(chromosomeData)) {
                if(maxFitness != null) {
                    if(chromosomeData.getMaxFitnessValue() == maxFitness.doubleValue()) {
                        break;
                    }
                }
                population.evolve();
                i++;
            }
        }
    }

    /**
     * Sets the volatile private boolean value checked regularly by the run method to false.
     * This stops the while loop in the run method.
     */
    public void shutdown() {
        if (running) {
            running = false;
        }
    }

    /**
     * return maxGenerations
     *
     * @return
     */
    public int getMaxGenerations() {
        return maxGenerations;
    }
}
