import java.util.List;

/**
 * Created by yay on 24.10.2016.aaaa
 */
@FunctionalInterface
public interface FunctionalEvolutionInterface {
    List<Chromosome> evolve(
            List<Chromosome> chromosomeList,
            FunctionalCrossoverInterface crossoverInterface,
            FunctionalSelectionInterface selectionInterface,
            FunctionalMutationInterface mutationInterface,
            float elitismRate,
            float crossoverRate,
            float mutationRate
    );
}
