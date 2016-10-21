import java.util.List;
import java.util.logging.Logger;

/**
 * Created by yay on 12.10.2016.
 */
public abstract class Chromosome implements Comparable<Chromosome> {
    private int[] gene;
    private double fitness;
    public static int DEFAULT_MUTATION = 0;
    private int mutationType;

    private static Logger LOGGER = Logger.getLogger(Chromosome.class.getName());

    public Chromosome(int[] gene) {
        this.gene = gene;
        this.fitness = calculateFitness();
        this.mutationType = DEFAULT_MUTATION;
    }

    public Chromosome(int[] gene, int mutationType) {
        this.gene = gene;
        this.fitness = calculateFitness();
        this.mutationType = mutationType;
    }

    public int getMutationType() {
        return mutationType;
    }

    /**
     * Gets the private static Logger Object for the class.
     * @return LOGGER object.
     */
    public static Logger getLOGGER() {
        return LOGGER;
    }

    /**
     * @return copy of gene of this chromosome.
     */
    public int[] getGene() {
        return gene;
    }


    /**
     * @return fitness value as int
     */
    public double getFitness() {
        return fitness;
    }

    /**
     * Calculates the fitness value based on the gene, and sets it.
     * To be implemented by child class. Method is called in constructor.
     *
     * @return new fitness value.
     */
    protected abstract double calculateFitness();

    /**
     * Default mutation method, calls the mutation method implemented by subclass with type variable DEFAULT_MUTATION = 0.
     * @param mutationRate rate of mutation
     * @return newly mutated Chromosomes
     */
    protected Chromosome mutate(float mutationRate){
        return mutate(mutationType, mutationRate);
    }

    /**
     * Mutation method to be implemented by subclass.
     * @param mutationRate rate of mutation
     * @return newly mutated Chromosomes
     */
    protected abstract Chromosome mutate(int type, float mutationRate);

    /**
     * mate method to be implemented by child class
     * Creates offsprings.
     * It is possible to only implement it so that it returns an array with only one offspring.
     * Or to pass null as the param and create offspring(s) from itself.
     *
     * @param partner the {@link Chromosome} mating partner.
     * @return the newly created chromosome offsprings.
     */
    protected abstract List<Chromosome> mate(Chromosome partner);

    /**
     * compareTo method of Interface Comparable.
     * compares based on fitness value.
     *
     * @param chromosome to be compared to
     * @return 0 for same fitness value, -1 if the chromosome to be compared to has bigger fitness.
     * and 1 if the fitness value of this chromosome is bigger.
     */
    @Override
    public int compareTo(Chromosome chromosome) {
        return fitness < chromosome.getFitness() ? -1 : fitness > chromosome.getFitness() ? 1 : 0;
    }

    /**
     * Checks if two chromosome are equal.
     *
     * @param object to be compared to.
     * @return true if objects are EXACT same class, have exact same fitness and exact same gene value.
     */
    @Override
    public boolean equals(Object object) {
        if (this.getClass().equals(object.getClass())) {
            return false;
        }
        Chromosome chromosome = (Chromosome) object;
        int[] gene = chromosome.getGene();
        if(gene.length != this.gene.length) return false;
        for(int i =0 ; i < gene.length; i ++) {
            if(this.gene[i] != gene[i]) return false;
        }
        return this.getFitness() == chromosome.getFitness();
    }

    /**
     * Default toString method of the Chromosome class.
     * The default behavior is to build a string representation of the bits. i.e. "1010101101".
     * @return String of bits.
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0; i < gene.length; i++){
            sb.append(i);
            sb.append(", ");
        }
        sb.replace(sb.lastIndexOf(", "),sb.length(),"");
        sb.append("]");
        return sb.toString();
    }
}
