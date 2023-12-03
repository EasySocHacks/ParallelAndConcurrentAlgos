import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Filter extends ParPrimitive<Integer, ArrayList<Integer>> {
    private final int begin;
    private final int end;
    private final Predicate<Integer> predicate;

    public Filter(
            ArrayList<Integer> list,
            ForkJoinPool forkJoinPool,
            int begin,
            int end,
            Predicate<Integer> predicate
    ) {
        super(list, forkJoinPool);

        this.begin = begin;
        this.end = end;
        this.predicate = predicate;
    }

    @Override
    public ArrayList<Integer> proceed() {
        Map map = new Map(
                list,
                forkJoinPool,
                begin,
                end,
                new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer x) {
                        if (predicate.test(x)) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                }
        );
        ArrayList<Integer> flags = map.proceed();

        Scan scan = new Scan(
                flags,
                forkJoinPool,
                begin,
                end,
                0
        );
        ArrayList<Integer> sums = scan.proceed();

        ArrayList<Integer> filteredList = new ArrayList<>(Arrays.asList(new Integer[sums.get(sums.size() - 1)]));
        ParallelFor parallelFor = new ParallelFor(
                sums,
                forkJoinPool,
                begin,
                end,
                new BiFunction<ArrayList<Integer>, Integer, Void>() {
                    @Override
                    public Void apply(ArrayList<Integer> sumsList, Integer pos) {
                        if (sumsList.get(pos) > 0 && (pos == 0 || sumsList.get(pos - 1) < sumsList.get(pos))) {
                            filteredList.set(sumsList.get(pos) - 1, list.get(pos));
                        }

                        return null;
                    }
                }
        );
        parallelFor.proceed();

        return filteredList;
    }
}
