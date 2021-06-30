package SimpleStructure;

import java.util.ArrayList;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/26
 */
public class Stack {
    private int capSize;
    private int curSize;
    private ArrayList<Object> stack;

    public Stack(int length) {
        this.capSize = length;
        this.curSize = 0;
        this.stack = new ArrayList<>();
    }

    public int size() {
        return curSize;
    }

    public boolean isFull() {
        return capSize == curSize;
    }

    public boolean isEmpty() {
        return curSize == 0;
    }

    public void push(Object o) {
        if (isFull()) {
            return;
        }
        if (stack.size() <= curSize) {
            stack.add(o);
        } else {
            stack.set(curSize, o);
        }
        curSize++;
    }

    public Object pop() {
        if (isEmpty()) {
            return null;
        }
        Object popData = stack.get(curSize - 1);
        curSize--;
        return popData;
    }

    public Object top() {
        if (isEmpty()) {
            return null;
        }
        return stack.get(curSize - 1);
    }

    public void show() {
        for (int i = 0; i < curSize; i++) {
            System.out.print(stack.get(i).toString() + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Stack stack = new Stack(10);
        System.out.println("isEmpty: " + stack.isEmpty());
        for (int i = 0; i < 12; i++) {
            stack.push(i);
        }
        stack.show();
        System.out.println("isFull: " + stack.isFull());

        Object o = stack.pop();
        System.out.println("pop ele: " + o.toString());
        stack.show();
        System.out.println("top ele: " + stack.top().toString());
    }
}
