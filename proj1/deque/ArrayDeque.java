package deque;

public class ArrayDeque<T> implements Deque<T>{
    protected T[] items;
    protected int size;
    protected int nextfirst;
    protected int nextlast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextfirst = 0;
        nextlast = 1;
    }

    protected int addfirstnextindex(int index) {
        return (index + items.length - 1) % items.length;
    }

    protected int addlastnextindex(int index) {
        return (index + 1) % items.length;
    }

    public void resize(int newsize) {
        T[] tmp = (T[]) new Object[newsize];
        int index = addlastnextindex(nextfirst);
        for (int i = 0; i < size; i++) {
            tmp[i] = items[index];
            index = addlastnextindex(index);
        }
        nextfirst = newsize - 1;
        nextlast = size;
        items = tmp;
    }

    protected void check_more() {
        if (size == items.length) {
            resize(size * 2);
        }
    }

    protected void check_less() {
        if (items.length >= 16 && size <items.length / 4) {
            resize(items.length / 4);
        }
    }
    @Override
    public void addFirst(T item) {
        check_more();
        items[nextfirst] = item;
        nextfirst = addfirstnextindex(nextfirst);
        size += 1;
    }

    @Override
    public void addLast(T item) {
        check_less();
        items[nextlast] = item;
        nextlast = addlastnextindex(nextlast);
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        nextfirst = addlastnextindex(nextfirst);
        T tmp = items[nextfirst];
        items[nextfirst] = null;
        size -= 1;
        check_less();
        return tmp;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        nextlast = addfirstnextindex(nextlast);
        T tmp = items[nextlast];
        items[nextlast] = null;
        size -= 1;
        check_less();
        return tmp;
    }

    @Override
    public void printDeque() {
        int index = addlastnextindex(nextfirst);
        for (int i = 0; i < size; i++) {
            System.out.print(items[index] + " ");
            index = addlastnextindex(index);
        }
        System.out.println();
    }

    @Override
    public T get(int index) {
        int index_ = addlastnextindex(nextfirst);
        for (int i = 0; i < index; i++) {
            index_ = addlastnextindex(index_);
        }
        return items[index_];
    }

    public boolean equeals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque oo = (Deque) o;
        if (size != oo.size()) {
            return false;
        }
        int index = addlastnextindex(nextfirst);
        for (int i = 0; i < size; i++) {
            if (!items[index].equals(oo.get(i))) {
                return false;
            }
            index = addlastnextindex(index);
        }
        return true;
    }
}
