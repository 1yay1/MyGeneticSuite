
import java.util.List;
import java.util.logging.Logger;

/**
 * Chromosome class.
 * Wraps the gene and the fitness.
 * Created by yay on 12.10.2016.
 */
public abstract class Chromosome implements Comparable<Chromosome> {
    private List<Number> gene;
    private double fitness;

    /**
     * Default constructor taking a list of Number objects as gene.
     * Fitness is then calculated.
     * @param gene
     */
    public Chromosome(List<Number> gene) {
        this.gene = gene;
        this.fitness = calculateFitness();
    }

    public Chromosome(List<Number> gene, double fitness) {
        this.gene = gene;
        this.fitness = fitness;
    }

    /**
     * @return Reference to this gene.
     */
    public List<Number> getGene() {
        return gene;
    }


    /**
     * createChild method to create a new Chromosome of the same class as the subclass.
     * @param gene
     * @return
     */
    public abstract Chromosome createChild(List<Number> gene);

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

    public void setGene(List<Number> gene) {
        this.gene = gene;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

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
        List<Number> gene = chromosome.getGene();
        if(gene.size() != this.gene.size()) return false;
        for(int i =0 ; i < gene.size(); i ++) {
            if(!this.gene.get(i).equals(gene.get(i))) return false;
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
        for(int i = 0; i < gene.size(); i++){
            sb.append(gene.get(i));
            sb.append(", ");
        }
        sb.replace(sb.lastIndexOf(", "),sb.length(),"");
        sb.append("]");
        return sb.toString();
    }
}
