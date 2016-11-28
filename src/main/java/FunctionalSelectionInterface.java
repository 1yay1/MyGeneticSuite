import java.util.List;

/**
 * Functional Interface for the parent selection method used by the population class
 * Created by yay on 21.10.2016.
 */
@FunctionalInterface
public interface FunctionalSelectionInterface {
     int select(List<Chromosome> chromosomeList);

}
