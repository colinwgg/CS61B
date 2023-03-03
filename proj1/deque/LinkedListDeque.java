package deque;

public class LinkedListDeque<T> implements Deque<T> {
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
        size -= 1;
        return tmp.item;
    }

    @Override
    public T removeLast() {
        Node tmp = sentinel.prev;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size -= 1;
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
        Node tmp = sentinel;
        for (int i = 0; i < index; i++) {
            tmp = tmp.next;
        }
        return tmp.item;
    }

    public T getRecursive(int index) {
            return getRecursivehelper(sentinel, index);
    }

    public T getRecursivehelper(Node n, int index) {
        if (index == 0) {
            return n.item;
        }
        else {
            return getRecursivehelper(n.next, index - 1);
        }
    }

    public boolean equeals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque oo = (Deque) o;
        if (size != oo.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (get(i).equals(oo.get(i))) {
                return false;
            }
        }
        return true;
    }
}
