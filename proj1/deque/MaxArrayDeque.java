package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
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
        T Max_ = items[index];
        for (int i = 0; i < size; i++) {
            if (cmp.compare(items[index], Max_) > 0) {
                Max_ = items[index];
            }
            index = addlastnextindex(index);
        }
        return Max_;
    }

    public T max(Comparator<T> c) {
        if (size == 0) {
            return null;
        }
        int index = addlastnextindex(nextfirst);
        T Max_ = items[index];
        for (int i = 0; i < size; i++) {
            if (c.compare(items[index], Max_) > 0) {
                Max_ = items[index];
            }
            index = addlastnextindex(index);
        }
        return Max_;
    }
}
