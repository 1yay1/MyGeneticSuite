import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by yay on 16.10.2016.
 */
public class GeneticProducer implements Runnable {
    private volatile boolean running; //running boolean for the run method
    private final ArrayBlockingQueue<ChromosomeData> sharedBlockingQueue;
    private final Population population;

    public GeneticProducer(Population pop, ArrayBlockingQueue<ChromosomeData> sharedBlockingQueue) {
        this.sharedBlockingQueue = sharedBlockingQueue;
        this.population = pop;
    }

    /**
     * Overwritten run method of the Runnable interface.
     * Offers all {@link Chromosome} Objects of the {@link Population} Object to the shared blocking queue.
     * Then it evolves the population another generation.
     */
    @Override
    public void run() {
        this.running = true;
        while (running) {
            ChromosomeData chromosomeData = new ChromosomeData(population.getId(), population.getChromosomeList());
            sharedBlockingQueue.offer(chromosomeData);
            population.evolve();
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
}
