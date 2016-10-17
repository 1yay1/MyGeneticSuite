/**
 * Subclass of Population.
 * Population of Chromosomes which are consisting of five bits.
 * Goal is to create a chromosome with the gene "11111".
 *
 * Created by yay on 12.10.2016.
 */
public class BitPopulation extends Population {


    public BitPopulation(String id, int populationSize, float mutationRate, float crossoverRate, float elitismRate) {
        super(id, populationSize, mutationRate, crossoverRate, elitismRate);
    }

    /**
     * Constructor with no Param. {@link BitChromosome} constructor already returns a random {@link BitChromosome}.
     *
     * @return new {@link BitChromosome},
     */
    @Override
    protected Chromosome generateRandomChromosome() {
        return new BitChromosome();
    }

    /**
     * We select two parents with rouletteSelect() method.
     * @return Chromosome array with two parent Chromosomes.
     */
    @Override
    protected Chromosome[] selectParents() {
        return new Chromosome[]{
                getPopulationArray()[rouletteSelect()],
                getPopulationArray()[rouletteSelect()]
        };
    }
}
