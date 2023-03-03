package deque;

public interface Deque<T> {
    void addFirst(T item);

    void addLast(T item);

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }

    T removeFirst();

    T removeLast();

    void printDeque();

    T get(int index);
}
