import java.util.List;

/**
 * Functional Interface for the mutation method used by the population class
 * Created by yay on 21.10.2016.
 */
@FunctionalInterface
public interface FunctionalMutationInterface {
     List<Chromosome> mutate(List<Chromosome> chromosomeList, double mutationRate);
}
