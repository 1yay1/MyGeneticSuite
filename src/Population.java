import java.util.*;
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
    private Chromosome[] populationArray;
    private final String id;

    /**
     * Abstract constructor for a new Instance of Population.
     * Throws {@link IllegalArgumentException} if the mutationRate and crossoverRate are not between 0 and 1.
     * Fills the populationArray with random Chromosomes, using the abstract method generateRandomChromosome.
     * Then the array is sorted by fitness value.
     *
     * @param populationSize size of populationArray
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
        this.populationArray = new Chromosome[populationSize];

        for (int i = 0; i < populationSize; i++) {
            this.populationArray[i] = generateRandomChromosome();
        }
        Arrays.sort(this.populationArray);
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
     * Returns a copy of the population array
     *
     * @return chromosome array of the population as a copy of the original.
     */
    public Chromosome[] getPopulationArray() {
        Chromosome[] populationArrayCopy = new Chromosome[populationSize];
        System.arraycopy(populationArray, 0, populationArrayCopy, 0, populationSize);
        return populationArrayCopy;
    }

    /**
     * Gets the highest fitness value Chromosome. The Population Array has to be sorted,
     * but it always should be sorted in the first place. Then we can grab the last index Chromosome.
     *
     * @return highest fitness value Chromosome
     */
    public Chromosome getMaxFittest() {
        return getMaxFittest(populationArray);
    }

    public static Chromosome getMaxFittest(Chromosome[] populationArray){
        Arrays.sort(populationArray);
        return populationArray[populationArray.length - 1];
    }

    /**
     * Gets the lowest fitness value Chromosome. The Population Array has to be sorted,
     * but it always should be sorted in the first place. Then we can grab the last index Chromosome.
     *
     * @return lowest fitness Chromosome
     */
    public Chromosome getMinFittest() {
        return getMinFittest(populationArray);
    }

    public static Chromosome getMinFittest(Chromosome[] populationArray){
        Arrays.sort(populationArray);
        return populationArray[0];
    }


    /**
     * Calculates the average fitness value of the complete population
     *
     * @return
     */
    public double getAverageFitness() {
        return getAverageFitness(populationArray);
    }

    public static double getAverageFitness(Chromosome[] populationArray) {
        double sum = 0;
        double amount = 0;
        for (Chromosome c : populationArray) {
            sum += c.getFitness();
            amount++;
        }
        return sum / amount;
    }

    /**
     * Sets a new populationArray if the new length is the same as the old populationSize.
     * Sorts the new array.
     *
     * @param populationArray
     * @throws IllegalArgumentException
     */
    public void setPopulationArray(Chromosome[] populationArray) throws IllegalArgumentException {
        if (populationArray.length != populationSize) {
            throw new IllegalArgumentException("Wrong size for populationArray");
        }
        this.populationArray = populationArray;
        Arrays.sort(populationArray);
    }

    /**
     * Sums all fitness values, gets a random double multiplicated with the sum.
     * Uses that value as selection value, loops through all fitnesses and substracts the fitness of each index.
     * Once the selection value is smaller than 0, we return the current index.
     * If value never gets below 0, we return last index.
     *
     * @return int of the index of the Chromosome in the population Array
     */
    protected int rouletteSelect() {
        int[] fitnessArray = new int[populationSize];
        for (int i = 0; i < populationSize; i++) {
            fitnessArray[i] = (int) populationArray[i].getFitness();
        }
        int fitnessSum = IntStream.of(fitnessArray).sum();

        double select = GeneticUtilities.random.nextDouble() * fitnessSum;
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
        List<Chromosome> populationArrayAsList = Arrays.asList(populationArray);
        List<Chromosome> shuffledPopulation = new ArrayList<>(populationArrayAsList);
        Collections.shuffle(shuffledPopulation);
        List<Chromosome> candidates = new ArrayList<>();
        for (int i = 0; i < tournamentSize; i++) {
            candidates.add(shuffledPopulation.get(i));
        }
        Collections.sort(candidates);
        return populationArrayAsList.indexOf(candidates.get(candidates.size() - 1));
    }

    protected int tournamentSelectMin(int tournamentSize) {
        List<Chromosome> populationArrayAsList = Arrays.asList(populationArray);
        List<Integer> shuffledIndexes = new ArrayList<>();
        for (int i = 0; i < populationArray.length; i++) {
            shuffledIndexes.add(i);
        }
        Collections.shuffle(shuffledIndexes);

        Map<Chromosome, Integer> candidates = new HashMap<>();
        for (int i = 0; i < tournamentSize; i++) {
            candidates.put(populationArray[shuffledIndexes.get(i)], shuffledIndexes.get(i));
        }
        List<Chromosome> sortedCandidates = new ArrayList<>();
        candidates.forEach((V, K) -> {
            sortedCandidates.add(V);
        });
        Collections.sort(sortedCandidates);
        return candidates.get(sortedCandidates.get(0));
    }

    /**
     * Generates a new random Chromosome. Is used to fill the populationArray in the abstract constructor.
     *
     * @return new randomly generated Chromosome
     */
    protected abstract Chromosome generateRandomChromosome();

    /**
     * Evolves the population one generation.
     * First copy over the best n Chromosomes, based on elitism ratio.
     * Then crossover and mutate the rest based on those ratios.
     * Lastly, we change the populationArray to the nextGenerationArray
     * Selection of Parents for crossover and mutation methods or defined in mutate() and selectParents()
     */
    protected void evolve() {
        Chromosome[] nextGeneration = new Chromosome[getPopulationSize()];
        int i = (int) (getElitismRate() * getPopulationSize());
        System.arraycopy(getPopulationArray(), getPopulationSize() - i - 1, nextGeneration, 0, i); //copy top n chromosomes, based on elitism rate
        while (i < getPopulationSize()) {
            if (GeneticUtilities.random.nextFloat() <= getCrossoverRate()) { //crossover?
                Chromosome parents[] = selectParents();
                Chromosome children[] = parents[0].mate(parents[1]);
                for (Chromosome c : children) { //add children if there is enough space in new population array
                    if (i < getPopulationSize()) {
                        nextGeneration[i++] = c.mutate(getMutationRate());
                    }
                }
            } else {
                nextGeneration[i] = getPopulationArray()[i].mutate(getMutationRate());
                i++;
            }
        }
        setPopulationArray(nextGeneration);
    }


    /**
     * Abstract Method for the Selection of the parents for crossover.
     * Selection method should be implemented by child class.
     *
     * @return newly Selected Array of parents
     */
    protected abstract Chromosome[] selectParents();

    /**
     * Builds a large String where each line is the String representation of a population member and their fitness.
     *
     * @return String of all Chromosomes
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (Chromosome c : populationArray) {
            sb.append(c);
            sb.append("\t");
            sb.append(c.getFitness());
            sb.append("\n");
        }
        return sb.toString();
    }

    public List<Double> getAllFitnessValue(){
        List<Double> fitnessValues = new ArrayList<>();
        for(Chromosome c: populationArray) {
            fitnessValues.add(c.getFitness());
        }
        return fitnessValues;
    }
}
