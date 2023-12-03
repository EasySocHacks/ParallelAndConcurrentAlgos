import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Map extends ParPrimitive<Integer, ArrayList<Integer>> {
    private static final int BLOCK = 1000;

    private final int begin;
    private final int end;
    private final Function<Integer, Integer> f;

    private ArrayList<Integer> mappedList = new ArrayList<>();

    public Map(
            ArrayList<Integer> list,
            ForkJoinPool forkJoinPool,
            int being,
            int end,
            Function<Integer, Integer> f
    ) {
        super(list, forkJoinPool);

        this.begin = being;
        this.end = end;
        this.f = f;

        this.mappedList = new ArrayList<>(Arrays.asList(new Integer[list.size()]));
    }

    @Override
    public ArrayList<Integer> proceed() {
        if (end < begin)
            return null;

        if (end - begin < BLOCK) {
            for (int i = begin; i <= end; i++) {
                mappedList.set(i, f.apply(list.get(i)));
            }

            return mappedList;
        }

        int m = (begin + end) / 2;

        RecursiveTask<ArrayList<Integer>> parallelForLeft = new RecursiveTask<>() {
            @Override
            protected ArrayList<Integer> compute() {
                Map map = new Map(
                        list,
                        forkJoinPool,
                        begin,
                        m,
                        f
                );
                return map.proceed();
            }
        };
        RecursiveTask<ArrayList<Integer>> parallelForRight = new RecursiveTask<>() {
            @Override
            protected ArrayList<Integer> compute() {
                Map map = new Map(
                        list,
                        forkJoinPool,
                        m + 1,
                        end,
                        f
                );
                return map.proceed();
            }
        };
        this.forkJoinPool.execute(parallelForLeft);
        this.forkJoinPool.execute(parallelForRight);

        ArrayList<Integer> left = parallelForLeft.join();
        ArrayList<Integer> right = parallelForRight.join();

        ParallelFor leftParallelFor = new ParallelFor(
                left,
                forkJoinPool,
                begin,
                m,
                new BiFunction<ArrayList<Integer>, Integer, Void>() {
                    @Override
                    public Void apply(ArrayList<Integer> innerList, Integer pos) {
                        mappedList.set(pos, innerList.get(pos));

                        return null;
                    }
                }
        );
        leftParallelFor.proceed();

        ParallelFor rightParallelFor = new ParallelFor(
                right,
                forkJoinPool,
                m + 1,
                end,
                new BiFunction<ArrayList<Integer>, Integer, Void>() {
                    @Override
                    public Void apply(ArrayList<Integer> innerList, Integer pos) {
                        mappedList.set(pos, innerList.get(pos));

                        return null;
                    }
                }
        );
        rightParallelFor.proceed();

        return this.mappedList;
    }
}
