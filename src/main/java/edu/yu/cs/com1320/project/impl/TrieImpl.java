package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;
import java.util.*;

public class TrieImpl<Value> implements Trie<Value> {
    private final int ALPHABET_SIZE = 62;
    private Node<Value> root;
    private Set<Value> recentlyDeleted;
    private Set<Value> foundValues;

    private class Node<Value>{
        private Set<Value> valueSet;
        private Node<Value>[] links;

        private Node(){
            this.valueSet = new HashSet<>();
            this.links = new Node[ALPHABET_SIZE];
        }
    }
    public TrieImpl(){
        this.root = new Node<>();
        this.recentlyDeleted = new HashSet<>();
        this.foundValues = new HashSet<>();
    }

    @Override
    public void put(String key, Value val) {
            if (val != null){
                this.root = put(this.root, key, val, 0);
            }
    }

    @Override
    public List<Value> getSorted(String key, Comparator<Value> comparator) {
        if(key == null){
            throw new IllegalArgumentException("Blank Key");
        }
        if(comparator == null){
            throw new IllegalArgumentException("Blank Comparator");
        }
        if(!key.equals(key.replaceAll("[^0-9A-Za-z]", ""))){
            return new ArrayList<>();
        }
        List<Value> list = new ArrayList<>(this.get(key));
        list.sort(comparator);
        return list;
    }

