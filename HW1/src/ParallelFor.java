import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.BiFunction;

public class ParallelFor extends ParPrimitive<Integer, Void> {
    private final int begin;
    private final int end;
    private final BiFunction<ArrayList<Integer>, Integer, Void> f;

    public ParallelFor(
            ArrayList<Integer> list,
            ForkJoinPool forkJoinPool,
            int being,
            int end,
            BiFunction<ArrayList<Integer>, Integer, Void> f
    ) {
        super(list, forkJoinPool);

        this.begin = being;
        this.end = end;
        this.f = f;
    }

    @Override
    public Void proceed() {
        if (end < begin)
            return null;

        if (begin == end) {
            f.apply(list, begin);
            return null;
        }

        int m = (begin + end) / 2;

        RecursiveTask<Void> parallelForLeft = new RecursiveTask<Void>() {
            @Override
            protected Void compute() {
                ParallelFor parallelFor = new ParallelFor(
                        list,
                        forkJoinPool,
                        begin,
                        m,
                        f
                );
                parallelFor.proceed();
                return null;
            }
        };
        RecursiveTask<Void> parallelForRight = new RecursiveTask<Void>() {
            @Override
            protected Void compute() {
                ParallelFor parallelFor = new ParallelFor(
                        list,
                        forkJoinPool,
                        m + 1,
                        end,
                        f
                );
                parallelFor.proceed();
                return null;
            }
        };
        this.forkJoinPool.execute(parallelForLeft);
        this.forkJoinPool.execute(parallelForRight);

        parallelForLeft.join();
        parallelForRight.join();

        return null;
    }
}
