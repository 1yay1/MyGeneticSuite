import java.util.*;
import java.util.function.Function;
import java.util.logging.MemoryHandler;

/**
 * ChromosomeData class.
 * Is used by GeneticProducer als a result of Production.
 * Holds a list of chromosomes, and an id which refers to a population that created the list of chromosomes.
 * Created by yay on 17.10.2016.
 */
public class ChromosomeData {

    interface CallMethod {
        void runMethod(ChromosomeData d);
    }

    private final List<Chromosome> chromosomeList;
    private final String id;
    private final static Map<METHOD, CallMethod> methodMap;
    private int currentGeneration;

    //not used atm
    public enum METHOD{
        AVG,
        MIN,
        MAX
    }

    //not used atm
    static{
        methodMap = new EnumMap<>(METHOD.class);

    }

    public int getCurrentGeneration() {
        return currentGeneration;
    }

    ChromosomeData(String id, List<Chromosome> chromosomeList, int currentGeneration) {
        this.id = id;
        this.chromosomeList = chromosomeList;
        this.currentGeneration = currentGeneration;
    }

    /**
     * getter for the chromosome list
     * @return
     */
    public List<Chromosome> getChromosomeList() {
        return chromosomeList;
    }

    /**
     * getter for the id
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * returns the chromosome with the highest fitness
     * @return
     */
    public Chromosome getMaxFittest() {
        return Collections.max(chromosomeList);
    }

    /**
     * returns the value of the fittest chromosome
     * @return
     */
    public double getMaxFitnessValue() {
        return Collections.max(chromosomeList).getFitness();
    }

    /**
     * returns the chromosome with the lowest fitness
     * @return
     */
    public Chromosome getMinFittest() {
        return Collections.min(chromosomeList);
    }

    /**
     * returns the fitness value of the chromosome with the lowest fitness
     * @return
     */
    public double getMinFitnessValue() {
        return Collections.min(chromosomeList).getFitness();
    }

    /**
     * returns the average fitness value of the list of chromosomes.
     * @return
     */
    public double getAverageFitnessValue() {
        return chromosomeList
                .stream()
                .mapToDouble(Chromosome::getFitness)
                .average()
                .getAsDouble();
    }
}
