import java.util.*;

/**
 * Created by yay on 14.10.2016.
 */
public class SalesmanPopulation extends Population {
    private final static int CITIES = 1000;
    public static final Map<Integer,City> CITY_HASH_MAP;
    private final int tournamentSize;

    /**
     * List of cities to be traveled.
     */
    static {
        CITY_HASH_MAP = new HashMap<>();
        List<City> cityList = City.getRandomListOfCities(CITIES);
        for(int i =0; i < CITIES; i++){
            CITY_HASH_MAP.put(i, cityList.get(i));
        };
    }

    /**
     * Default Constructor for a SalesmanPopulation object.
     * Population size is 100, mutationRate 0.01f, crossoverRate 0.85f and elistimRate 0.03f.
     */
    public SalesmanPopulation() {
        this("SalesmanDefault", 8, 250, 0.03f, 0.85f, 0.03f);
    }

    /**
     * Constructor for SalesmanPopulation. The id and all rates can be customized, and tournamentSize for the tournament selection.
     */
    public SalesmanPopulation(String id, int tournamentSize, int populationSize, float mutationRate, float crossoverRate, float elitismRate) {
        super(id,populationSize,mutationRate,crossoverRate,elitismRate);
        this.tournamentSize = tournamentSize;
    }

    /**
     * We create a new SalesmanPath Object with a shuffled list of cities.
     * @return
     */
    @Override
    protected Chromosome generateRandomChromosome() {
        return new SalesmanPath(CITIES);
    }



    /**
     * Returns the City mapped to the index in CITY_HASH_MAP
     * @param index of the City in the HashMap
     * @return City Object
     */
    public static City getCity(int index){
        return CITY_HASH_MAP.get(index);
    }

    /**
     * We select two parents with the tournament selection method.
     * Tournament size is TOURNAMENT_SIZE.
     * @return Array with two selected parents.
     */
    @Override
    protected Chromosome[] selectParents() {
        return new Chromosome[]{
                getPopulationArray()[tournamentSelectMin(tournamentSize)],
                getPopulationArray()[tournamentSelectMin(tournamentSize)]};
    }


    /**
     * Evolves the population one generation.
     * First copy over the best n Chromosomes, based on elitism ratio.
     * Then crossover and mutate the rest based on those ratios.
     * Lastly, we change the populationArray to the nextGenerationArray
     * Selection of Parents for crossover and mutation methods or defined in mutate() and selectParents()
     */
    @Override
    protected void evolve() {
        Chromosome[] nextGeneration = new Chromosome[getPopulationSize()];
        int i = (int) (getElitismRate() * getPopulationSize());
        System.arraycopy(getPopulationArray(), 0, nextGeneration, 0, i); //copy top n chromosomes, based on elitism rate
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
}
