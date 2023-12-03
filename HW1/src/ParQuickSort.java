import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.function.Predicate;

public class ParQuickSort extends QuickSort<Integer> {
    private static final int BLOCK = 1000;
    private final Random random = new Random(Timestamp.from(Instant.now()).getTime());
    private final ForkJoinPool forkJoinPool = new ForkJoinPool(4);

    public ParQuickSort(ArrayList<Integer> list) {
        super(list);
    }

    private void seqSort(int begin, int end) {
        if (end <= begin)
            return;

        int l = begin;
        int r = end;
        int m = Math.abs(this.random.nextInt()) % (end - begin + 1) + begin;

        while (l < r) {
            while (l < m && this.list.get(l) <= this.list.get(m)) {
                l++;
            }

            while (r > m && this.list.get(r) >= this.list.get(m)) {
                r--;
            }

            Collections.swap(this.list, l, r);

            if (l == m) {
                m = r;
            } else if (r == m) {
                m = l;
            }
        }

        seqSort(begin, m);
        seqSort(m + 1, end);
    }

    private void parSort(int begin, int end) {
        if (end - begin < BLOCK) {
            seqSort(begin, end);
            return;
        }

        int m = Math.abs(this.random.nextInt()) % (end - begin + 1) + begin;

        Filter filterLeft = new Filter(
                list,
                forkJoinPool,
                begin,
                end,
                new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer x) {
                        return x < list.get(m);
                    }
                }
        );
        ArrayList<Integer> left = filterLeft.proceed();

        Filter filterMiddle = new Filter(
                list,
                forkJoinPool,
                begin,
                end,
                new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer x) {
                        return x == list.get(m);
                    }
                }
        );
        ArrayList<Integer> middle = filterMiddle.proceed();

        Filter filterRight = new Filter(
                list,
                forkJoinPool,
                begin,
                end,
                new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer x) {
                        return x > list.get(m);
                    }
                }
        );
        ArrayList<Integer> right = filterRight.proceed();

        RecursiveTask<ArrayList<Integer>> quickSortLeft = new RecursiveTask<ArrayList<Integer>>() {
            @Override
            protected ArrayList<Integer> compute() {
                ParQuickSort parQuickSort = new ParQuickSort(left);
                parQuickSort.sort();

                return parQuickSort.getList();
            }
        };
        RecursiveTask<ArrayList<Integer>> quickSortRight = new RecursiveTask<ArrayList<Integer>>() {
            @Override
            protected ArrayList<Integer> compute() {
                ParQuickSort parQuickSort = new ParQuickSort(right);
                parQuickSort.sort();

                return parQuickSort.getList();
            }
        };
        this.forkJoinPool.execute(quickSortLeft);
        this.forkJoinPool.execute(quickSortRight);

        ArrayList<Integer> sortedLeft = quickSortLeft.join();
        ArrayList<Integer> sortedRight = quickSortRight.join();

        list = new ArrayList<>();
        list.addAll(sortedLeft);
        list.addAll(middle);
        list.addAll(sortedRight);
    }

    @Override
    public void sort() {
        parSort(0, this.list.size() - 1);
    }
}
