import java.util.BitSet;

/**
 * Chromosome consisting of five bits.
 * The fittest possible chromosome is "11111" and the unfittest "00000"
 * Mutating flips a random bit, mateing is crossover at a random pivot point.
 * Created by yay on 12.10.2016.
 */
public class BitChromosome extends Chromosome {
    public static int GENE_SIZE = 150;


    /**
     * Constructor for BitChromosome, uses a randomly created Gene of length 5 as gene.
     */
    public BitChromosome() {
        super(GeneticUtilities.getRandomFixedLengthBitSet(GENE_SIZE));
    }

    /**
     * Private constructor, only used in mutate
     *
     * @param gene Gene representing the new gene.
     */
    private BitChromosome(Gene gene) {
        super(gene);
    }

    /**
     * Fitness is the cardinality of the BitSet
     *
     * @return new fitness value, is called in constructor.
     */
    @Override
    protected double calculateFitness() {
        return this.getGene().getBitSet().cardinality();
    }


    /**
     * Mutate a randomly chosen bit in the gene.
     *
     * @return newly mutated chromosome
     */
    @Override
    protected Chromosome mutate(float mutationRate) {
        Gene newGene = this.getGene();
        if(GeneticUtilities.random.nextFloat() < mutationRate) {
            int i = GeneticUtilities.random.nextInt(GENE_SIZE);
            if (newGene.get(i)) {
                newGene.set(i, false);
            } else {
                newGene.set(i, true);
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
     * @return array of two new Chromosomes.
     */
    @Override
    protected Chromosome[] mate(Chromosome chromosome) {

        int pivotPoint = GeneticUtilities.random.nextInt(GENE_SIZE);
        Gene chromosomeOneGene = this.getGene();
        Gene chromosomeTwoGene = chromosome.getGene();
        BitSet newChromosomeOneGene = new BitSet();
        BitSet newChromosomeTwoGene = new BitSet();
        for (int i = 0; i < pivotPoint; i++) {
            newChromosomeOneGene.set(i, chromosomeOneGene.get(i));
            newChromosomeTwoGene.set(i, chromosomeTwoGene.get(i));
        }
        for (int i = pivotPoint; i < GENE_SIZE; i++) {
            newChromosomeOneGene.set(i, chromosomeTwoGene.get(i));
            newChromosomeTwoGene.set(i, chromosomeOneGene.get(i));
        }
        return new Chromosome[]{
                new BitChromosome(new Gene(newChromosomeOneGene, GENE_SIZE)),
                new BitChromosome(new Gene(newChromosomeTwoGene, GENE_SIZE))
        };
    }
}
