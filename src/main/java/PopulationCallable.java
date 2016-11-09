import org.jzy3d.maths.Coord3d;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.DoubleAccumulator;

/**
 * Created by yay on 09.11.2016.
 */
public class PopulationCallable implements Callable<Double> {
    private Population population;
    private int maxGenerations;
    private double maxFitness;


    public PopulationCallable(Population population, int maxGenerations, double maxFitness) {
        this.population = population;
        this.maxGenerations = maxGenerations;
        this.maxFitness = maxFitness;
    }

    @Override
    public Double call() throws Exception {
        int currentGen = 0;

        while (currentGen < maxGenerations) {
            population.evolve();
            currentGen++;
            if (population.getMaxFittest().getFitness() >= maxFitness) {
                return new Double(currentGen);
            }
        }

        return new Double(maxGenerations);
    }
}
