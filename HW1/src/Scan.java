import java.util.concurrent.RecursiveAction;

public class Scan extends RecursiveAction {
    private final static int BLOCK = 1000;

    private int[] array;
    private final int begin;
    private final int end;

    public Scan(
            int[] array,
            int begin,
            int end
    ) {
        this.array = array;
        this.begin = begin;
        this.end = end;
    }

    private void scanSerial() {
        for (int i = begin + 1; i <= end; i++) {
            array[i] += array[i - 1];
        }
    }

    @Override
    protected void compute() {
        if (end - begin < BLOCK) {
            scanSerial();
            return;
        }

        int blockCount = (int) Math.ceil((double) array.length / BLOCK);
        int[] sums = new int[blockCount];
        ParallelFor computeSumsParalellFor = new ParallelFor(0, blockCount - 1, block -> {
            for (int i = 0; i < BLOCK && block * BLOCK + i < array.length; i++) {
                sums[block] += array[block * BLOCK + i];
            }
        });
        computeSumsParalellFor.compute();

        Scan sumsScan = new Scan(sums, 0, sums.length - 1);
        sumsScan.compute();

        ParallelFor computeFinalScanParallelFor = new ParallelFor(0, blockCount - 1, block -> {
            for (int i = 0; i < BLOCK && block * BLOCK + i < array.length; i++) {
                if (i == 0) {
                    if (block != 0) {
                        array[block * BLOCK + i] += sums[block - 1];
                    }
                } else {
                    array[block * BLOCK + i] += array[block * BLOCK + i - 1];
                }
            }
        });
        computeFinalScanParallelFor.compute();
    }
}
