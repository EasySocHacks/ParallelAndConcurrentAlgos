import java.util.concurrent.RecursiveTask;
import java.util.function.Predicate;

public class Filter extends RecursiveTask<int[]> {
    private int[] array;
    private int[] arrayCopy;
    private final int begin;
    private final int end;
    private final Predicate<Integer> predicate;

    public Filter(
            int[] array,
            int begin,
            int end,
            Predicate<Integer> predicate
    ) {
        this.array = array;
        this.begin = begin;
        this.end = end;
        this.predicate = predicate;

        this.arrayCopy = array.clone();
    }

    @Override
    protected int[] compute() {
        int[] flags = new int[array.length];
        ParallelFor testPredicate = new ParallelFor(begin, end, pos -> {
            if (predicate.test(pos)) {
                flags[pos] = 1;
            } else {
                flags[pos] = 0;
            }
        });
        testPredicate.compute();

        Scan scanFlags = new Scan(flags, begin, end);
        scanFlags.compute();

        int[] result = new int[flags[flags.length - 1]];
        ParallelFor parallelFor = new ParallelFor(begin, end, pos -> {
            if ((pos == 0 && flags[0] != 0) || (pos > 0 && flags[pos] != flags[pos - 1])) {
                result[flags[pos] - 1] = arrayCopy[pos];
            }
        });
        parallelFor.compute();

        return result;
    }
}
