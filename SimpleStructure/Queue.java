package SimpleStructure;

import java.util.ArrayList;

/**
 * @author CAgAG
 * @version 1.0
 * @date 2021/6/26
 */
public class Queue {

    private int front;
    private int tail;
    private int capSize;
    private int curSize;
    private ArrayList<Object> queue;

    public Queue(int length) {
        this.capSize = length;
        this.curSize = 0;
        this.front = 0;
        this.tail = 0;
        queue = new ArrayList<>();
    }

    public int size() {
        return curSize;
    }

    public Object front() {
        return queue.get(front);
    }

    public Object tail() {
        return queue.get(tail);
    }

    public boolean isEmpty() {
        return curSize == 0;
    }

    public boolean isFull() {
        return curSize == capSize;
    }

    public void EnQueue(Object o) {
        if (isFull()) {
            return;
        }
        if (tail >= queue.size()) {
            queue.add(o);
        } else {
            queue.set(tail, o);
        }
        curSize++;
        tail = (tail + 1) % capSize;
    }

    public Object DeQueue() {
        if (isEmpty()) {
            return null;
        }
        Object de_data = queue.get(front);
        front = (front + 1) % capSize;
        curSize--;
        return de_data;
    }

    public void show() {
        for (int i = front, j = 0; j < curSize; j++, i = (i + 1) % capSize) {
            System.out.print(queue.get(i).toString() + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Queue queue = new Queue(10);

        System.out.println("is empty: " + queue.isEmpty());
        for (int i = 0; i < 12; i++) {
            queue.EnQueue(i);
        }
        System.out.println("is full: " + queue.isFull());

        queue.show();

        Object o = queue.DeQueue();
        System.out.println("del queue ele: " + o.toString());
        queue.show();
        System.out.println("is full: " + queue.isFull());

        queue.EnQueue(100);
        queue.show();
        System.out.println("is full: " + queue.isFull());
    }

}
