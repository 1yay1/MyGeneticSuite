import java.util.ArrayList;
import java.util.List;

/**
 * Subclass of Population.
 * Population of Chromosomes which are consisting of five bits.
 * Goal is to create a chromosome with the gene "11111".
 *
 * Created by yay on 12.10.2016.
 */
public class BitPopulation extends Population {
    private final int geneSize;

    public BitPopulation(String id, int populationSize, int geneSize, float mutationRate, float crossoverRate, float elitismRate) {
        super(id, populationSize, mutationRate, crossoverRate, elitismRate);
        this.geneSize = geneSize;
    }

    /**
     * Constructor with no Param. {@link BitChromosome} constructor already returns a random {@link BitChromosome}.
     *
     * @return new {@link BitChromosome},
     */
    @Override
    protected Chromosome generateRandomChromosome() {
        return new BitChromosome(geneSize);
    }

    /**
     * Default call to evolveToMax()
     */
    @Override
    public void evolve() {
        evolveToMax();
    }

    /**
     * We select two parents with rouletteSelect() method.
     * @return Chromosome array with two parent Chromosomes.
     */
    @Override
    protected List<Chromosome> selectParents(int selectionType) {
        List<Chromosome> parents = new ArrayList<>();
        parents.add(getChromosomeList().get(rouletteSelect()));
        parents.add(getChromosomeList().get(rouletteSelect()));

        return parents;
    }
}
