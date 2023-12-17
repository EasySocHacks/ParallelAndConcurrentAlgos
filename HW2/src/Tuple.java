public class Tuple<T> {
    private T[] args;

    public Tuple(T... args) {
        this.args = args;
    }

    public T get(int index) {
        return args[index];
    }

    public T[] getAll() {
        return args;
    }

    public int getSize() {
        return args.length;
    }
}
