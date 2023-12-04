import java.sql.Timestamp;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.RecursiveAction;

public class ParQuickSortV2 extends RecursiveAction {
    private static final int BLOCK = 1000;
    private final Random random = new Random(Timestamp.from(Instant.now()).getTime());

    private int[] array;
    private final int begin;
    private final int end;


    public ParQuickSortV2(int[] array, int begin, int end) {
        this.array = array;
        this.begin = begin;
        this.end = end;
    }
    @Override
    protected void compute() {
        if (end <= begin)
            return;

        if (end - begin < BLOCK) {
            SeqQuickSort seqQuickSort = new SeqQuickSort(array, begin, end);
            seqQuickSort.sort();
            return;
        }

        int m = (begin + end) / 2;

        ParQuickSortV2 leftQuickSort = new ParQuickSortV2(array, begin, m);
        ParQuickSortV2 rightQuickSort = new ParQuickSortV2(array, m + 1, end);

        invokeAll(
                leftQuickSort,
                rightQuickSort
        );
    }
}
