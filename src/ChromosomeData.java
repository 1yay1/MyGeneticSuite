import java.util.Collections;
import java.util.List;

/**
 * Created by yay on 17.10.2016.
 */
public class ChromosomeData {
    private final List<Chromosome> chromosomeList;
    private final String id;

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

    public double getAverageFitness() {
        return chromosomeList
                .stream()
                .mapToDouble(Chromosome::getFitness)
                .average()
                .getAsDouble();
    }
}
