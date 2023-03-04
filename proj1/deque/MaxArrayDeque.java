package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{
    Comparator cmp;
    public MaxArrayDeque(Comparator<T> c) {
        cmp = c;
        items = (T[]) new Object[8];
        size = 0;
        nextfirst = 0;
        nextlast = 1;
    }

    public T max() {
        if (size == 0) {
            return null;
        }
        int index = addlastnextindex(nextfirst);
        T max_ = items[index];
        for (int i = 1; i < size; i++) {
            index = addlastnextindex(nextfirst);
            if (cmp.compare(items[index], max_) > 0) {
                max_ = items[index];
            }
        }
        return max_;
    }

    public T max(Comparator<T> c) {
        if (size == 0) {
            return null;
        }
        int index = addlastnextindex(nextfirst);
        T max_ = items[index];
        for (int i = 1; i < size; i++) {
            index = addlastnextindex(nextfirst);
            if (c.compare(items[index], max_) > 0) {
                max_ = items[index];
            }
        }
        return max_;
    }
}
