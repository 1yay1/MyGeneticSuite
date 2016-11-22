import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.omg.CORBA.SystemException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Subclass of Population.
 * Population of Chromosomes which are consisting of five bits.
 * Goal is to create a chromosome with the gene "11111".
 * <p>
 * Created by yay on 12.10.2016.
 */
public class BitPopulation extends Population {

    public BitPopulation(String id, int populationSize, int setBits, int geneSize, double mutationRate, double crossoverRate, double elitismRate) {
        this(id, populationSize, mutationRate, crossoverRate, elitismRate, generateRandomChromosome(geneSize, setBits), Population.tournamentSelectMax(3), Population.onePointCrossover(), flipBitMuation(), evolveToMaxAndReplicate());
    }

    public BitPopulation(
            String id,
            int populationSize,
            double mutationRate,
            double crossoverRate,
            double elitismRate,
            FunctionalChromosomeGenerator chromosomeGenerator,
            FunctionalSelectionInterface selectionInterface,
            FunctionalCrossoverInterface crossoverInterface,
            FunctionalMutationInterface mutationInterface,
            FunctionalEvolutionInterface evolutionInterface
    ) {
        super(id, populationSize, mutationRate, crossoverRate, elitismRate, chromosomeGenerator, selectionInterface, crossoverInterface, mutationInterface, evolutionInterface);
    }

    /**
     * Constructor with no Param. {@link BitChromosome} constructor already returns a random {@link BitChromosome}.
     *
     * @return new {@link BitChromosome},
     */
    public static FunctionalChromosomeGenerator generateRandomChromosome(int geneSize, int setBits) {
        return () -> new BitChromosome(geneSize, setBits);
    }

    /**
     * Default call to evolveToMax()
     */

    /**
     * Mutation flips random bits with a chance equal to mutationRate parameter.
     *
     * @return
     */
    public static FunctionalMutationInterface flipBitMuation() {
        return (c, mutationRate) -> {
            final List<Number> newGene = c.getGene();
            for (int i = 0; i < c.getGene().size(); i++) {
                if (ThreadLocalRandom.current().nextFloat() < mutationRate) {
                    newGene.set(i, (newGene.get(i).intValue() == new Integer(1) ? new Integer(0) : new Integer(1)));
                }
            }
            return new BitChromosome(newGene);
        };
    }

    public static FunctionalCrossoverInterface twoPointCrossover() {
        return parentChromosomeList -> {
            List<Chromosome> children = new ArrayList<>();

            final List<Number> chromosomeOneGene = parentChromosomeList.get(0).getGene();
            final List<Number> chromosomeTwoGene = parentChromosomeList.get(1).getGene();
            final int length = chromosomeOneGene.size();

            final int index1 = ThreadLocalRandom.current().nextInt(length - 1);
            final int index2 = index1 + ThreadLocalRandom.current().nextInt(length - index1);

            final List<Number> newChromosomeOneGene = new ArrayList<Number>();
            final List<Number> newChromosomeTwoGene = new ArrayList<Number>();

            for (int i = 0; i < index1; i++) {
                newChromosomeOneGene.add(chromosomeOneGene.get(i));
                newChromosomeTwoGene.add(chromosomeTwoGene.get(i));
            }
            for (int i = index1; i < index2; i++) {
                newChromosomeOneGene.add(chromosomeTwoGene.get(i));
                newChromosomeTwoGene.add(chromosomeOneGene.get(i));
            }
            for (int i = index2; i < length; i++) {
                newChromosomeOneGene.add(chromosomeOneGene.get(i));
                newChromosomeTwoGene.add(chromosomeTwoGene.get(i));
            }

            children.add(parentChromosomeList.get(0).createChild(newChromosomeOneGene));
            children.add(parentChromosomeList.get(1).createChild(newChromosomeTwoGene));

            return children;
        };
    }

    private static BitPopulation createPopulationForMultiThreadCoord3DCallable(double mutationRate, double crossoverRate) {
        return new BitPopulation(null, 200, 5, 200, mutationRate, crossoverRate, 0.01);
    }

    private static BitPopulation createPopulationForMultiThreadCoord3DCallable(
            int populationSize,
            double mutationRate,
            double crossoverRate,
            double elitismRate,
            FunctionalChromosomeGenerator chromosomeGenerator,
            FunctionalSelectionInterface selectionInterface,
            FunctionalCrossoverInterface crossoverInterface,
            FunctionalMutationInterface mutationInterface,
            FunctionalEvolutionInterface evolutionInterface
    ) {
        return new BitPopulation(null, populationSize, mutationRate, crossoverRate, elitismRate, chromosomeGenerator, selectionInterface, crossoverInterface, mutationInterface, evolutionInterface);
    }

