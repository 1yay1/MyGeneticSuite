import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Chromosome consisting of five bits.
 * The fittest possible chromosome is "11111" and the unfittest "00000"
 * Mutating flips a random bit, mateing is crossover at a random pivot point.
 * Created by yay on 12.10.2016.
 */
public class BitChromosome extends Chromosome {


    /**
     * Constructor for BitChromosome, uses a randomly created Gene of length geneSize as gene.
     */
    public BitChromosome(int geneSize) {
        this(GeneticUtilities.getShuffledListOfInts(2,geneSize));
    }

    /**
     * Private constructor, only used in mutate
     *
     * @param genes Gene representing the new gene.
     */
    private BitChromosome(int[] genes) {
        super(genes);
    }

    /**
     * Fitness is the amount of 1s set
     *
     * @return new fitness value, is called in constructor.
     */
    @Override
    protected double calculateFitness() {
        return IntStream.of(getGene()).sum();
    }


    /**
     * Mutate a randomly chosen bit in the gene.
     *
     * @return newly mutated chromosome
     */
    @Override
    protected Chromosome mutate(float mutationRate) {
        final int newGene[] = getGene();
        for(int i = 0; i < getGene().length; i++) {
            if(ThreadLocalRandom.current().nextFloat() < mutationRate) {
                newGene[i] = newGene[i] == 1 ? 0 : 1;
            }
        }
        return new BitChromosome(newGene);
    }

    /**
     * Mates the Chromosomes.
     * The gene of each Chromosome gets cut at a randomly generated pivot point,
     * and spliced together into two new chromosomes.
     *
     * @param chromosome Chromosome to be mated with.
     * @return list of two new Chromosomes.
     */
    @Override
    protected List<Chromosome> mate(Chromosome chromosome) {

        final int length = getGene().length;
        List<Chromosome> newChromosomes = new ArrayList<>();

        final int pivotPoint = ThreadLocalRandom.current().nextInt(length);
        final int chromosomeOneGene[] = this.getGene();
        final int chromosomeTwoGene[] = chromosome.getGene();
        final int newChromosomeOneGene[] = new int[length];
        final int newChromosomeTwoGene[] = new int[length];

        for (int i = 0; i < pivotPoint; i++) {
            newChromosomeOneGene[i] = chromosomeOneGene[i];
            newChromosomeTwoGene[i] = chromosomeTwoGene[i];
        }
        for (int i = pivotPoint; i < getGene().length; i++) {
            newChromosomeOneGene[i] = chromosomeTwoGene[i];
            newChromosomeTwoGene[i] = chromosomeOneGene[i];
        }

        newChromosomes.add(new BitChromosome(chromosomeOneGene));
        newChromosomes.add(new BitChromosome(chromosomeTwoGene));

        return newChromosomes;
    }
}
