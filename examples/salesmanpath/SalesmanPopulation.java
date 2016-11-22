import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Created by yay on 14.10.2016.
 */
public class SalesmanPopulation extends Population {
    private static int id;
    protected final static int CITIES = 15;
    private final static Map<Integer, City> CITY_HASH_MAP;

    public static final float DEFAULT_MUTATION_RATE = 0.06f;
    public static final float DEFAULT_CROSSOVER_RATE = 0.65f;
    public static final float DEFAULT_ELITISM_RATE = 0.01f;
    public static final int DEFAULT_POPULATION_SIZE = 250;
    public static final int DEFAULT_TOURNAMENT_SIZE = 8;

    public static final String DEFAULT_ID = "SalesmanPopulation";


    /**
     * List of cities to be traveled.
     */
    static {
        id = 0;
        CITY_HASH_MAP = new HashMap<>();
        List<City> cityList = City.getRandomListOfCities(CITIES);
        for (int i = 0; i < CITIES; i++) {
            CITY_HASH_MAP.put(i, cityList.get(i));
        }
    }

    /**
     * Default Constructor for a SalesmanPopulation object.
     * Population size is 100, mutationRate 0.01f, crossoverRate 0.85f and elistimRate 0.03f.
     */
    public SalesmanPopulation() {
        this(DEFAULT_ID + " " + id++, DEFAULT_TOURNAMENT_SIZE, DEFAULT_POPULATION_SIZE, DEFAULT_MUTATION_RATE, DEFAULT_CROSSOVER_RATE, DEFAULT_ELITISM_RATE);
    }

    /**
     * Constructor for SalesmanPopulation. The id and all rates can be customized, and tournamentSize for the tournament selection.
     */
    public SalesmanPopulation(String id, int tournamentSize, int populationSize, double mutationRate, double crossoverRate, double elitismRate) {
        super(id, populationSize, mutationRate, crossoverRate, elitismRate, generateRandomChromosome(), Population.tournamentSelectMin(tournamentSize), defaultCrossoverInterface(), defaultMutationInterface(), evolveToMax());
        //this.tournamentSize = tournamentSize;
    }

    public SalesmanPopulation(
            String id,
            //int tournamentSize,
            int populationSize,
            double mutationRate,
            double crossoverRate,
            double elitismRate,
            FunctionalChromosomeGenerator chromosomeGenerator,
            FunctionalSelectionInterface selectionInterface,
            FunctionalCrossoverInterface crossoverInterface,
            FunctionalMutationInterface mutationInterface,
            FunctionalEvolutionInterface evolutionInterface
    ) {
        super(id, populationSize, mutationRate, crossoverRate, elitismRate, chromosomeGenerator, selectionInterface, crossoverInterface, mutationInterface, evolutionInterface);
        //this.tournamentSize = tournamentSize;
    }

    /**
     * We create a new SalesmanPath Object with a shuffled list of cities.
     *
     * @return
     */
    protected static FunctionalChromosomeGenerator generateRandomChromosome() {
        return () -> {
            return new SalesmanPath(CITIES);
        };
    }


    private static FunctionalMutationInterface defaultMutationInterface() {
        return (c, mutationRate) -> {
            List<Number> path = c.getGene();
            for (int i = 1; i < path.size() - 1; i++) {
                if (ThreadLocalRandom.current().nextFloat() < mutationRate) {
                    int index1 = i;
                    int index2 = i + 1;
                    Number temp = path.get(index1);
                    path.set(index1, path.get(index2));
                    path.set(index2, temp);

                }
            }
            return c.createChild(path);
        };
    }

