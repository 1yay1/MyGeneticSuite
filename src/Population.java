import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * Created by yay on 12.10.2016.
 */
public abstract class Population {
    private static Logger LOGGER = Logger.getLogger(Population.class.getName());

    private int populationSize;
    private float elitismRate;
    private float crossoverRate;
    private float mutationRate;
    private List<Chromosome> chromosomeList;
    private final String id;

    /**
     * Abstract constructor for a new Instance of Population.
     * Throws {@link IllegalArgumentException} if the mutationRate and crossoverRate are not between 0 and 1.
     * Fills the chromosomeList with random Chromosomes, using the abstract method generateRandomChromosome.
     * Then the list is sorted by fitness value.
     *
     * @param populationSize size of chromsomeList
     * @param mutationRate   rate of mutation, has to be between 0 and 1
     * @param crossoverRate  rate of crossover, has to be between 0 and 1
     */

    /**
     * Gets the private static Logger Object for the class.
     *
     * @return LOGGER object.
     */
    public static Logger getLOGGER() {
        return LOGGER;
    }

    protected Population(String id, int populationSize, float mutationRate, float crossoverRate, float elitismRate) throws IllegalArgumentException {
        if (mutationRate < 0 || mutationRate >= 1 || crossoverRate < 0 || crossoverRate >= 1) {
            throw new IllegalArgumentException("mutationRate, CrossoverRate must both be <= 1 and < 0");
        }
        this.id = id;
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismRate = elitismRate;
        this.chromosomeList = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {
            chromosomeList.add(generateRandomChromosome());
        }
        Collections.sort(chromosomeList);
    }

    /**
     * Getter for the id. Id is the name of the population.
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the crossoverRate
     *
     * @return float value of the crossover rate
     */
    public float getCrossoverRate() {
        return crossoverRate;
    }

    /**
     * Simple getter for the elitismRate
     *
     * @return float value of the elitismRate
     */
    public float getElitismRate() {
        return elitismRate;
    }

    /**
     * Returns the mutationRate
     *
     * @return float value of the mutation rate
     */
    public float getMutationRate() {
        return mutationRate;
    }

    /**
     * Returns the population size
     *
     * @return int value of the population size
     */
    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * Returns a reference to the chromosomeList
     *
     * @return
     */
    public List<Chromosome> getChromosomeList() {
        return this.chromosomeList;
    }

    /**
     * Gets the highest fitness value Chromosome. The Population List has to be sorted,
     * but it always should be sorted in the first place. Then we can grab the last index Chromosome.
     *
     * @return highest fitness value Chromosome
     */
    public Chromosome getMaxFittest() {
        return getMaxFittest(chromosomeList);
    }

    public static Chromosome getMaxFittest(List<Chromosome> chromosomeList){
        Collections.sort(chromosomeList);
        return chromosomeList.get(chromosomeList.size()-1);
    }

    /**
     * Gets the lowest fitness value Chromosome. The Population List has to be sorted,
     * but it always should be sorted in the first place. Then we can grab the last index Chromosome.
     *
     * @return lowest fitness Chromosome
     */
    public Chromosome getMinFittest() {
        return getMinFittest(chromosomeList);
    }

    public static Chromosome getMinFittest(List<Chromosome> chromosomeList){
        Collections.sort(chromosomeList);
        return chromosomeList.get(0);
    }


    /**
     * Calculates the average fitness value of the complete population
     *
     * @return
     */
    public static double getAverageFitness(List<Chromosome> chromosomeList) {
        return chromosomeList
                .stream()
                .mapToDouble(Chromosome::getFitness)
                .average()
                .getAsDouble();
    }

    /**
     * Sets a new chromosomeList if the new length is the same as the old populationSize.
     * Sorts the new List.
     *
     * @param chromosomeList
     * @throws IllegalArgumentException
     */
    public void setChromosomeList(List<Chromosome> chromosomeList) throws IllegalArgumentException {
        this.chromosomeList = chromosomeList;
        Collections.sort(chromosomeList);
    }

    /**
     * Sums all fitness values, gets a random double multiplicated with the sum.
     * Uses that value as selection value, loops through all fitnesses and substracts the fitness of each index.
     * Once the selection value is smaller than 0, we return the current index.
     * If value never gets below 0, we return last index.
     *
     * @return int of the index of the Chromosome in the chromosomeList
     */
    protected int rouletteSelect() {
        int[] fitnessArray = new int[populationSize];
        for (int i = 0; i < populationSize; i++) {
            fitnessArray[i] = (int) chromosomeList.get(i).getFitness();
        }
        int fitnessSum = IntStream.of(fitnessArray).sum();

        double select = ThreadLocalRandom.current().nextDouble() * fitnessSum;
        for (int i = 0; i < populationSize; i++) {
            select -= fitnessArray[i];
            if (select <= 0) return i;
        }
        return populationSize - 1;
    }

