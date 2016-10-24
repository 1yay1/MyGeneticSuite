import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    @Override
    public Chromosome createChild(List<Number> gene) {
        return new BitChromosome(gene);
    }

    /**
     * Constructor for BitChromosome, uses a randomly created Gene of length geneSize as gene.
     */
    public BitChromosome(int geneSize, int setBits) {
        this(createRandomGene(geneSize,setBits));
    }

    private static List<Number> createRandomGene(int geneSize, int setBits) {
        List<Number> gene = new ArrayList<>();
        for(int i = 0; i < geneSize - setBits; i++) {
            gene.add(new Integer(0));
        }
        for(int i = 0; i < setBits; i ++) {
            gene.add(new Integer(1));
        }
        Collections.shuffle(gene, ThreadLocalRandom.current());
        return gene;
    }

    /**
     * Private constructor, only used in mutate
     *
     * @param genes Gene representing the new gene.
     */
    public BitChromosome(List<Number> genes) {
        super(genes);
    }

    /**
     * Fitness is the amount of 1s set
     *
     * @return new fitness value, is called in constructor.
     */
    @Override
    protected double calculateFitness() {
        return getGene().stream().mapToInt((i) -> i.intValue()).sum();
    }

    @Override
    public String toString(){
        return Arrays.toString(getGene().toArray());
    }

}
