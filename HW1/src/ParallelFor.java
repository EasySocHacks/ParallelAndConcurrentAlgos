import java.util.concurrent.RecursiveAction;
import java.util.function.Consumer;

public class ParallelFor extends RecursiveAction {
    private static final int BLOCK = 1000;

    private final int begin;
    private final int end;
    private final Consumer<Integer> f;

    public ParallelFor(
            int begin,
            int end,
            Consumer<Integer> f
    ) {
        this.begin = begin;
        this.end = end;
        this.f = f;
    }

    @Override
    protected void compute() {
        if (end < begin)
            return;

        if (end - begin < BLOCK) {
            for (int i = begin; i <= end; i++)
                f.accept(i);

            return;
        }

        int m = (begin + end) / 2;
        ParallelFor parallelForLeft = new ParallelFor(begin, m, f);
        ParallelFor parallelForRight = new ParallelFor(m + 1, end, f);

        parallelForLeft.fork();
        parallelForRight.fork();

        parallelForLeft.join();
        parallelForRight.join();
    }
}
