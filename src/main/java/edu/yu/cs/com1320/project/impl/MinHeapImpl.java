package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {
    public MinHeapImpl(){
        this.elements = (E[]) new Comparable[25];
    }
    @Override
    public void reHeapify(E element) {
        if(element == null){
            throw new IllegalArgumentException();
        }
        //Find the index - method getArrayIndex will throw exception if the element does not exist
        int index = getArrayIndex(element);
        if(index == 1 || this.isGreater(index, index / 2)){
            downHeap(index);
        }
        else upHeap(index);
    }

    @Override
    protected int getArrayIndex(E element) {
        if(element == null){
            throw new IllegalArgumentException();
        }
        //Check if Array is empty or if element is less than root
        if(this.isEmpty()){
            throw new NoSuchElementException();
        }
        int index = this.findArrayIndex(1, element);
        if(index == -1){
            throw new NoSuchElementException();
        }
        return index;
    }

    @Override
    protected void doubleArraySize() {
        this.elements = Arrays.copyOf(this.elements, this.elements.length * 2);
    }

    private int findArrayIndex(int index, E element){
        if(index > this.count){
            return -1;
        }
        if(element.equals(this.elements[index])){
            return index;
        }
        int leftIndex = index * 2;
        int rightIndex = leftIndex + 1;

        if(leftIndex <= this.count && this.elements[leftIndex] != null && element.compareTo(this.elements[leftIndex]) >= 0){
            int leftFoundIndex = findArrayIndex(leftIndex, element);
            if(leftFoundIndex != -1) return leftFoundIndex;
        }
        if(rightIndex <= this.count && this.elements[rightIndex] != null && element.compareTo(this.elements[rightIndex]) >= 0){
            int rightFoundIndex = findArrayIndex(rightIndex, element);
            if(rightFoundIndex != -1) return rightFoundIndex;
        }
        return -1;
    }
}
