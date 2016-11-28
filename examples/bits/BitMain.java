import org.jfree.chart.*;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by yay on 19.10.2016.
 */
public class BitMain {


    private final static Map<Integer, FunctionalCrossoverInterface> crossoverInterfaceMap;
    private final static Map<Integer, FunctionalMutationInterface> mutationInterfaceMap;
    private final static Map<Integer, FunctionalSelectionInterface> selectionInterfaceMap;
    private final static Map<Integer, FunctionalEvolutionInterface> evolutionInterfaceMap;

    static {
        crossoverInterfaceMap = new HashMap<>();
        crossoverInterfaceMap.put(0, BitPopulation.onePointCrossover());
        crossoverInterfaceMap.put(1, BitPopulation.twoPointCrossover());

        mutationInterfaceMap = new HashMap<>();
        mutationInterfaceMap.put(0, BitPopulation.flipBitMutation());

        selectionInterfaceMap = new HashMap<>();
        selectionInterfaceMap.put(0, chromosomeList -> ThreadLocalRandom.current().nextInt(chromosomeList.size()));
        selectionInterfaceMap.put(1, Population.rouletteSelect());
        selectionInterfaceMap.put(2, Population.tournamentSelectMax(2));
        selectionInterfaceMap.put(3, Population.tournamentSelectMax(4));
        selectionInterfaceMap.put(4, Population.tournamentSelectMax(8));

        evolutionInterfaceMap = new HashMap<>();
        evolutionInterfaceMap.put(0, Population.evolveToMax());
        evolutionInterfaceMap.put(1, Population.evolveToMaxAnd10x10Replicate());
        evolutionInterfaceMap.put(2, Population.evolveToMaxAndTournamentReplicate(5));
        evolutionInterfaceMap.put(3, Population.evolveToMaxAndRankBasedReplicate(2));
    }


