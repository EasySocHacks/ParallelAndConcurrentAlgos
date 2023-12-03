import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Main {
    private static final int INITIAL_SIZE = (int) 10000;

    public static ArrayList<Integer> generateRandomList(int initSize) {
        Random random = new Random(Timestamp.from(Instant.now()).getTime());

        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < initSize; i++) {
            list.add(random.nextInt());
        }

        return list;
    }

    public static ArrayList<Integer> generateSeqList(int initSize) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < initSize; i++) {
            list.add(i);
        }
        Collections.shuffle(list);

        return list;
    }

    public static void runSample(QuickSort<Integer> quickSort) {
        Instant start = Instant.now();
        //---------------------------
        quickSort.sort();
        //---------------------------
        Instant end = Instant.now();

        if (quickSort instanceof SeqQuickSort) {
            System.out.println("[SEQ QUICKSORT]");
        }
        if (quickSort instanceof ParQuickSort) {
            System.out.println("[PAR QUICKSORT]");
        }
        Duration deltaTime = Duration.between(start, end);
        System.out.println(deltaTime.toSeconds() + "s");
        System.out.println(deltaTime.toMillis() + "ms");
        System.out.println(deltaTime.toNanos() + "ns");
    }

    public static boolean isSorted(ArrayList<Integer> list) {
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) < list.get(i - 1)) {
                return false;
            }
        }

        return true;
    }

    public static void printCompressed(ArrayList<Integer> list) {
        if (list.size() < 20) {
            System.out.print(list + " ");

            if (isSorted(list)) {
                System.out.print("[SORTED]");
            } else {
                System.out.print("[NOT SORTED]");
            }

            System.out.println();

            return;
        }

        for (int i = 0; i < 10; i++) {
            System.out.print(list.get(i) + " ");
        }
        System.out.print(" ... ");
        for (int i = 0; i < 10; i++) {
            System.out.print(list.get(list.size() - 10 +  i) + " ");
        }

        if (isSorted(list)) {
            System.out.print("[SORTED]");
        } else {
            System.out.print("[NOT SORTED]");
        }

        System.out.println();
    }

    public static void main(String... args) {
        ArrayList<Integer> list = generateSeqList(INITIAL_SIZE);
        SeqQuickSort seqQuickSort = new SeqQuickSort(list);
        ParQuickSort parQuickSort = new ParQuickSort(list);

        printCompressed(list);
        runSample(parQuickSort);
        printCompressed(parQuickSort.getList());
        runSample(seqQuickSort);
        printCompressed(seqQuickSort.getList());
    }
}
