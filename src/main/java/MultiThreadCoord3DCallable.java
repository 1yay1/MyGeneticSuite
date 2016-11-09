import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.concrete.OrthonormalTessellator;
import org.jzy3d.plot3d.primitives.Shape;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by yay on 09.11.2016.
 */
public abstract class MultiThreadCoord3DCallable implements Callable<List<Coord3d>> {
    private final Range mutationRange;
    private final Range crossoverRange;
    private final int mutationSteps;
    private final int crossoverSteps;
    private final int runs;

    public Range getMutationRange() {
        return mutationRange;
    }

    public Range getCrossoverRange() {
        return crossoverRange;
    }

    public int getMutationSteps() {
        return mutationSteps;
    }

    public int getCrossoverSteps() {
        return crossoverSteps;
    }

    public int getRuns() {
        return runs;
    }

    private final List<Callable<Coord3d>> populationCallables;

    public MultiThreadCoord3DCallable(int runs,
                                      Range mutationRange,
                                      Range crossoverRange,
                                      int mutationSteps,
                                      int crossoverSteps) {
        this.runs = runs;
        this.mutationRange = mutationRange;
        this.crossoverRange = crossoverRange;
        this.mutationSteps = mutationSteps;
        this.crossoverSteps = crossoverSteps;

        this.populationCallables = createPopulationCallables();
    }


    private List<Callable<Coord3d>> createPopulationCallables() {
        double xstep = (double) this.mutationRange.getRange() / (double) (this.mutationSteps - 1);
        double ystep = (double) this.crossoverRange.getRange() / (double) (this.crossoverSteps - 1);
        ArrayList callables = new ArrayList(this.mutationSteps * this.crossoverSteps);

        for (int xi = 0; xi < this.mutationSteps; ++xi) {
            for (int yi = 0; yi < this.crossoverSteps; ++yi) {
                double mutationRate = (double) this.mutationRange.getMin() + (double) xi * xstep;
                double crossoverRate = (double) this.crossoverRange.getMin() + (double) yi * ystep;
                callables.add(createPopulationRunsCallable(mutationRate, crossoverRate));
            }
        }

        return callables;

    }

    protected abstract Callable<Coord3d> createPopulationRunsCallable(double mutationRate, double crossoverRate);


    @Override
    public List<Coord3d> call() throws Exception {
        ExecutorService execSvc = Executors.newCachedThreadPool();
        List<? extends Future> results = execSvc.invokeAll(populationCallables);

        return results.stream()
                .map((f) -> {
                    Coord3d coord3d = null;
                    try {
                        coord3d = (Coord3d) f.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return coord3d;
                }).sorted((o1, o2) -> {
                    if (o1.x == o2.x) {
                        return o1.y > o2.y ? 1 : o1.y == o2.y ? 0 : -1;
                    } else {
                        return o1.x > o2.x ? 1 : -1;
                    }
                }).collect(Collectors.toList());
    }

    public static Shape getShape(List<Coord3d> coord3dList) {
        OrthonormalTessellator tesselator = new OrthonormalTessellator();
        return (Shape) tesselator.build(coord3dList);
    }
}
