import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.concrete.OrthonormalTessellator;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.text.drawable.DrawableTextBitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by yay on 09.11.2016.
 */
public class GraphicalBitMain {
    private final static Map<Integer, FunctionalCrossoverInterface> crossoverInterfaceMap;
    private final static Map<Integer, FunctionalMutationInterface> mutationInterfaceMap;
    private final static Map<Integer, FunctionalSelectionInterface> selectionInterfaceMap;
    private final static Map<Integer, FunctionalEvolutionInterface> evolutionInterfaceMap;

    private final static Map<FunctionalCrossoverInterface, String> crossoverInterfaceNameMap;
    private final static Map<FunctionalMutationInterface, String> mutationInterfaceNameMap;
    private final static Map<FunctionalSelectionInterface, String> selectionInterfaceNameMap;
    private final static Map<FunctionalEvolutionInterface, String> evolutionInterfaceNameMap;

    static {
        crossoverInterfaceMap = new HashMap<>();
        crossoverInterfaceNameMap = new HashMap<>();
        crossoverInterfaceMap.put(0, BitPopulation.onePointCrossover());
        crossoverInterfaceNameMap.put(crossoverInterfaceMap.get(0), "OnePointCrossover");
        crossoverInterfaceMap.put(1, BitPopulation.twoPointCrossover());
        crossoverInterfaceNameMap.put(crossoverInterfaceMap.get(1), "TwoPointCrossover");


        mutationInterfaceMap = new HashMap<>();
        mutationInterfaceNameMap = new HashMap<>();
        mutationInterfaceMap.put(0, BitPopulation.swapMutation());
        mutationInterfaceNameMap.put(mutationInterfaceMap.get(0), "SwapMutation");

        selectionInterfaceMap = new HashMap<>();
        selectionInterfaceNameMap = new HashMap<>();
        selectionInterfaceMap.put(0, chromosomeList -> ThreadLocalRandom.current().nextInt(chromosomeList.size()));
        selectionInterfaceNameMap.put(selectionInterfaceMap.get(0), "RandomSelect");
        selectionInterfaceMap.put(1, Population.rouletteSelect());
        selectionInterfaceNameMap.put(selectionInterfaceMap.get(1), "RouletteSelect");
        selectionInterfaceMap.put(2, Population.tournamentSelectMax(2));
        selectionInterfaceNameMap.put(selectionInterfaceMap.get(2), "TournamentSelect(2)");
        selectionInterfaceMap.put(3, Population.tournamentSelectMax(4));
        selectionInterfaceNameMap.put(selectionInterfaceMap.get(3), "TournamentSelect(4)");
        selectionInterfaceMap.put(4, Population.tournamentSelectMax(8));
        selectionInterfaceNameMap.put(selectionInterfaceMap.get(4), "TournamentSelect(8)");

        evolutionInterfaceMap = new HashMap<>();
        evolutionInterfaceNameMap = new HashMap<>();
        evolutionInterfaceMap.put(0, Population.evolveToMax());
        evolutionInterfaceNameMap.put(evolutionInterfaceMap.get(0), "EvolveToMax");
        evolutionInterfaceMap.put(1, Population.evolveToMaxAnd10x10Replicate());
        evolutionInterfaceNameMap.put(evolutionInterfaceMap.get(1), "EvolveToMaxAndReplicate10x10");
        evolutionInterfaceMap.put(2, Population.evolveToMaxAndRankBasedReplicate(2));
        evolutionInterfaceNameMap.put(evolutionInterfaceMap.get(2), "EvolveToMaxAndRankBasedReplicate(2)");
        evolutionInterfaceMap.put(3, Population.evolveToMaxAndTournamentReplicate(3));
        evolutionInterfaceNameMap.put(evolutionInterfaceMap.get(3), "EvolveToMaxAndTournamentReplicate(3)");
        evolutionInterfaceMap.put(4, Population.evolveToMaxAndTournamentReplicate(5));
        evolutionInterfaceNameMap.put(evolutionInterfaceMap.get(4), "EvolveToMaxAndTournamentReplicate(5)");
    }


