import java.sql.Timestamp;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.RecursiveAction;

public class ParQuickSort extends RecursiveAction {
    private static final int BLOCK = 1000;
    private final Random random = new Random(Timestamp.from(Instant.now()).getTime());

    private int[] array;
    private final int begin;
    private final int end;


    public ParQuickSort(int[] array, int begin, int end) {
        this.array = array;
        this.begin = begin;
        this.end = end;
    }

    private ParallelFor moveToInitialArray(int[] movingArray, int offset) {
        return new ParallelFor(0, movingArray.length - 1, pos -> {
            array[offset + pos] = movingArray[pos];
        });
    }

    @Override
    protected void compute() {
        if (end - begin < BLOCK) {
            SeqQuickSort seqQuickSort = new SeqQuickSort(array, begin, end);
            seqQuickSort.sort();
            return;
        }

        int m = Math.abs(this.random.nextInt()) % (end - begin + 1) + begin;

        int mx = array[m];
        Filter filterLeft = new Filter(array, begin, end, x -> x < mx);
        filterLeft.fork();

        Filter filterMiddle = new Filter(array, begin, end, x -> x == mx);
        filterMiddle.fork();

        Filter filterRight = new Filter(array, begin, end, x -> x > mx);
        filterRight.fork();

        int[] left = filterLeft.join();
        int[] middle = filterMiddle.join();
        int[] right = filterRight.join();

        ParallelFor moveLeftParallelFor = moveToInitialArray(left, 0);
        ParallelFor moveMiddleParallelFor = moveToInitialArray(middle, left.length);
        ParallelFor moveRightParallelFor = moveToInitialArray(right, left.length + middle.length);

        moveMiddleParallelFor.fork();

        ParQuickSort quickSortLeft = new ParQuickSort(left, 0, left.length - 1);
        quickSortLeft.fork();

        ParQuickSort quickSortRight = new ParQuickSort(right, 0, right.length - 1);
        quickSortRight.fork();

        quickSortLeft.join();
        moveLeftParallelFor.fork();

        quickSortRight.join();
        moveRightParallelFor.fork();

        moveLeftParallelFor.join();
        moveMiddleParallelFor.join();
        moveRightParallelFor.join();
    }

    public void sort() {
        compute();
    }
}
