package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;
import java.util.Arrays;

public class StackImpl<T> implements Stack <T> {
    private T[] data;
    private int top;

    public StackImpl(){
        this.data = (T []) new Object[5];
        this.top = -1;

    }
    @Override
    public void push(T element) {
        if(element == null){
            throw new IllegalArgumentException();
        }
        if(this.top == this.data.length -1){
            this.data = Arrays.copyOf(this.data, this.data.length * 2);
        }
        this.top++;
        this.data[top] = element;
    }

    @Override
    public T pop() {
        if(this.top == -1){
            return null;
        }
        T item = this.data[this.top];
        this.data[this.top] = null;
        this.top--;
        return item;
    }

    @Override
    public T peek() {
        if(this.top == -1){
            return null;
        }
        return this.data[this.top];
    }

    @Override
    public int size() {
        return this.top+1;
    }
}