    public static void main(String[] args) {

        if (args.length == 0) {
            printHelp();
            System.exit(0);
        }

        int setBits = 0;
        int geneSize = 0;
        int runs = 0;
        Range mutationRange = null;
        Range crossoverRange = null;
        int mutationSteps = 0;
        int crossoverSteps = 0;
        int maxGenerations = 0;
        int populationSize = 0;
        double elitismRate = 0;
        FunctionalSelectionInterface selectionInterface = null;
        FunctionalCrossoverInterface crossoverInterface = null;
        FunctionalMutationInterface mutationInterface = null;
        FunctionalEvolutionInterface evolutionInterface = null;

        //-ps 200 -sb 5 -gs 200 -mr 0.1 0.1 5 -cr 0.5 0.99 5 -er 0.01 -r 10 -mg 200 -cf 0 -mf 0 -sf 2 -rs 1
        StringBuilder sb = new StringBuilder();
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
                } else if (args[i].toLowerCase().equals("-mr") || args[i].toLowerCase().equals("-mutationrange")) { //mutation rate
                    mutationRange = new Range(Float.parseFloat(args[i + 1]), Float.parseFloat(args[i + 2]));
                    mutationSteps = Integer.parseInt(args[i + 3]);
                    i += 2;
                    sb.append("mutationRange = " + "[" + mutationRange.getMin() + " : " + mutationRange.getMax() + "]" + " steps: " + mutationSteps);
                    sb.append("\n");
                } else if (args[i].toLowerCase().equals("-cr") || args[i].toLowerCase().equals("-crossoverrange")) { //mutation rate
                    crossoverRange = new Range(Float.parseFloat(args[i + 1]), Float.parseFloat(args[i + 2]));
                    crossoverSteps = Integer.parseInt(args[i + 3]);
                    i += 2;
                    sb.append("crossoverRange = " + "[" + crossoverRange.getMin() + " : " + crossoverRange.getMax() + "]" + " steps: " + crossoverSteps);
                    sb.append("\n");
                } else if (args[i].toLowerCase().equals("-er") || args[i].toLowerCase().equals("-elitismrate")) { //mutation rate
                    elitismRate = Double.parseDouble(args[i + 1]);
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
                    crossoverInterface = crossoverInterfaceMap.get(Integer.parseInt(args[i + 1]));
                    sb.append("crossoverFunction = " + Integer.parseInt(args[i + 1]));
                    sb.append("\n");
                } else if (args[i].toLowerCase().equals("-mf") || args[i].toLowerCase().equals("-mutationfunction")) { //mutation rate
                    mutationInterface = mutationInterfaceMap.get(Integer.parseInt(args[i + 1]));
                    sb.append("mutationFunction = " + Integer.parseInt(args[i + 1]));
                    sb.append("\n");
                } else if (args[i].toLowerCase().equals("-sf") || args[i].toLowerCase().equals("-selectionfunction")) { //mutation rate
                    selectionInterface = selectionInterfaceMap.get(Integer.parseInt(args[i + 1]));
                    sb.append("selectionFunction = " + Integer.parseInt(args[i + 1]));
                    sb.append("\n");
                } else if (args[i].toLowerCase().equals("-rs") || args[i].toLowerCase().equals("-replicationscheme")) { //mutation rate
                    evolutionInterface = evolutionInterfaceMap.get(Integer.parseInt(args[i + 1]));
                    sb.append("replicationScheme = " + Integer.parseInt(args[i + 1]));
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

        long start = System.currentTimeMillis();
        List<Coord3d> coord3dList = BitPopulation.createCoord3dList(
                runs,
                mutationRange,
                crossoverRange,
                mutationSteps,
                crossoverSteps,
                maxGenerations,
                geneSize,
                populationSize,
                elitismRate,
                BitPopulation.generateRandomChromosome(geneSize, setBits),
                selectionInterface,
                crossoverInterface,
                mutationInterface,
                evolutionInterface
        );

        long end = System.currentTimeMillis();

        OrthonormalTessellator tesselator = new OrthonormalTessellator();
        Shape surface = (Shape) tesselator.build(coord3dList);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(true);
        surface.setWireframeColor(Color.BLACK);

        // Create a chart and add the surface
        Coord3d lowestRunsCoord3D = coord3dList.stream()
                .min((o1, o2) -> o1.z > o2.z ? 1 : o1.z < o2.y ? -1 : 0)
                .get();

        Coord3d maxRunsCoord3D = coord3dList.stream()
                .max((o1, o2) -> o1.z > o2.z ? 1 : o1.z < o2.y ? -1 : 0)
                .get();

        /*sb = new StringBuilder();
        sb.append("Best with: ");
        sb.append("Mutation Rate: ");
        sb.append(lowestRunsCoord3D.x);
        sb.append("\n");
        sb.append("Crossover Rate: ");
        sb.append(lowestRunsCoord3D.y);
        sb.append("\n");
        sb.append("Generations: ");
        sb.append(lowestRunsCoord3D.z);
        sb.append("\n");
        sb.append("Time: ");
        sb.append((end - start) / 100);
        sb.append("s");*/

        float bestMutation = lowestRunsCoord3D.x;
        float bestCrossover = lowestRunsCoord3D.y;
        float bestGeneration = lowestRunsCoord3D.z;
        float time = (end - start) / 1000;
        String elitismString = Double.toString(elitismRate);
        String crossoverString = crossoverInterfaceNameMap.get(crossoverInterface);
        String mutationString = mutationInterfaceNameMap.get(mutationInterface);
        String selectionString = selectionInterfaceNameMap.get(selectionInterface);
        String replicationString = evolutionInterfaceNameMap.get(evolutionInterface);

        final Chart chart = new AWTChart(Quality.Advanced);
        chart.getScene().getGraph().add(surface);


        getListOfDrawableTextBitmap(
                maxRunsCoord3D.z,
                bestMutation,
                bestCrossover,
                bestGeneration,
                time,
                elitismString,
                crossoverString,
                mutationString,
                selectionString,
                replicationString).forEach((dtb) -> {
            chart.getScene().getGraph().add(dtb);
        });


        chart.getAxeLayout().setXAxeLabel("Mutation rate");
        chart.getAxeLayout().setYAxeLabel("Crossover rate");
        chart.getAxeLayout().setZAxeLabel("Average generations");

        ChartLauncher.openChart(chart);



        /*MultiThreadCoord3DCallable multiThreadCoord3DCallable = BitPopulation.createMultiThreadCoord3DCallable(runs,mutationRange,crossoverRange,mutationSteps,crossoverSteps,maxGenerations,maxFitness);
        try {
            ExecutorService execSvc = Executors.newCachedThreadPool();
            //Future<List<Coord3d>> results = execSvc.submit(multiThreadCoord3DCallable);
            List<Coord3d> results = multiThreadCoord3DCallable.call();
            for(Coord3d c: results) {
                StringBuilder sb = new StringBuilder();
                sb.append(c.x);
                sb.append(" : ");
                sb.append(c.y);
                sb.append(" : ");
                sb.append(c.z);
                sb.append("\n");
                System.out.print(sb.toString());
            }


        OrthonormalTessellator tesselator = new OrthonormalTessellator();
        Shape surface =  (Shape)tesselator.build(results);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(false);
        surface.setWireframeColor(Color.BLACK);

        // Create a chart and add the surface
        Chart chart = new AWTChart(Quality.Advanced);
        chart.add(surface);
        chart.open("Jzy3d Demo", 600, 600);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    static private void printHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("Usage:\n");
        sb.append("\t-ps or -populationsize\t\t\t\tamount of chromosomes per population object.\n");
        sb.append("\t-sb or -setbits\t\t\t\t\t\tamount of bits set in the first generation of chromosomes.\n");
        sb.append("\t-gs or -genesize\t\t\t\t\tsize of gene. amount of bits per gene.\n");
        sb.append("\t-mr or -mutationrange\t\t\t\trange of mutation. min rate, max rate and steps.\n");
        sb.append("\t-cr or -crossoverrange\t\t\t\t\t\t\trange of crossover min rate, max rate and steps\n");
        sb.append("\t-er or -elitismrate\t\t\t\t\trate of elitism as float. top n% of chromosomes which are transferred to next generation before any mutation or crossover occurs.\n");
        sb.append("\t-r or -runs\t\t\t\t\t\t\tamount of population objects to be created and evolved.\n");
        sb.append("\t-mg or -maxgenerations\t\t\t\tamount of generations. equals the max amount of evolve calls per population object.\n");
        sb.append("\t-cf or -crossoverfunction\n\t\tcrossover function. options:\n");
        sb.append("\t\t\t0: one point crossover.\n");
        sb.append("\t\t\t1: two point crossover.\n");
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

    static private List<DrawableTextBitmap> getListOfDrawableTextBitmap(
            float baseZ,
            float bestMutation,
            float bestCrossover,
            float bestGeneration,
            float time,
            String elitismString,
            String crossoverString,
            String mutationString,
            String selectionString,
            String replicationString
    ) {
        final int baseX = 0;
        final int baseY = 2;
        int zOffset = 0;

        List<DrawableTextBitmap> drawableTextBitmapList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        drawableTextBitmapList.add(new DrawableTextBitmap(sb.toString(), new Coord3d(baseX, baseY, baseZ + zOffset), Color.MAGENTA));
        zOffset -= baseZ/6;

        sb.append("Mutation Rate: ");
        sb.append(bestMutation);
        sb.append(" ");

        sb.append("Crossover Rate: ");
        sb.append(bestCrossover);
        sb.append(" ");

        sb.append("Generations: ");
        sb.append(bestGeneration);
        sb.append(" ");

        drawableTextBitmapList.add(new DrawableTextBitmap(sb.toString(), new Coord3d(baseX, baseY, baseZ + zOffset), Color.MAGENTA));
        zOffset -= baseZ/6;

        sb = new StringBuilder();
        sb.append("Time: ");
        sb.append(time);
        sb.append("s");
        sb.append(" ");

        sb.append("Elitism: ");
        sb.append(elitismString);
        sb.append(" ");

        sb.append("Crossover: ");
        sb.append(crossoverString);
        sb.append(" ");

        drawableTextBitmapList.add(new DrawableTextBitmap(sb.toString(), new Coord3d(baseX, baseY, baseZ + zOffset), Color.MAGENTA));
        zOffset -= baseZ/6;

        sb = new StringBuilder();
        sb.append("Mutation: ");
        sb.append(mutationString);
        sb.append(" ");

        sb.append("Selections: ");
        sb.append(selectionString);
        sb.append(" ");

        sb.append("Replication: ");
        sb.append(replicationString);
        sb.append(" ");

        drawableTextBitmapList.add(new DrawableTextBitmap(sb.toString(), new Coord3d(baseX, baseY, baseZ + zOffset), Color.MAGENTA));

        return drawableTextBitmapList;
    }
}

