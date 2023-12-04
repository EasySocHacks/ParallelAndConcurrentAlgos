import java.sql.Timestamp;
import java.time.Instant;
import java.util.Random;

public class SeqQuickSort {
    private int[] array;
    private final int begin;
    private final int end;
    private Random random = new Random(Timestamp.from(Instant.now()).getTime());

    public SeqQuickSort(int[] array, int begin, int end) {
        this.array = array;
        this.begin = begin;
        this.end = end;
    }

    private void sort(int begin, int end) {
        if (end <= begin)
            return;

        int l = begin;
        int r = end;
        int m = Math.abs(this.random.nextInt()) % (end - begin + 1) + begin;

        while (l < r) {
            while (l < m && this.array[l] <= this.array[m]) {
                l++;
            }

            while (r > m && this.array[r] >= this.array[m]) {
                r--;
            }

            int tmp = array[l];
            array[l] = array[r];
            array[r] = tmp;

            if (l == m) {
                m = r;
            } else if (r == m) {
                m = l;
            }
        }

        sort(begin, m);
        sort(m + 1, end);
    }

    public void sort() {
        sort(this.begin, this.end);
    }
}
