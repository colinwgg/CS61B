package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private BSTNode root;
    private int size = 0;

    public void clear() {
        size = 0;
        root = null;
    }

    public boolean containsKey(K key) {
        return containsKey(root, key);
    }
    private boolean containsKey(BSTNode node, K key) {
        if (node == null) {
            return false;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return containsKey(node.left, key);
        }
        else if (cmp > 0) {
            return containsKey(node.right, key);
        }
        return true;
    }

    public V get(K key) {
        return get(root, key);
    }
    private V get(BSTNode node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return get(node.left, key);
        }
        else if (cmp > 0) {
            return get(node.right, key);
        }
        return node.val;
    }

    public int size() {
        return size;
    }

    public void put(K key, V value) {
        root = put(root, key, value);
        size += 1;
    }

    private BSTNode put(BSTNode node, K key, V value) {
        if (node == null) {
            return new BSTNode(key, value);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = put(node.left, key, value);
        } else if (cmp > 0) {
            node.right = put(node.right, key, value);
        } else {
            node.val = value;
        }
        return node;
    }

    public Set<K> keySet() {
        HashSet<K> set = new HashSet<>();
        addKeys(root, set);
        return set;
    }

    private void addKeys(BSTNode node, Set<K> set) {
        if (node == null) {
            return;
        }
        set.add(node.key);
        addKeys(node.left, set);
        addKeys(node.right, set);
    }

    public V remove(K key) {
        if (containsKey(key)) {
            V value = get(key);
            root = remove(root, key);
            size -= 1;
            return value;
        }
        return null;
    }

    private BSTNode remove(BSTNode node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = remove(node.left, key);
        } else if (cmp > 0) {
            node.right = remove(node.right, key);
        } else {
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            BSTNode originalNode = node;
            node = getMinChild(node.right);
            node.left = originalNode.left;
            node.right = remove(originalNode.right, node.key);
        }
        return node;
    }

    private BSTNode getMinChild(BSTNode node) {
        if (node.left == null) {
            return node;
        }
        return getMinChild(node.left);
    }
    
    public V remove(K key, V value) {
        if (containsKey(key)) {
            V targetValue = get(key);
            if (targetValue.equals(value)) {
                root = remove(root, key);
                size -= 1;
                return targetValue;
            }
        }
        return null;
    }

    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(BSTNode node) {
        if (node == null) {
            return;
        }
        printInOrder(node.left);
        System.out.println(node.key.toString() + " -> " + node.val.toString());
        printInOrder(node.right);
    }
    
    private class BSTNode {
        public final K key;
        public V val;
        public BSTNode left;
        public BSTNode right;

        BSTNode(K k, V v) {
            key = k;
            val = v;
        }
    }
}