    public static MultiThreadCoord3DCallable createMultiThreadCoord3DCallable(
            int runs,
            Range mutationRange,
            Range crossoverRange,
            int mutationSteps,
            int crossoverSteps,
            int maxGenerations,
            double maxFitness
    ) {
        return new MultiThreadCoord3DCallable(runs, mutationRange, crossoverRange, mutationSteps, crossoverSteps) {
            @Override
            protected Callable<Coord3d> createPopulationRunsCallable(double mutationRate, double crossoverRate) {
                return () -> {
                    double generationSum = 0;
                    for (int i = 0; i < getRuns(); i++) {
                        int currentGen = 0;
                        Population population = createPopulationForMultiThreadCoord3DCallable(
                                mutationRate,
                                crossoverRate
                        );
                        while (currentGen < maxGenerations && population.getMaxFittest().getFitness() < maxFitness) {
                            population.evolve();
                            currentGen++;
                        }
                        generationSum += currentGen;
                    }
                    return new Coord3d(mutationRate, crossoverRate, generationSum / getRuns());
                };
            }
        };
    }

    public static MultiThreadCoord3DCallable createMultiThreadCoord3DCallable(
            int runs,
            Range mutationRange,
            Range crossoverRange,
            int mutationSteps,
            int crossoverSteps,
            int maxGenerations,
            double maxFitness,
            int populationSize,
            double elitismRate,
            FunctionalChromosomeGenerator chromosomeGenerator,
            FunctionalSelectionInterface selectionInterface,
            FunctionalCrossoverInterface crossoverInterface,
            FunctionalMutationInterface mutationInterface,
            FunctionalEvolutionInterface evolutionInterface
    ) {
        return new MultiThreadCoord3DCallable(runs, mutationRange, crossoverRange, mutationSteps, crossoverSteps) {
            @Override
            protected Callable<Coord3d> createPopulationRunsCallable(double mutationRate, double crossoverRate) {
                return () -> {
                    double generationSum = 0;
                    for (int i = 0; i < getRuns(); i++) {
                        int currentGen = 0;
                        Population population = createPopulationForMultiThreadCoord3DCallable(
                                populationSize,
                                mutationRate,
                                crossoverRate,
                                elitismRate,
                                chromosomeGenerator,
                                selectionInterface,
                                crossoverInterface,
                                mutationInterface,
                                evolutionInterface
                        );

                        while (currentGen < maxGenerations && population.getMaxFittest().getFitness() < maxFitness) {
                            population.evolve();
                            currentGen++;
                        }
                        generationSum += currentGen;
                    }
                    return new Coord3d(mutationRate, crossoverRate, generationSum / runs);
                };
            }
        };
    }

    public static List<Coord3d> createCoord3DList(
            int runs,
            Range mutationRange,
            Range crossoverRange,
            int mutationSteps,
            int crossoverSteps,
            int maxGenerations,
            double maxFitness) {
        return createCoord3dList(
                runs,
                mutationRange,
                crossoverRange,
                mutationSteps,
                crossoverSteps,
                maxGenerations,
                maxFitness,
                200,
                0.01,
                generateRandomChromosome(200, 5),
                tournamentSelectMax(3),
                onePointCrossover(),
                flipBitMuation(),
                evolveToMaxAndReplicate()
        );
    }

    public static List<Coord3d> createCoord3dList(
            int runs,
            Range mutationRange,
            Range crossoverRange,
            int mutationSteps,
            int crossoverSteps,
            int maxGenerations,
            double maxFitness,
            int populationSize,
            double elitismRate,
            FunctionalChromosomeGenerator chromosomeGenerator,
            FunctionalSelectionInterface selectionInterface,
            FunctionalCrossoverInterface crossoverInterface,
            FunctionalMutationInterface mutationInterface,
            FunctionalEvolutionInterface evolutionInterface) {
        final AtomicInteger counter = new AtomicInteger();

        return GeneticUtilities.createListOfCoord2DFromRanges(mutationRange, crossoverRange, mutationSteps, crossoverSteps)
                .stream()
                .map((c) -> {
                    double mutationRate = c.x;
                    double crossoverRate = c.y;
                    double generationSum = 0;
                    for (int i = 0; i < runs; i++) {
                        Population population = new BitPopulation(
                                null,
                                populationSize,
                                mutationRate,
                                crossoverRate,
                                elitismRate,
                                chromosomeGenerator,
                                selectionInterface,
                                crossoverInterface,
                                mutationInterface,
                                evolutionInterface);
                        int currentGen = 0;
                        while (currentGen < maxGenerations && population.getMaxFittest().getFitness() < maxFitness) {
                            population.evolve();
                            currentGen++;
                        }
                        generationSum += currentGen;
                    }
                    System.out.println(counter.addAndGet(1) + ":" + c.x + "/" + c.y + "/" + (float) generationSum / runs);

                    return new Coord3d(c, (float) generationSum / runs);
                })
                .sorted((o1, o2) -> o1.x == o2.x ? (o1.y > o2.y ? 1 : o1.y == o2.y ? 0 : -1) : (o1.x > o2.x ? 1 : -1))
                .collect(Collectors.toList());
    }

}