    @Override
    public Set<Value> get(String key) {
        if(key == null){
            throw new IllegalArgumentException("Blank Key");
        }
        if(!key.equals(key.replaceAll("[^0-9A-Za-z]", ""))){
            return new HashSet<>();
        }
        Node<Value> x = this.get(this.root, key, 0);
        if (x == null){
            return new HashSet<>();
        }
        return x.valueSet;
    }

    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator){
        if(prefix == null){
            throw new IllegalArgumentException("Blank Key");
        }
        if(comparator == null){
            throw new IllegalArgumentException("Blank Comparator");
        }
        if(!prefix.equals(prefix.replaceAll("[^0-9A-Za-z]", ""))){
            return new ArrayList<>();
        }
        Node<Value> x = this.get(this.root, prefix, 0);
        if (x == null) {
            return new ArrayList<>();
        }
        this.findPrefix(this.root, prefix, 0);
        ArrayList<Value> list = new ArrayList<>(this.foundValues);
        this.foundValues = new HashSet<>();
        list.sort(comparator);

        return list;
    }

    @Override
    public Set<Value> deleteAllWithPrefix(String prefix) {
        if(prefix == null){
            throw new IllegalArgumentException("Blank Key");
        }
        if(!prefix.equals(prefix.replaceAll("[^0-9A-Za-z]", ""))){
            return new HashSet<>();
        }
        this.root = this.deletePrefix(this.root, prefix, 0);
        Set<Value> deleted = this.recentlyDeleted;
        this.recentlyDeleted = new HashSet<>();
        return deleted;
    }

    @Override
    public Set<Value> deleteAll(String key) {
        if(key == null){
            throw new IllegalArgumentException("Blank Key");
        }
        if(!key.equals(key.replaceAll("[^0-9A-Za-z]", ""))){
            return new HashSet<>();
        }
        this.root = deleteAll(this.root, key, 0);
        Set<Value> deleted = this.recentlyDeleted;
        this.recentlyDeleted = new HashSet<>();
        return deleted;
    }

    @Override
    public Value delete(String key, Value val) {
        if(key == null){
            throw new IllegalArgumentException("Blank Key");
        }
        if(!key.equals(key.replaceAll("[^0-9A-Za-z]", ""))){
            return null;
        }
        this.root = this.delete(this.root, key, 0, val);
        for(Value value : this.recentlyDeleted){
            this.recentlyDeleted = new HashSet<>();
            return value;
            }
        return null;
    }
    private Node<Value> put(Node<Value> x, String key, Value val, int d){
        //create a new node
        if (x == null){
            x = new Node<Value>();
        }
        key = key.replaceAll("[^0-9A-Za-z]", "").trim();
        //we've reached the last node in the key,
        //set the value for the key and return the node
        if (d == key.length()){
            x.valueSet.add(val);
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        int index = convert(c);
        x.links[index] = this.put(x.links[index], key, val, d + 1);
        return x;
    }
    private Node<Value> get(Node<Value> x, String key, int d){
        //link was null - return null, indicating a miss
        if (x == null){
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length()){
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        int index = convert(c);
        return this.get(x.links[index], key, d + 1);
    }
    private Node<Value> findPrefix(Node<Value> x, String prefix, int d){
        if (x == null){
            return null;
        }
        //we're at the node to del - set the val to null
        if (d >= prefix.length()){
            this.foundValues.addAll(x.valueSet);

            for (int index = 0; index < ALPHABET_SIZE; index++) {
                if (x.links[index] != null) {
                    x.links[index] = this.findPrefix(x.links[index], prefix, d + 1); //not empty
                }
            }
        }
        //continue down the trie to the target node
        else{
            char c = prefix.charAt(d);
            int index = convert(c);
            x.links[index] = this.findPrefix(x.links[index], prefix, d + 1);
        }
        return x;
    }
    private Node<Value> deletePrefix(Node<Value> x, String prefix, int d){
        if (x == null){
            return null;
        }
        //we're at the node to del - set the val to null
        if (d >= prefix.length()){
            this.recentlyDeleted.addAll(x.valueSet);
            x.valueSet = new HashSet<>();

            for (int index = 0; index < ALPHABET_SIZE; index++) {
                if (x.links[index] != null) {
                    x.links[index] = this.deletePrefix(x.links[index], prefix, d + 1); //not empty
                }
            }
        }
        //continue down the trie to the target node
        else{
            char c = prefix.charAt(d);
            int index = convert(c);
            x.links[index] = this.deletePrefix(x.links[index], prefix, d + 1);
        }
        //this node has a val – do nothing, return the node
        if (x.valueSet != null){
            return x;
        }
        //remove sub-trie rooted at x if it is completely empty
        for (int index = 0; index < ALPHABET_SIZE; index++) {
            if (x.links[index] != null) {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }
    private Node<Value> delete(Node<Value> x, String key, int d, Value val){
        if (x == null){
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == key.length()){
            x.valueSet.remove(val);
            this.recentlyDeleted.add(val);
        }
        //continue down the trie to the target node
        else{
            char c = key.charAt(d);
            int index = convert(c);
            x.links[index] = this.delete(x.links[index], key, d + 1, val);
        }
        //this node has a val – do nothing, return the node
        if (x.valueSet != null){
            return x;
        }
        //remove sub-trie rooted at x if it is completely empty
        for (int index = 0; index < ALPHABET_SIZE; index++) {
            if (x.links[index] != null) {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }
    private Node<Value> deleteAll(Node<Value> x, String key, int d){
        if (x == null){
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == key.length()){
            this.recentlyDeleted = x.valueSet;
            x.valueSet = new HashSet<>();
        }
        //continue down the trie to the target node
        else{
            char c = key.charAt(d);
            int index = convert(c);
            x.links[index] = this.deleteAll(x.links[index], key, d + 1);
        }
        //this node has a val – do nothing, return the node
        if (x.valueSet != null){
            return x;
        }
        //remove sub-trie rooted at x if it is completely empty
        for (int index = 0; index < ALPHABET_SIZE; index++) {
            if (x.links[index] != null) {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }
    private int convert(Character ch){
        if(Character.isDigit(ch)){
            return ch - 48;
        }
        else if(Character.isUpperCase(ch)){
            return ch - 55;
        }
        else if(Character.isLowerCase(ch)){
            return ch - 61;
        }
        else return -1;
    }


}
