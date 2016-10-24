import java.util.List;

/**
 * Functional Interface for the crossover method used by the population class
 * Created by yay on 21.10.2016.
 */
@FunctionalInterface
public interface FunctionalCrossoverInterface {
     List<Chromosome> crossover(List<Chromosome> parentChromosomeList);
}
