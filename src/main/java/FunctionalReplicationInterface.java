import java.util.List;

/**
 * Created by yay on 23.11.2016.
 */
@FunctionalInterface
public interface FunctionalReplicationInterface {
     List<Chromosome> replicate(List<Chromosome> chromosomeList);
}
