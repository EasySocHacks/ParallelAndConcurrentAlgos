import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

public class SeqBfs {
    private final Function<Integer, int[]> getNeighboursFunction;
    private final boolean[] used;
    private final int start, finish;
    private int dimProduct;

    private final short[] dist;

    public SeqBfs(
            Tuple<Integer> dimSize,
            Function<Integer, int[]> getNeighboursFunction,
            int start,
            int finish
    ) {
        this.getNeighboursFunction = getNeighboursFunction;
        this.start = start;
        this.finish = finish;

        dimProduct = 1;
        for (int i = 0; i < dimSize.getSize(); i++) {
            dimProduct *= dimSize.get(i);
        }


        used = new boolean[dimProduct];
        dist = new short[dimProduct];
        for (int i = 0; i < dimProduct; i++)
            dist[i] = -1;
    }

    public int findPathLength() {
        Queue<Integer> queue = new LinkedBlockingQueue<>();
        queue.add(start);
        used[start] = true;
        dist[start] = 0;

        while (!queue.isEmpty()) {
            int v = queue.poll();

            for (int u : getNeighboursFunction.apply(v)) {
                if (u == -1)
                    continue;

                if (used[u])
                    continue;

                queue.add(u);
                used[u] = true;
                dist[u] = (short) (dist[v] + 1);
            }
        }

        return dist[finish];
    }
}
