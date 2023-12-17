import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class ParBfs extends RecursiveAction {
    private final Function<Integer, int[]> getNeighboursFunction;
    private final Function<Integer, Integer> getDegFunction;
    private final int start, finish;
    private int dimProduct;

    private final short[] dist;

    private final int[] checkArray;

    private int[] currentFront;
    private int[] nextFront;

    public ParBfs(
            Tuple<Integer> dimSize,
            Function<Integer, int[]> getNeighboursFunction,
            Function<Integer, Integer> getDegFunction,
            int start,
            int finish
    ) {
        this.getNeighboursFunction = getNeighboursFunction;
        this.getDegFunction = getDegFunction;
        this.start = start;
        this.finish = finish;

        dimProduct = 1;
        for (int i = 0; i < dimSize.getSize(); i++) {
            dimProduct *= dimSize.get(i);
        }

        dist = new short[dimProduct];
        for (int i = 0; i < dimProduct; i++)
            dist[i] = -1;

        checkArray = new int[dimProduct];
        for (int i = 0; i < dimProduct; i++)
            checkArray[i] = -1;
    }

    public int getResult() {
        return dist[finish];
    }

    @Override
    protected void compute() {
        currentFront = new int[1];
        currentFront[0] = start;
        dist[start] = 0;

        while (true) {
            if (currentFront.length == 0)
                break;

            int[] deg = new int[currentFront.length];
            ParallelFor computeDegsParallelFor = new ParallelFor(
                    0,
                    currentFront.length - 1,
                    pos -> deg[pos] = getDegFunction.apply(currentFront[pos])
            );
            computeDegsParallelFor.compute();

            Scan degScan = new Scan(
                    deg,
                    0,
                    deg.length - 1
            );
            degScan.compute();

            nextFront = new int[deg[deg.length - 1]];

            ParallelFor clearNextFront = new ParallelFor(
                    0,
                    nextFront.length - 1,
                    pos -> nextFront[pos] = -1
            );
            clearNextFront.compute();

            ParallelFor parallelFor = new ParallelFor(
                    0,
                    currentFront.length - 1,
                    pos -> {
                        int v = currentFront[pos];

                        int innderIndex = -1;
                        for (int u : getNeighboursFunction.apply(v)) {
                            if (u == -1)
                                break;

                            innderIndex++;

                            if (dist[u] != -1)
                                continue;

                            nextFront[(pos > 0 ? deg[pos - 1] : 0) + innderIndex] = u;
                            dist[u] = (short) (dist[v] + 1);
                            checkArray[u] = (pos > 0 ? deg[pos - 1] : 0) + innderIndex;
                        }
                    }
            );
            parallelFor.compute();

            Filter filterNextFront = new Filter(
                    nextFront,
                    0,
                    nextFront.length - 1,
                    pos -> {
                        int x = nextFront[pos];
                        return x != -1 && checkArray[x] == pos;
                    }
            );
            currentFront = filterNextFront.compute();
        }
    }
}
