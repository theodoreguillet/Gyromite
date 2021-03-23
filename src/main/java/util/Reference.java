package util;

public class Reference<T> {
    private T value;
    public Reference(T val) {
        value = val;
    }
    public T get() {
        return value;
    }
    public void set(T val) {
        value = val;
    }
}
