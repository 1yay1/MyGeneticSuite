import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Subclass of Population.
 * Population of Chromosomes which are consisting of five bits.
 * Goal is to create a chromosome with the gene "11111".
 * <p>
 * Created by yay on 12.10.2016.
 */
public class BitPopulation extends Population {

    public BitPopulation(String id, int populationSize, int setBits, int geneSize, float mutationRate, float crossoverRate, float elitismRate) {
        this(id, populationSize, mutationRate, crossoverRate, elitismRate, generateRandomChromosome(geneSize, setBits), Population.tournamentSelectMax(1), Population.onePointCrossover(), flipBitMuation(),evolveToMax() );
    }

    public BitPopulation(
            String id,
            int populationSize,
            float mutationRate,
            float crossoverRate,
            float elitismRate,
            FunctionalChromosomeGenerator chromosomeGenerator,
            FunctionalSelectionInterface selectionInterface,
            FunctionalCrossoverInterface crossoverInterface,
            FunctionalMutationInterface mutationInterface,
            FunctionalEvolutionInterface evolutionInterface
    ) {
        super(id, populationSize, mutationRate, crossoverRate, elitismRate, chromosomeGenerator, selectionInterface, crossoverInterface, mutationInterface, evolutionInterface);
    }

    /**
     * Constructor with no Param. {@link BitChromosome} constructor already returns a random {@link BitChromosome}.
     *
     * @return new {@link BitChromosome},
     */
    public static FunctionalChromosomeGenerator generateRandomChromosome(int geneSize, int setBits) {
        return () -> new BitChromosome(geneSize, setBits);
    }

    /**
     * Default call to evolveToMax()
     */
    @Override
    public void evolve() {
        evolveToMax();
    }

    /**
     * Mutation flips random bits with a chance equal to mutationRate parameter.
     *
     * @return
     */
    public static FunctionalMutationInterface flipBitMuation() {
        return (c, mutationRate) -> {
            final List<Number> newGene = c.getGene();
            for (int i = 0; i < c.getGene().size(); i++) {
                if (ThreadLocalRandom.current().nextFloat() < mutationRate) {
                    newGene.set(i, (newGene.get(i).intValue() == new Integer(1) ? new Integer(0) : new Integer(1)));
                }
            }
            return new BitChromosome(newGene);
        };
    }

    public static FunctionalCrossoverInterface twoPointCrossover() {
        return parentChromosomeList -> {
            List<Chromosome> children = new ArrayList<>();

            final List<Number> chromosomeOneGene = parentChromosomeList.get(0).getGene();
            final List<Number> chromosomeTwoGene = parentChromosomeList.get(1).getGene();
            final int length = chromosomeOneGene.size();

            final int index1 = ThreadLocalRandom.current().nextInt(length-1);
            final int index2 = index1 + ThreadLocalRandom.current().nextInt(length-index1);

            final List<Number> newChromosomeOneGene = new ArrayList<Number>();
            final List<Number> newChromosomeTwoGene = new ArrayList<Number>();

            for (int i = 0; i < index1; i++) {
                newChromosomeOneGene.add(chromosomeOneGene.get(i));
                newChromosomeTwoGene.add(chromosomeTwoGene.get(i));
            }
            for (int i = index1; i < index2; i++) {
                newChromosomeOneGene.add(chromosomeTwoGene.get(i));
                newChromosomeTwoGene.add(chromosomeOneGene.get(i));
            }
            for (int i = index2; i < length; i++) {
                newChromosomeOneGene.add(chromosomeOneGene.get(i));
                newChromosomeTwoGene.add(chromosomeTwoGene.get(i));
            }

            children.add(parentChromosomeList.get(0).createChild(newChromosomeOneGene));
            children.add(parentChromosomeList.get(1).createChild(newChromosomeTwoGene));

            return children;
        };
    }

}
