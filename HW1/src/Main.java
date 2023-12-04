import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static int[] generateRandomArray(int initSize) {
        Random random = new Random(Timestamp.from(Instant.now()).getTime());

        int[] array = new int[initSize];
        for (int i = 0; i < initSize; i++) {
            array[i] = random.nextInt();
        }

        return array;
    }

    public static int[] generateSeqArray(int initSize) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < initSize; i++) {
            list.add(i);
        }
        Collections.shuffle(list);

        return list.stream().mapToInt(i -> i).toArray();
    }

    public static void runSample(int[] array, QuickSortType quickSortType) {
        Instant start = Instant.now();
        //---------------------------
        if (quickSortType == QuickSortType.SEQ) {
            System.out.println("[SEQ START]");
            System.out.flush();
            SeqQuickSort seqQuickSort = new SeqQuickSort(array, 0, array.length - 1);
            seqQuickSort.sort();
        }

        if (quickSortType == QuickSortType.PAR) {
            System.out.println("[PAR START]");
            System.out.flush();
            ParQuickSort parQuickSort = new ParQuickSort(array, 0, array.length - 1);
            ForkJoinPool forkJoinPool = new ForkJoinPool(4);
            forkJoinPool.invoke(parQuickSort);
        }

        if (quickSortType == QuickSortType.PARV2) {
            System.out.println("[PARv2 START]");
            System.out.flush();
            ParQuickSortV2 parQuickSort = new ParQuickSortV2(array, 0, array.length - 1);
            ForkJoinPool forkJoinPool = new ForkJoinPool(4);
            forkJoinPool.invoke(parQuickSort);
        }
        //---------------------------
        Instant end = Instant.now();

        Duration deltaTime = Duration.between(start, end);
        System.out.println(deltaTime.toSeconds() + "s");
        System.out.println(deltaTime.toMillis() + "ms");
        System.out.println(deltaTime.toNanos() + "ns");

        if (quickSortType == QuickSortType.SEQ) {
            System.out.println("[SEQ END]");
        }
        if (quickSortType == QuickSortType.PAR) {
            System.out.println("[PAR END]");
        }
        if (quickSortType == QuickSortType.PARV2) {
            System.out.println("[PARv2 END]");
        }
        System.out.flush();
    }

    public static boolean isSorted(int[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[i - 1]) {
                return false;
            }
        }

        return true;
    }

    public static void printCompressed(int[] array) {
        if (array.length < 20) {
            for (int x : array) {
                System.out.print(x + " ");
            }

            if (isSorted(array)) {
                System.out.print("[SORTED]");
            } else {
                System.out.print("[NOT SORTED]");
            }

            System.out.println();

            return;
        }

        for (int i = 0; i < 10; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.print(" ... ");
        for (int i = 0; i < 10; i++) {
            System.out.print(array[array.length - 10 + i] + " ");
        }

        if (isSorted(array)) {
            System.out.print("[SORTED]");
        } else {
            System.out.print("[NOT SORTED]");
        }

        System.out.println();
    }

    public static void main(String... args) {
        int initialSize = (int) 1e7;
        System.out.println("Measuring 1e7");
        System.out.println("---------------");

        for (QuickSortType type : QuickSortType.values()) {
            System.out.println("[[[" + type + "]]]");

            for (int i = 0; i < 5; i++) {
                int[] array = generateSeqArray(initialSize);
                runSample(array, type);
            }
            System.out.println("===============");
        }

        initialSize = (int) 1e8;
        System.out.println("Measuring 1e8");
        System.out.println("---------------");

        for (QuickSortType type : QuickSortType.values()) {
            if (type == QuickSortType.PAR)
                continue;

            System.out.println("[[[" + type + "]]]");

            for (int i = 0; i < 5; i++) {
                int[] array = generateSeqArray(initialSize);
                runSample(array, type);
            }
            System.out.println("===============");
        }
    }
}