    public static void main(String[] args) throws InterruptedException {
        int populationSize = 0;
        int setBits = 0;
        int geneSize = 0;
        float mutationRate = 0;
        float crossoverRate = 0;
        float elitismRate = 0;
        int runs = 0;
        int maxGenerations = 0;

        int crossoverFunction = 0;
        int mutationFunction = 0;
        int selectionFunction = 0;

        int replicationScheme = 0;

        if (args.length == 0) {
            printHelp();
            System.exit(0);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("**********************\n");
        try {
            for (int i = 0; i < args.length; i += 2) {
                if (args[i].toLowerCase().equals("-ps") || args[i].toLowerCase().equals("-populationsize")) { //mutation rate
                    populationSize = Integer.parseInt(args[i + 1]);
                    sb.append("populationSize = " + populationSize);
                    sb.append("\n");
                } else if (args[i].toLowerCase().equals("-sb") || args[i].toLowerCase().equals("-setbits")) { //mutation rate
                    setBits = Integer.parseInt(args[i + 1]);
                    sb.append("setBits = " + setBits);
                    sb.append("\n");
                } else if (args[i].toLowerCase().equals("-gs") || args[i].toLowerCase().equals("-genesize")) { //mutation rate
                    geneSize = Integer.parseInt(args[i + 1]);
                    sb.append("geneSize = " + geneSize);
                    sb.append("\n");
                } else if (args[i].toLowerCase().equals("-mr") || args[i].toLowerCase().equals("-mutationrate")) { //mutation rate
                    mutationRate = Float.parseFloat(args[i + 1]);
                    sb.append("mutationRate = " + mutationRate);
                    sb.append("\n");
                } else if (args[i].toLowerCase().equals("-cr") || args[i].toLowerCase().equals("-crossoverrate")) { //mutation rate
                    crossoverRate = Float.parseFloat(args[i + 1]);
                    sb.append("crossoverRate = " + crossoverRate);
                    sb.append("\n");
                } else if (args[i].toLowerCase().equals("-er") || args[i].toLowerCase().equals("-elitismrate")) { //mutation rate
                    elitismRate = Float.parseFloat(args[i + 1]);
                    sb.append("elitismRate = " + elitismRate);
                    sb.append("\n");
                } else if (args[i].toLowerCase().equals("-r") || args[i].toLowerCase().equals("-runs")) { //mutation rate
                    runs = Integer.parseInt(args[i + 1]);
                    sb.append("runs = " + runs);
                    sb.append("\n");
                } else if (args[i].toLowerCase().equals("-mg") || args[i].toLowerCase().equals("-maxgenerations")) { //mutation rate
                    maxGenerations = Integer.parseInt(args[i + 1]);
                    sb.append("maxGenerations = " + maxGenerations);
                    sb.append("\n");
                } else if (args[i].toLowerCase().equals("-cf") || args[i].toLowerCase().equals("-crossoverfunction")) { //mutation rate
                    crossoverFunction = Integer.parseInt(args[i + 1]);
                    sb.append("crossoverFunction = " + crossoverFunction);
                    sb.append("\n");
                } else if (args[i].toLowerCase().equals("-mf") || args[i].toLowerCase().equals("-mutationfunction")) { //mutation rate
                    mutationFunction = Integer.parseInt(args[i + 1]);
                    sb.append("mutationFunction = " + mutationFunction);
                    sb.append("\n");
                } else if (args[i].toLowerCase().equals("-sf") || args[i].toLowerCase().equals("-selectionfunction")) { //mutation rate
                    selectionFunction = Integer.parseInt(args[i + 1]);
                    sb.append("selectionFunction = " + selectionFunction);
                    sb.append("\n");
                } else if (args[i].toLowerCase().equals("-rs") || args[i].toLowerCase().equals("-replicationscheme")) { //mutation rate
                    replicationScheme = Integer.parseInt(args[i + 1]);
                    sb.append("replicationScheme = " + replicationScheme);
                    sb.append("\n");
                } else if (args[i].toLowerCase().equals("-h") || args[i].toLowerCase().equals("-help")) {
                    printHelp();
                } else {
                    printHelp();
                    System.exit(0);
                }
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            printHelp();
            System.exit(0);
        }
        sb.append("**********************\n");
        System.out.print(sb.toString());


        ArrayBlockingQueue<ChromosomeData> sharedBlockingQueue = new ArrayBlockingQueue(10000);
        List<String> idList = new ArrayList<>();
        for (int i = 0; i < runs; i++) {
            Population population;
            idList.add(Integer.toString(i));
            population = new BitPopulation(
                    idList.get(i),
                    populationSize,
                    mutationRate,
                    crossoverRate,
                    elitismRate,
                    BitPopulation.generateRandomChromosome(geneSize, setBits),
                    selectionInterfaceMap.get(selectionFunction),
                    crossoverInterfaceMap.get(crossoverFunction),
                    mutationInterfaceMap.get(mutationFunction),
                    evolutionInterfaceMap.get(replicationScheme)
            );

            new Thread(new GeneticProducer(maxGenerations, (double) geneSize, population, sharedBlockingQueue)).start();
        }
        Map<String, Integer> idGenerationUntilMaxFitnessMap = new HashMap<>();
        Map<String, Chromosome> idCurrentFittestMap = new HashMap<>();
        Map<String, XYSeries> idXYseriesMap = new HashMap<>();
        XYSeriesCollection dataset = new XYSeriesCollection();

        ChromosomeData cd;
        while ((cd = sharedBlockingQueue.poll(1, TimeUnit.SECONDS)) != null) {
            //max possible fitness
            idGenerationUntilMaxFitnessMap.put(cd.getId(), cd.getCurrentGeneration());
            idCurrentFittestMap.put(cd.getId(), cd.getMaxFittest());
            idXYseriesMap.putIfAbsent(cd.getId(), new XYSeries(String.format("%s max fitness", cd.getId())));
            idXYseriesMap.get(cd.getId()).add(cd.getCurrentGeneration(), cd.getMaxFitnessValue());
        }
        idXYseriesMap.values().stream().forEach((s) -> dataset.addSeries(s));
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Genetic bit flipping",
                "Generations",
                "Fitness",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                false,
                false
        );

        double averageGenerations = idGenerationUntilMaxFitnessMap.values().stream().mapToDouble((i) -> i.doubleValue() + 1).average().getAsDouble();

        XYPlot plot = (XYPlot) chart.getPlot();

        double x = plot.getDomainAxis().getRange().getUpperBound() - plot.getDomainAxis().getRange().getUpperBound() / 10;
        double y = plot.getRangeAxis().getRange().getUpperBound() / 10;
        XYTextAnnotation averageGenerationsAnnotation = new XYTextAnnotation("Avg gens: " + averageGenerations, x, y);
        XYTextAnnotation newLineAnnotation = new XYTextAnnotation("new line", x, y + 12);
        averageGenerationsAnnotation.setTextAnchor(TextAnchor.BASELINE_RIGHT);
        averageGenerationsAnnotation.setFont(new Font("SansSerif", Font.BOLD, 12));
        newLineAnnotation.setTextAnchor(TextAnchor.BASELINE_RIGHT);
        newLineAnnotation.setFont(new Font("SansSerif", Font.BOLD, 12));
        plot.addAnnotation(averageGenerationsAnnotation);
        plot.addAnnotation(newLineAnnotation);

        try {
            ChartUtilities.saveChartAsPNG(new File("bitdata.png"), chart, 400, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final StringBuilder sb1 = new StringBuilder();
        sb1.append("**********************\n");
        idCurrentFittestMap.forEach((id, c) -> {
            sb1.append("id: ");
            sb1.append(id);
            sb1.append("\t");
            sb1.append("fittest: ");
            sb1.append(c.toString());
            sb1.append("\t");
            sb1.append("fitness: ");
            sb1.append((int) c.getFitness());
            sb1.append("\t");
            sb1.append("generation: ");
            sb1.append(idGenerationUntilMaxFitnessMap.get(id).intValue() + 1);
            sb1.append("\n");
        });
        sb1.append("**********************");
        sb1.append("\nAverages generation until max fitness is achieved: ");
        sb1.append(averageGenerations);
        sb1.append("\n");
        sb1.append("**********************");
        System.out.print(sb1.toString());

    }

    static private void printHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("Usage:\n");
        sb.append("\t-ps or -populationsize\t\t\t\tamount of chromosomes per population object.\n");
        sb.append("\t-sb or -setbits\t\t\t\t\t\tamount of bits set in the first generation of chromosomes.\n");
        sb.append("\t-gs or -genesize\t\t\t\t\tsize of gene. amount of bits per gene.\n");
        sb.append("\t-mr or -mutationrate\t\t\t\trate of mutation as float. is passed the mutationfunction..\n");
        sb.append("\t-cr or -cr\t\t\t\t\t\t\trate of crossover as float. is passed the crossoverfunction..\n");
        sb.append("\t-er or -elitismrate\t\t\t\t\trate of elitism as float. top n% of chromosomes which are transferred to next generation before any mutation or crossover occurs.\n");
        sb.append("\t-r or -runs\t\t\t\t\t\t\tamount of population objects to be created and evolved.\n");
        sb.append("\t-mg or -maxgenerations\t\t\t\tamount of generations. equals the max amount of evolve calls per population object.\n");
        sb.append("\t-cf or -crossoverfunction\n\t\tcrossover function. options:\n");
        sb.append("\t\t\t0: one point crossover.\n");
        sb.append("\t-mf or -mutationfunction\n\t\tmutation function. options:\n");
        sb.append("\t\t\t0: random flip of bits with a rate equal to mutationrate.\n");
        sb.append("\t-sf or -selectionfunction\n\t\tselection function. options:\n");
        sb.append("\t\t\t0: complete random selection of parent chromosomes for crossover.\n");
        sb.append("\t\t\t1: roulette select two parents for crossover.\n");
        sb.append("\t\t\t2: tournament select (tournament size n = 2)  select two parents for crossover.\n");
        sb.append("\t\t\t3: tournament select (tournament size n = 4)  select two parents for crossover.\n");
        sb.append("\t\t\t4: tournament select (tournament size n = 8)  select two parents for crossover.\n");
        sb.append("\t-rs or -replicationscheme\n\t\teplication scheme. options:\n");
        sb.append("\t\t\t0: copy entire new generation\n");
        sb.append("\t\t\t1: select top 10% of newly created chromosomes and copy them into new generation, multiplied by 10.\n");
        System.out.print(sb.toString());
    }
}
