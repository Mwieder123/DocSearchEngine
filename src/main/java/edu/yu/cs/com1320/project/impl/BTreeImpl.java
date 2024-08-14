package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.PersistenceManager;
import edu.yu.cs.com1320.project.stage6.impl.DocumentPersistenceManager;

import java.io.IOException;

public class BTreeImpl<Key extends Comparable<Key>, Value> implements BTree<Key, Value> {
    private static final int MAX = 4;
    private Node root;
    private int height;
    private int n;
    private PersistenceManager<Key, Value> pm;
    public BTreeImpl(){
        this.root = new Node(0);
        this.height = 0;
        this.n = 0;
    }
    private static final class Node {
        private int entryCount; // number of entries
        private Entry[] entries = new Entry[BTreeImpl.MAX]; // the array of children
        // create a node with k entries
        private Node(int k) {
            this.entryCount = k;
        }
    }
    private static class Entry<Key, Value> {
        private Comparable<Key> key;
        private Value val;
        private Node child;

        public Entry(Comparable key, Value val, Node child) {
            this.key = key;
            this.val = val;
            this.child = child;
        }
    }
    @Override
    public Value get(Key k) {
        if (k == null) {
            throw new IllegalArgumentException("argument to get() is null");
        }
        Entry<Key, Value> thisEntry = this.get(this.root, k, this.height);
        if(thisEntry == null){
            return null;
        }
        Value val;
        if(thisEntry.val == null){
            try {
                val = this.pm.deserialize(k);
                this.put(k, val);
                this.pm.delete(k);
            } catch (IOException e) {
                return null;
            }
        }
        return thisEntry.val;
    }
    private Entry get(Node currentNode, Key key, int height) {
        Entry[] entries = currentNode.entries;
        if (height == 0) {
            for (int j = 0; j < currentNode.entryCount; j++) {
                if (key.equals(entries[j].key)) {
                    return entries[j];
                }
            }
            return null;
        }
        else {
            for (int j = 0; j < currentNode.entryCount; j++) {
                if (j + 1 == currentNode.entryCount || key.compareTo((Key) entries[j + 1].key) < 0) {
                    return this.get(entries[j].child, key, height - 1);
                }
            }
        }
        return null;
    }

    @Override
    public Value put(Key k, Value v) {
        Entry<Key, Value> alreadyThere = this.get(this.root, k, this.height);
        if(alreadyThere != null){
            if(alreadyThere.val == null){
                try{
                    this.pm.delete(k);
                }catch(IOException e){
                    throw new RuntimeException();
                }
        }
            Value oldValue = alreadyThere.val;
            alreadyThere.val = v;
            return oldValue;

        }
        Node newNode = this.put(this.root, k, v, this.height);
        this.n++;
        if (newNode == null){//no split of root, we’re done
            return null;
        }
        Node newRoot = new Node(2);
        newRoot.entries[0] = new Entry(this.root.entries[0].key, null, this.root);
        newRoot.entries[1] = new Entry(newNode.entries[0].key, null, newNode);
        this.root = newRoot;
        this.height++;

        return null;
    }
    private Node put(Node currentNode, Key key, Value val, int height) {
        int j;
        Entry<Key, Value> newEntry = new Entry<>(key, val, null);
        if (height == 0) {
            for (j = 0; j < currentNode.entryCount; j++) {
                if (key.compareTo((Key) currentNode.entries[j].key) < 0) {
                    break;
                }
            }
        }
        else {
            for (j = 0; j < currentNode.entryCount; j++) {
                if ((j + 1 == currentNode.entryCount) || key.compareTo((Key) currentNode.entries[j + 1].key) < 0) {
                    Node newNode = this.put(currentNode.entries[j++].child, key, val, height - 1);
                    if (newNode == null) {
                        return null;
                    }
                    newEntry.key = newNode.entries[0].key;
                    newEntry.val = null;
                    newEntry.child = newNode;
                    break;
                }
            }
        }
        for (int i = currentNode.entryCount; i > j; i--) {
            currentNode.entries[i] = currentNode.entries[i - 1];
        }
        currentNode.entries[j] = newEntry;
        currentNode.entryCount++;
        if (currentNode.entryCount < BTreeImpl.MAX) {
            return null;
        }
        else {
            return this.split(currentNode);
        }
    }
    private Node split(Node currentNode){
        Node newNode = new Node(BTreeImpl.MAX / 2);
        for (int j = 0; j < BTreeImpl.MAX / 2; j++){
            newNode.entries[j] = currentNode.entries[BTreeImpl.MAX / 2 + j];
            currentNode.entries[BTreeImpl.MAX / 2 + j] = null;
        }
        currentNode.entryCount = BTreeImpl.MAX / 2;
        return newNode;
    }

    @Override
    public void moveToDisk(Key k) throws IOException {
        if(this.pm == null){
            throw new IllegalStateException();
        }
        this.pm.serialize(k, this.get(k));
        this.put(k, null);
    }

    @Override
    public void setPersistenceManager(PersistenceManager<Key, Value> pm) {
        this.pm = pm;
    }
}
