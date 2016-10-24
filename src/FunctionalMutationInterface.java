/**
 * Functional Interface for the mutation method used by the population class
 * Created by yay on 21.10.2016.
 */
@FunctionalInterface
public interface FunctionalMutationInterface {
     Chromosome mutate(Chromosome c, float mutationRate);
}
