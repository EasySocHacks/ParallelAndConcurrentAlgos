import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SeqQuickSort extends QuickSort<Integer> {
    private Random random = new Random(Timestamp.from(Instant.now()).getTime());

    public SeqQuickSort(ArrayList<Integer> list) {
        super(list);
    }

    private void sort(int begin, int end) {
        if (end <= begin)
            return;

        int l = begin;
        int r = end;
        int m = Math.abs(this.random.nextInt()) % (end - begin + 1) + begin;

        while (l < r) {
            while (l < m && this.list.get(l) <= this.list.get(m)) {
                l++;
            }

            while (r > m && this.list.get(r) >= this.list.get(m)) {
                r--;
            }

            Collections.swap(this.list, l, r);

            if (l == m) {
                m = r;
            } else if (r == m) {
                m = l;
            }
        }

        sort(begin, m);
        sort(m + 1, end);
    }

    @Override
    public void sort() {
        sort(0, this.list.size() - 1);
    }
}
