import java.util.*;

/**
 * Created by yay on 14.10.2016.
 */
public class SalesmanPopulation extends Population {
    private static int id;
    protected final static int CITIES = 20;
    public final static Map<Integer, City> CITY_HASH_MAP;
    private final int tournamentSize;

    public static final float DEFAULT_MUTATION_RATE = 0.06f;
    public static final float DEFAULT_CROSSOVER_RATE = 0.65f;
    public static final float DEFAULT_ELITISM_RATE = 0.01f;
    public static final int DEFAULT_POPULATION_SIZE = 500;
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
        this(DEFAULT_ID+" "+id++, DEFAULT_TOURNAMENT_SIZE, DEFAULT_POPULATION_SIZE, DEFAULT_MUTATION_RATE, DEFAULT_CROSSOVER_RATE, DEFAULT_ELITISM_RATE);
    }

    /**
     * Constructor for SalesmanPopulation. The id and all rates can be customized, and tournamentSize for the tournament selection.
     */
    public SalesmanPopulation(String id, int tournamentSize, int populationSize, float mutationRate, float crossoverRate, float elitismRate) {
        super(id, populationSize, mutationRate, crossoverRate, elitismRate);
        this.tournamentSize = tournamentSize;
    }

    public SalesmanPopulation(String id, int tournamentSize, int populationSize, float mutationRate, float crossoverRate, float elitismRate, int selectionType, int mutationType) {
        super(id, populationSize, mutationRate, crossoverRate, elitismRate, selectionType, mutationType);
        this.tournamentSize = tournamentSize;
    }

    /**
     * We create a new SalesmanPath Object with a shuffled list of cities.
     *
     * @return
     */
    @Override
    protected Chromosome generateRandomChromosome() {
        return new SalesmanPath(CITIES, getMutationType());
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
     * We select two parents with the tournament selection method.
     * Tournament size is TOURNAMENT_SIZE.
     *
     * @return Array with two selected parents.
     */
    @Override
    protected List<Chromosome> selectParents(int type) {
        if (type == Population.ROULETTE_SELECT) {
            List<Chromosome> parents = new ArrayList<>();
            parents.add(getChromosomeList().get(tournamentSelectMin(tournamentSize)));
            parents.add(getChromosomeList().get(tournamentSelectMin(tournamentSize)));

            return parents;
        } else { //default selection is tournament selecion
            List<Chromosome> parents = new ArrayList<>();
            parents.add(getChromosomeList().get(tournamentSelectMin(tournamentSize)));
            parents.add(getChromosomeList().get(tournamentSelectMin(tournamentSize)));

            return parents;
        }
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
