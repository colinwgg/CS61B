package deque;

import java.util.Iterator;
public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    public class Node {
        private Node prev;
        private Node next;
        private T item;

        public Node(Node p, Node n, T i) {
            prev = p;
            next = n;
            item = i;
        }

        public Node() {
            item = null;
            prev = next = null;
        }
    }

    private int size;
    private Node sentinel;

    public LinkedListDeque() {
        size = 0;
        sentinel = new Node();
        sentinel.prev = sentinel.next = sentinel;
    }
    @Override
    public void addFirst(T item) {
        Node tmp = new Node(sentinel, sentinel.next, item);
        sentinel.next.prev = tmp;
        sentinel.next = tmp;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        Node tmp = new Node(sentinel.prev, sentinel, item);
        sentinel.prev.next = tmp;
        sentinel.prev = tmp;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T removeFirst() {
        Node tmp = sentinel.next;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size = (size == 0) ? size : size - 1;
        return tmp.item;
    }

    @Override
    public T removeLast() {
        Node tmp = sentinel.prev;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size = (size == 0) ? size : size - 1;
        return tmp.item;
    }

    @Override
    public void printDeque() {
        Node tmp = sentinel;
        for (int i = 0; i < size; i++) {
            tmp = tmp.next;
            System.out.print(tmp.item + " ");
        }
        System.out.println();
    }

    @Override
    public T get(int index) {
        Node p = sentinel.next;
        while (p != sentinel && index > 0) {
            p = p.next;
            index--;
        }
        return (index == 0) ? (T) p.item : null;
    }

    public T getRecursive(int index) {
        return getRecursivehelper(sentinel.next, index);
    }

    public T getRecursivehelper(Node n, int index) {
        if (n == sentinel) {
            return null;
        }
        if (index == 0) {
            return (T) n.item;
        }
        return getRecursivehelper(n.next, index - 1);
    }

    public boolean equeals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque oo = (Deque) o;
        if (size != oo.size()) {
            return false;
        }
        Node p = sentinel.next;
        for (int i = 0; i < size; i++) {
            if (!p.item.equals(oo.get(i))) {
                return false;
            }
            p = p.next;
        }
        return true;
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private Node ptr;
        LinkedListDequeIterator() {
            ptr = sentinel.next;
        }
        public boolean hasNext() {
            return (ptr != sentinel);
        }
        public T next() {
            T item = (T) ptr.item;
            ptr = ptr.next;
            return item;
        }
    }
}
