import java.util.ArrayList;

public abstract class QuickSort<T> {
    protected ArrayList<T> list;

    public QuickSort(ArrayList<T> list) {
        this.list = list;
    }

    public ArrayList<T> getList() {
        return this.list;
    }

    public abstract void sort();
}