    private static FunctionalCrossoverInterface defaultCrossoverInterface() {
        return parentChromosomeList -> {
            List<Chromosome> children = new ArrayList<>();

            List<Number> firstPath = parentChromosomeList.get(0).getGene();
            List<Number> secondPath = parentChromosomeList.get(1).getGene();

            int index1 = 1 + ThreadLocalRandom.current().nextInt(firstPath.size() - 2);
            int index2 = index1 + ThreadLocalRandom.current().nextInt(firstPath.size() - index1);

            List<Number> newFirstPath = new ArrayList<>();
            List<Number> newSecondPath = new ArrayList<>();

            newFirstPath.add(0);
            newSecondPath.add(0);
            for (int i = 1; i < firstPath.size(); i++) {
                newFirstPath.add(null);
                newSecondPath.add(null);
            }

            List<Number> addedToOne = new ArrayList<>();
            List<Number> addedToTwo = new ArrayList<>();
            for (int i = index1; i < index2; i++) {
                newFirstPath.set(i, secondPath.get(i));
                addedToOne.add(secondPath.get(i));
                newSecondPath.set(i, firstPath.get(i));
                addedToTwo.add(firstPath.get(i));
            }

            for (int i = 1; i < firstPath.size(); i++) {
                Number nextPotential = firstPath.get(i);
                boolean canAdd = true;
                for (Number b : addedToOne) {
                    if (b.equals(nextPotential)) {
                        canAdd = false;
                    }
                }
                if (canAdd) {
                    for (int j = 1; j < firstPath.size(); j++) {
                        if (newFirstPath.get(j) == null) {
                            newFirstPath.set(j, nextPotential);
                            break;
                        }
                    }
                }
            }

            for (int i = 1; i < firstPath.size(); i++) {
                Number nextPotential = secondPath.get(i);
                boolean canAdd = true;
                for (Number b : addedToTwo) {
                    if (b == nextPotential) {
                        canAdd = false;
                    }
                }
                if (canAdd) {
                    for (int j = 1; j < firstPath.size(); j++) {
                        if (newSecondPath.get(j) == null) {
                            newSecondPath.set(j, nextPotential);
                            break;
                        }
                    }
                }
            }

            children.add(parentChromosomeList.get(0).createChild(newFirstPath));
            children.add(parentChromosomeList.get(1).createChild(newFirstPath));
            return children;
        };
    }

    public static FunctionalCrossoverInterface greedyCrossover() {
        return parentChromosomeList -> {
            final List<Number> p1 = parentChromosomeList.get(0).getGene();
            final List<Number> p2 = parentChromosomeList.get(1).getGene();

            final List<Number> childGene = new ArrayList<>();
            childGene.add(p1.get(0));
            double fitness = 0;
            while (childGene.size() < p1.size()) {
                Integer c0 = (Integer) childGene.get(childGene.size() - 1);
                Integer c1 = (Integer) getNextCity(p1, c0);
                Integer c2 = (Integer) getNextCity(p2, c0);
                double d1 = City.distanceFromTo(SalesmanPopulation.getCity(c0), SalesmanPopulation.getCity(c1));
                double d2 = City.distanceFromTo(SalesmanPopulation.getCity(c0), SalesmanPopulation.getCity(c2));
                if (d1 <= d2) {
                    childGene.add(c1);
                    fitness += d1;
                } else {
                    childGene.add(c2);
                    fitness += d2;
                }
            }
            //round trip:
            fitness += City.distanceFromTo(SalesmanPopulation.getCity((Integer) childGene.get(0)), SalesmanPopulation.getCity((Integer) childGene.get(childGene.size() - 1)));

            List<Chromosome> temp = new ArrayList<>();
            new ArrayList<>().add(new SalesmanPath(childGene, fitness));
            return temp;
        };
    }

    private static Number getNextCity(List<Number> path, Number city) {
        Number next = null;
        for (int i = 0; i < path.size(); i++) {
            if (path.get(i).equals(city)) {
                next = i == path.size() - 1 ? path.get(0) : path.get(i + 1);
                break;
            }
        }
        return next;
    }


    /**
     * Returns the City mapped to the index in CITY_HASH_MAP
     *
     * @param index of the City in the HashMap
     * @return City Object
     */
    public static City getCity(int index) {
        return CITY_HASH_MAP.get(index);
    }


    /**
     * Evolves the population one generation.
     * First copy over the best n Chromosomes, based on elitism ratio.
     * Then crossover and mutate the rest based on those ratios.
     * Lastly, we change the populationArray to the nextGenerationArray
     * Selection of Parents for crossover and mutation methods or defined in mutate() and selectParents()
     */
    @Override
    public void evolve() {
        evolveToMin();
    }


}