    /**
     * Selects a Chromosome via tournament of size tournamentSize.
     * Select n candidates, and get the index of the fittest.
     *
     * @param tournamentSize amount of candidates to choose from
     * @return index in the populationArray of the chosen Chromosome
     */
    protected int tournamentSelectMax(int tournamentSize) {
        List<Chromosome> shuffledPopulation = new ArrayList<>(chromosomeList);
        Collections.shuffle(shuffledPopulation);
        List<Chromosome> candidates = new ArrayList<>();
        for (int i = 0; i < tournamentSize; i++) {
            candidates.add(shuffledPopulation.get(i));
        }
        Collections.sort(candidates);
        return chromosomeList.indexOf(candidates.get(candidates.size() - 1));
    }

    protected int tournamentSelectMin(int tournamentSize) {
        List<Integer> shuffledIndexes = new ArrayList<>();
        for (int i = 0; i < chromosomeList.size(); i++) {
            shuffledIndexes.add(i);
        }
        Collections.shuffle(shuffledIndexes);

        Map<Chromosome, Integer> candidates = new HashMap<>();
        for (int i = 0; i < tournamentSize; i++) {
            candidates.put(chromosomeList.get(shuffledIndexes.get(i)), shuffledIndexes.get(i));
        }
        List<Chromosome> sortedCandidates = new ArrayList<>();
        candidates.forEach((V, K) -> {
            sortedCandidates.add(V);
        });
        Collections.sort(sortedCandidates);
        return candidates.get(sortedCandidates.get(0));
    }

    /**
     * Generates a new random Chromosome. Is used to fill the chromosomeList in the abstract constructor.
     *
     * @return new randomly generated Chromosome
     */
    protected abstract Chromosome generateRandomChromosome();


    /**
     * Abstract method evolve(), to be implemented by subclass.
     * The default methods evolveToMax and evolveToMin offer a good default evolve method.
     */
    public abstract void evolve();

    /**
     * Evolves the population one generation.
     * First copy over the best n Chromosomes, based on elitism ratio.  Best equals highest fitness value.
     * Then crossover and mutate the rest based on those ratios.
     * Lastly, we change the chromosomeList to the nextGeneration
     * Selection of Parents for crossover and mutation methods or defined in mutate() and selectParents()
     */
    protected void evolveToMax() {
        List<Chromosome> nextGeneration = new ArrayList<>();
        int i = (int) (getElitismRate() * getPopulationSize());
        getChromosomeList().subList(i, getChromosomeList().size()).forEach((c) -> nextGeneration.add(c));

        while (i < getPopulationSize()) {
            if (ThreadLocalRandom.current().nextFloat() <= getCrossoverRate()) { //crossover?
                List<Chromosome> parents = selectParents();
                List<Chromosome> children = parents.get(0).mate(parents.get(1));
                for (Chromosome c : children) { //add children if there is enough space in new population array
                    if (i < getPopulationSize()) {
                        nextGeneration.add(c.mutate(getMutationRate()));
                        i++;
                    }
                }
            } else {
                nextGeneration.add(getChromosomeList().get(i).mutate(getMutationRate()));
                i++;
            }
        }
        setChromosomeList(nextGeneration);

    }
    /**
     * Evolves the population one generation.
     * First copy over the best n Chromosomes, based on elitism ratio. Best equals lowest fitness value.
     * Then crossover and mutate the rest based on those ratios.
     * Lastly, we change the chromosomeList to the nextGeneration
     * Selection of Parents for crossover and mutation methods or defined in mutate() and selectParents()
     */
    protected void evolveToMin() {
        List<Chromosome> nextGeneration = new ArrayList<>();
        int i = (int) (getElitismRate() * getPopulationSize());
        getChromosomeList().subList(0, i).forEach((c) -> nextGeneration.add(c));

        while (i < getPopulationSize()) {
            if (ThreadLocalRandom.current().nextFloat() <= getCrossoverRate()) { //crossover?
                List<Chromosome> parents = selectParents();
                List<Chromosome> children = parents.get(0).mate(parents.get(1));
                for (Chromosome c : children) { //add children if there is enough space in new population array
                    if (i < getPopulationSize()) {
                        nextGeneration.add(c.mutate(getMutationRate()));
                        i++;
                    }
                }
            } else {
                nextGeneration.add(getChromosomeList().get(i).mutate(getMutationRate()));
                i++;
            }
        }
        setChromosomeList(nextGeneration);
    }


    /**
     * Abstract Method for the Selection of the parents for crossover.
     * Selection method should be implemented by child class.
     *
     * @return newly Selected List of parents
     */
    protected abstract List<Chromosome> selectParents();

    /**
     * Builds a large String where each line is the String representation of a population member and their fitness.
     *
     * @return String of all Chromosomes
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (Chromosome c : chromosomeList) {
            sb.append(c);
            sb.append("\t");
            sb.append(c.getFitness());
            sb.append("\n");
        }
        return sb.toString();
    }

    public List<Double> getAllFitnessValue(){
        List<Double> fitnessValues = new ArrayList<>();
        for(Chromosome c: chromosomeList) {
            fitnessValues.add(c.getFitness());
        }
        return fitnessValues;
    }
}
