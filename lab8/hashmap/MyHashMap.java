package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Colin Wang
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int size = 0;
    private static final int DEFAULT_INITIALSIZE = 16;
    private static final double DEFAULT_LOADFACTOR = 0.75;
    private final double maxloadfactor;


    /** Constructors */
    public MyHashMap() { 
        this(DEFAULT_INITIALSIZE, DEFAULT_LOADFACTOR);
    }

    public MyHashMap(int initialSize) { 
        this(initialSize, DEFAULT_LOADFACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        maxloadfactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i += 1) {
            table[i] = createBucket();
        }
        return table;
    }
    
    public void clear() {
        size = 0;
        buckets = createTable(DEFAULT_INITIALSIZE);
    }

    private int getIndex(K key) {
        return getIndex(key, buckets);
    }

    private int getIndex(K key, Collection<Node>[] table) {
        int hashcode = key.hashCode();
        return Math.floorMod(hashcode, table.length);
    }

    private Node getNode(K key) {
        int index = getIndex(key);
        return getNode(key, index);
    }

    private Node getNode(K key, int index) {
        for (Node node: buckets[index]) {
            if (node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        return getNode(key) != null;
    }
    
    public V get(K key) {
        Node node = getNode(key);
        if (node == null) {
            return null;
        }
        return node.value; 
    }

    public int size() {
        return size;
    }

    public void put(K key, V value) {
        int index = getIndex(key);
        Node node = getNode(key, index);
        if (node != null) {
            node.value = value;
            return;
        }
        node = createNode(key, value);
        buckets[index].add(node);
        size += 1;
        if (reachMaxLoad()) {
            resize(buckets.length * 2);
        }
    }

    private boolean reachMaxLoad() {
        return (double) (size / buckets.length) > maxloadfactor;
    }

    private void resize(int newsize) {
        Collection<Node>[] newBuckets = createTable(newsize);
        Iterator<Node> nodeIterator = new MyHashMapNodeIterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            int index = getIndex(node.key, newBuckets);
            newBuckets[index].add(node);
        }
        buckets = newBuckets;
    }

    public Set<K> keySet() {
        HashSet<K> set = new HashSet<>();
        for (K key : this) {
            set.add(key);
        }
        return set;
    }

    public V remove(K key) {
        int index = getIndex(key);
        Node node = getNode(key, index);
        if (node == null) {
            return null;
        }
        size -= 1;
        buckets[index].remove(node);
        return node.value;
    }

    public V remove(K key, V value) {
        int index = getIndex(key);
        Node node = getNode(key, index);
        if (node == null || !node.value.equals(value)) {
            return null;
        }
        size -= 1;
        buckets[index].remove(node);
        return node.value;
    }

    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }

    private class MyHashMapIterator implements Iterator<K> {
        private final Iterator<Node> nodeIterator = new MyHashMapNodeIterator();

        public boolean hasNext() {
            return nodeIterator.hasNext();
        }

        public K next() {
            return nodeIterator.next().key;
        }
    }

    private class MyHashMapNodeIterator implements Iterator<Node> {
        private final Iterator<Collection<Node>> bucketsIterator = Arrays.stream(buckets).iterator();
        private Iterator<Node> currentBucketIterator;
        private int leftNode = size;

        public boolean hasNext() {
            return leftNode > 0;
        }

        public Node next() {
            if (currentBucketIterator == null || !currentBucketIterator.hasNext()) {
                Collection<Node> currentBucket = bucketsIterator.next();
                while (currentBucket.size() == 0) {
                    currentBucket = bucketsIterator.next();
                }
                currentBucketIterator = currentBucket.iterator();
            }
            leftNode -= 1;
            return currentBucketIterator.next();
        }
    }
}