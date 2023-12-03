import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiFunction;

class ScanSerial {
    private final ArrayList<Integer> list;
    private final int begin;
    private final int end;
    private final int initialValue;

    public ScanSerial(
            ArrayList<Integer> list,
            int begin,
            int end,
            int initialValue
    ) {
        this.list = list;
        this.begin = begin;
        this.end = end;
        this.initialValue = initialValue;
    }

    public ArrayList<Integer> proceed() {
        if (end < begin) {
            return null;
        }

        ArrayList<Integer> scanList = new ArrayList<>(Arrays.asList(new Integer[end - begin + 1]));
        scanList.set(0, this.initialValue + this.list.get(begin));

        for (int i = begin + 1; i <= end; i++) {
            scanList.set(i - begin, scanList.get(i - begin - 1) + this.list.get(i));
        }

        return scanList;
    }
}

public class Scan extends ParPrimitive<Integer, ArrayList<Integer>> {
    private final static int BLOCK = 1000;

    private final int begin;
    private final int end;
    private final int initialValue;

    public Scan(
            ArrayList<Integer> list,
            ForkJoinPool forkJoinPool,
            int begin,
            int end,
            int initialValue
    ) {
        super(list, forkJoinPool);

        this.begin = begin;
        this.end = end;
        this.initialValue = initialValue;
    }

    @Override
    public ArrayList<Integer> proceed() {
        if (end - begin < BLOCK) {
            ScanSerial scanSerial = new ScanSerial(
                    list,
                    begin,
                    end,
                    0
            );
            return scanSerial.proceed();
        }

        ArrayList<Integer> sums = new ArrayList<>(Arrays.asList(new Integer[(int) Math.ceil((((double) this.list.size()) / BLOCK))]));
        ParallelFor parallelFor = new ParallelFor(
                this.list,
                this.forkJoinPool,
                0,
                ((int) Math.ceil(((double) this.list.size()) / BLOCK)) - 1,
                new BiFunction<ArrayList<Integer>, Integer, Void>() {
                    @Override
                    public Void apply(ArrayList<Integer> list, Integer pos) {
                        int ans = 0;

                        for (int i = 0; i < BLOCK && pos * BLOCK + i < list.size(); i++) {
                            ans += list.get(pos * BLOCK + i);
                        }

                        sums.set(pos, ans);

                        return null;
                    }
                }
        );
        parallelFor.proceed();

        Scan sumsScan = new Scan(
                sums,
                forkJoinPool,
                0,
                sums.size() - 1,
                0
        );
        ArrayList<Integer> sumsScanList = sumsScan.proceed();

        ArrayList<Integer> scanAnswer = new ArrayList<>(Arrays.asList(new Integer[list.size()]));
        parallelFor = new ParallelFor(
                list,
                forkJoinPool,
                0,
                ((int) Math.ceil(((double) this.list.size()) / BLOCK)) - 1,
                new BiFunction<ArrayList<Integer>, Integer, Void>() {
                    @Override
                    public Void apply(ArrayList<Integer> list, Integer pos) {
                        int acc = initialValue;
                        if (pos > 0) {
                            acc = sumsScanList.get(pos - 1);
                        }

                        for (int i = 0; i < BLOCK && pos * BLOCK + i < list.size(); i++) {
                            acc += list.get(pos * BLOCK + i);

                            scanAnswer.set(pos * BLOCK + i, acc);
                        }

                        return null;
                    }
                }
        );
        parallelFor.proceed();

        return scanAnswer;
    }
}
