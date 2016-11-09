import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * Created by yay on 12.10.2016.
 */
public abstract class Population<E extends Number> {
    private static Logger LOGGER = Logger.getLogger(Population.class.getName());

    private int populationSize;
    private double elitismRate;
    private double crossoverRate;
    private double mutationRate;
    private List<Chromosome> chromosomeList;
    private final String id;


    private FunctionalSelectionInterface selectionInterface;
    private FunctionalCrossoverInterface crossoverInterface;
    private FunctionalMutationInterface mutationInterface;
    private FunctionalChromosomeGenerator chromosomeGenerator;
    private FunctionalEvolutionInterface evolutionInterface;

    public FunctionalEvolutionInterface getEvolutionInterface() {
        return evolutionInterface;
    }

    public FunctionalCrossoverInterface getCrossoverInterface() {
        return crossoverInterface;
    }

    public FunctionalMutationInterface getMutationInterface() {
        return mutationInterface;
    }

    public FunctionalSelectionInterface getSelectionInterface() {
        return selectionInterface;
    }

    public FunctionalChromosomeGenerator getChromosomeGenerator() {
        return chromosomeGenerator;
    }
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

    protected Population(
            String id,
            int populationSize,
            double mutationRate,
            double crossoverRate,
            double elitismRate,
            FunctionalChromosomeGenerator chromosomeGenerator,
            FunctionalSelectionInterface selectionInterface,
            FunctionalCrossoverInterface crossoverInterface,
            FunctionalMutationInterface mutationInterface,
            FunctionalEvolutionInterface evolutionInterface
    ) throws IllegalArgumentException {
        if (mutationRate < 0 || mutationRate >= 1 || crossoverRate < 0 || crossoverRate >= 1) {
            throw new IllegalArgumentException("mutationRate, CrossoverRate must both be <= 1 and < 0");
        }
        this.id = id;
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismRate = elitismRate;
        this.selectionInterface = selectionInterface;
        this.crossoverInterface = crossoverInterface;
        this.mutationInterface = mutationInterface;
        this.chromosomeGenerator = chromosomeGenerator;
        this.evolutionInterface = evolutionInterface;

        this.chromosomeList = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            chromosomeList.add(chromosomeGenerator.generateRandomChromosome());
        }
        Collections.sort(chromosomeList);
    }


    /**
     * Getter for the id. Id is the name of the population.
     *
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
    public double getCrossoverRate() {
        return crossoverRate;
    }

    /**
     * Simple getter for the elitismRate
     *
     * @return float value of the elitismRate
     */
    public double getElitismRate() {
        return elitismRate;
    }

    /**
     * Returns the mutationRate
     *
     * @return float value of the mutation rate
     */
    public double getMutationRate() {
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

    public static Chromosome getMaxFittest(List<Chromosome> chromosomeList) {
        Collections.sort(chromosomeList);
        return chromosomeList.get(chromosomeList.size() - 1);
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

    public static Chromosome getMinFittest(List<Chromosome> chromosomeList) {
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
     * @return FunctionalSelectionInterface to be used as a parameter for the selectParents() method
     */
    public static FunctionalSelectionInterface rouletteSelect() {
        return chromosomeList1 -> {
            int[] fitnessArray = new int[chromosomeList1.size()];
            for (int i = 0; i < chromosomeList1.size(); i++) {
                fitnessArray[i] = (int) chromosomeList1.get(i).getFitness();
            }
            int fitnessSum = IntStream.of(fitnessArray).sum();

            double select = ThreadLocalRandom.current().nextDouble() * fitnessSum;
            for (int i = 0; i < chromosomeList1.size(); i++) {
                select -= fitnessArray[i];
                if (select <= 0) return i;
            }
            return chromosomeList1.size() - 1;
        };
    }

    public static FunctionalSelectionInterface tournamentSelectMin(int tournamentSize) {
        return chromosomeList1 -> {
            List<Integer> shuffledIndexes = new ArrayList<>();
            for (int i = 0; i < chromosomeList1.size(); i++) {
                shuffledIndexes.add(i);
            }
            Collections.shuffle(shuffledIndexes);

            Map<Chromosome, Integer> candidates = new HashMap<>();
            for (int i = 0; i < tournamentSize; i++) {
                candidates.put(chromosomeList1.get(shuffledIndexes.get(i)), shuffledIndexes.get(i));
            }
            List<Chromosome> sortedCandidates = new ArrayList<>();
            candidates.forEach((V, K) -> {
                sortedCandidates.add(V);
            });
            Collections.sort(sortedCandidates);
            return candidates.get(sortedCandidates.get(0));
        };
    }

    /**
     * Selects a Chromosome via tournament of size tournamentSize.
     * Select n candidates, and get the index of the fittest.
     *
     * @param tournamentSize amount of candidates to choose from
     * @return index in the populationArray of the chosen Chromosome
     */
    public static FunctionalSelectionInterface tournamentSelectMax(int tournamentSize) {
        return (chromosomeList) -> {
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
            Collections.reverse(sortedCandidates);
            return candidates.get(sortedCandidates.get(0));
        };
    }


    public static FunctionalCrossoverInterface onePointCrossover() {
        return parentChromosomeList -> {
            List<Chromosome> children = new ArrayList<>();

            final List<Number> chromosomeOneGene = parentChromosomeList.get(0).getGene();
            final List<Number> chromosomeTwoGene = parentChromosomeList.get(1).getGene();
            final int length = chromosomeOneGene.size();

            final int pivotPoint = ThreadLocalRandom.current().nextInt(length);
            final List<Number> newChromosomeOneGene = new ArrayList<Number>();
            final List<Number> newChromosomeTwoGene = new ArrayList<Number>();

            for (int i = 0; i < pivotPoint; i++) {
                newChromosomeOneGene.add(chromosomeOneGene.get(i));
                newChromosomeTwoGene.add(chromosomeTwoGene.get(i));
            }
            for (int i = pivotPoint; i < length; i++) {
                newChromosomeOneGene.add(chromosomeTwoGene.get(i));
                newChromosomeTwoGene.add(chromosomeOneGene.get(i));
            }

            children.add(parentChromosomeList.get(0).createChild(newChromosomeOneGene));
            children.add(parentChromosomeList.get(1).createChild(newChromosomeTwoGene));

            return children;
        };
    }

    /*protected int tournamentSelectMin(int tournamentSize) {
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
    }*/


    /**
     * Evolution method defined by the evolutionInterface field.
     * The default methods evolveToMax and evolveToMin offer a good default evolve method.
     */
    public void evolve() {
        setChromosomeList(
                evolutionInterface.evolve(
                        getChromosomeList(),
                        crossoverInterface,
                        selectionInterface,
                        mutationInterface,
                        elitismRate,
                        crossoverRate,
                        mutationRate
                )
        );
    }

    ;

    /**
     * Default evolve FunctionalInterface that can be called in the child class in evolve.
     * Uses default selection and mutation methods.
     * Evolves the population one generation.
     * First copy over the best n Chromosomes, based on elitism ratio.  Best equals highest fitness value.
     * Then crossover and mutate the rest based on those ratios.
     * Lastly, we change the chromosomeList to the nextGeneration
     * Selection of Parents for crossover and mutation methods or defined in mutate() and selectParents()
     */
    protected static FunctionalEvolutionInterface evolveToMax() {
        return (chromosomeList, crossoverInterface, selectionInterface, mutationInterface, elitismRate, crossoverRate, mutationRate) -> {
            List<Chromosome> nextGeneration = new ArrayList<>();
            int i = 0;
            if (elitismRate > 0) {
                i = (int) (elitismRate * chromosomeList.size());
                chromosomeList.subList(chromosomeList.size() - i, chromosomeList.size()).forEach((c) -> nextGeneration.add(c));
            }
            while (i < chromosomeList.size()) {
                if (ThreadLocalRandom.current().nextFloat() <= crossoverRate) { //crossover?
                    List<Chromosome> parents = new ArrayList<>();
                    parents.add(chromosomeList.get(selectionInterface.select(chromosomeList)));
                    parents.add(chromosomeList.get(selectionInterface.select(chromosomeList)));

                    List<Chromosome> children = crossoverInterface.crossover(parents);
                    for (Chromosome c : children) { //add children if there is enough space in new population array
                        if (i < chromosomeList.size()) {
                            nextGeneration.add(mutationInterface.mutate(c, mutationRate));
                            i++;
                        }
                    }
                } else {
                    nextGeneration.add(mutationInterface.mutate(chromosomeList.get(i), mutationRate));
                    i++;
                }
            }
            return(nextGeneration);
        };
    }

    /**
     * Evolves the population one generation.
     * First copy over the best n Chromosomes, based on elitism ratio. Best equals lowest fitness value.
     * Then crossover and mutate the rest based on those ratios.
     * Lastly, we change the chromosomeList to the nextGeneration
     * Selection of Parents for crossover and mutation methods or defined in mutate() and selectParents()
     */
    public static FunctionalEvolutionInterface evolveToMin() {
        return (chromosomeList, crossoverInterface, selectionInterface, mutationInterface, elitismRate, crossoverRate, mutationRate) -> {
            List<Chromosome> nextGeneration = new ArrayList<>();
            int i = (int) (elitismRate * chromosomeList.size());
            chromosomeList.subList(0, i).forEach((c) -> nextGeneration.add(c));

            while (i < chromosomeList.size()) {
                if (ThreadLocalRandom.current().nextFloat() <= crossoverRate) { //crossover?

                    List<Chromosome> parents = new ArrayList<>();
                    parents.add(chromosomeList.get(selectionInterface.select(chromosomeList)));
                    parents.add(chromosomeList.get(selectionInterface.select(chromosomeList)));

                    List<Chromosome> children = crossoverInterface.crossover(parents);
                    for (Chromosome c : children) { //add children if there is enough space in new population array
                        if (i < chromosomeList.size()) {
                            nextGeneration.add(mutationInterface.mutate(c, mutationRate));
                            i++;
                        }
                    }
                } else {
                    nextGeneration.add(mutationInterface.mutate(chromosomeList.get(i), mutationRate));
                    i++;
                }
            }
            return nextGeneration;
        };
    }

    public static FunctionalEvolutionInterface evolveToMaxAndReplicate(float replicationPercentage) {
        return null;
    }


    /**
     * Default Method for the Selection of the parents for crossover.
     * Default is to select two parents
     * Selection method should be implemented by child class.
     * Multiple selection methods will then be selected by the type parameter.
     *
     * @return newly Selected List of parents
     */
    protected List<Chromosome> selectParents() {
        List<Chromosome> parents = new ArrayList<>();
        parents.add(getChromosomeList().get(selectionInterface.select(getChromosomeList())));
        parents.add(getChromosomeList().get(selectionInterface.select(getChromosomeList())));

        return parents;
    }


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

    public List<Double> getAllFitnessValue() {
        List<Double> fitnessValues = new ArrayList<>();
        for (Chromosome c : chromosomeList) {
            fitnessValues.add(c.getFitness());
        }
        return fitnessValues;
    }

}
