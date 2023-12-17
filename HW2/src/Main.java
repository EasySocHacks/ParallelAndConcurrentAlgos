import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class Main {

    public static int[] getCubeGraphNeighbours(int pos) {
       int[] neighbours = new int[6];

        int[] dx = {1, -1, 0, 0, 0, 0};
        int[] dy = {0, 0, 1, -1, 0, 0};
        int[] dz = {0, 0, 0, 0, 1, -1};

        int x, y, z;

        z = pos % 500;
        pos /= 500;
        y = pos % 500;
        pos /= 500;
        x = pos % 500;

        int idx = -1;
        for (int i = 0; i < 6; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            int newZ = z + dz[i];

            if (newX < 0 || newY < 0 || newZ < 0 || newX >= 500 || newY >= 500 || newZ >= 500) {
                continue;
            }

            idx++;
            neighbours[idx] = newX * 500 * 500 + newY * 500 + newZ;
        }

        if (idx + 1 < 6)
            neighbours[idx + 1] = -1;

        return neighbours;
    }

    public static int getCubeGraphNeighboursDeg(int pos) {
        return 6;
    }

    public static void runSample(BfsType bfsType) {
        Instant start = Instant.now();
        //---------------------------
        if (bfsType == BfsType.SEQ) {
            System.out.println("[SEQ START]");
            System.out.flush();
            SeqBfs bfs = new SeqBfs(
                    new Tuple<Integer>(500, 500, 500),
                    Main::getCubeGraphNeighbours,
                    0,
                    499 * 499 * 499
            );
            bfs.findPathLength();
        }

        if (bfsType == BfsType.PAR) {
            System.out.println("[PAR START]");
            System.out.flush();
            ParBfs bfs = new ParBfs(
                    new Tuple<Integer>(500, 500, 500),
                    Main::getCubeGraphNeighbours,
                    Main::getCubeGraphNeighboursDeg,
                    0,
                    499 * 499 * 499
            );
            ForkJoinPool forkJoinPool = new ForkJoinPool(4);
            forkJoinPool.invoke(bfs);
        }
        //---------------------------
        Instant end = Instant.now();

        Duration deltaTime = Duration.between(start, end);
        System.out.println(deltaTime.toSeconds() + "s");
        System.out.println(deltaTime.toMillis() + "ms");
        System.out.println(deltaTime.toNanos() + "ns");

        if (bfsType == BfsType.SEQ) {
            System.out.println("[SEQ END]");
        }
        if (bfsType == BfsType.PAR) {
            System.out.println("[PAR END]");
        }
        System.out.flush();
    }

    public static void main(String[] args) {
        System.out.println("---------------");

        for (BfsType type : BfsType.values()) {
            if (type == BfsType.SEQ)
                continue;

            System.out.println("[[[" + type + "]]]");

            for (int i = 0; i < 5; i++) {
                runSample(type);
            }
            System.out.println("===============");
        }
    }
}