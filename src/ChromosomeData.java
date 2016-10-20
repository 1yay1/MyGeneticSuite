import java.util.*;
import java.util.function.Function;
import java.util.logging.MemoryHandler;

/**
 * Created by yay on 17.10.2016.
 */
public class ChromosomeData {

    interface CallMethod {
        void runMethod(ChromosomeData d);
    }

    private final List<Chromosome> chromosomeList;
    private final String id;
    private final static Map<METHOD, CallMethod> methodMap;

    public enum METHOD{
        AVG,
        MIN,
        MAX
    }

    static{
        methodMap =new EnumMap<>(METHOD.class);

    }

    ChromosomeData(String id, List<Chromosome> chromosomeList) {
        this.id = id;
        this.chromosomeList = chromosomeList;
    }

    public List<Chromosome> getChromosomeList() {
        return chromosomeList;
    }

    public String getId() {
        return id;
    }

    public Chromosome getMaxFittest() {
        return Collections.max(chromosomeList);
    }

    public double getMaxFitnessValue() {
        return Collections.max(chromosomeList).getFitness();
    }

    public Chromosome getMinFittest() {
        return Collections.min(chromosomeList);
    }

    public double getMinFitnessValue() {
        return Collections.min(chromosomeList).getFitness();
    }

    public double getAverageFitnessValue() {
        return chromosomeList
                .stream()
                .mapToDouble(Chromosome::getFitness)
                .average()
                .getAsDouble();
    }
}
