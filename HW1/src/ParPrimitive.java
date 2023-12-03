import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

public abstract class ParPrimitive<T, R> {
    protected final ArrayList<T> list;
    protected final ForkJoinPool forkJoinPool;

    public ParPrimitive(ArrayList<T> list, ForkJoinPool forkJoinPool) {
        this.list = list;
        this.forkJoinPool = forkJoinPool;
    }

    public ArrayList<T> getList() {
        return list;
    }

    public abstract R proceed();
}
