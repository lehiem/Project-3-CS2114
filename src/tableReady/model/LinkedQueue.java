package tableReady.model;

import java.util.NoSuchElementException;

/**
 * A singly linked FIFO queue. Keeps references to both the front and the back
 * node so enqueue and dequeue are both O(1).
 *
 * Used by Waitlist as LinkedQueue&lt;Party&gt; (and, in the wait-quote stretch
 * goal, as LinkedQueue&lt;Long&gt;), which is why it is generic.
 *
 * @param <T>
 *            the type of item stored
 */
public class LinkedQueue<T> {

    // Private helper: one link in the chain.
    private static class Node<T> {
        private final T data; // the item stored
        private Node<T> next; // the node behind this one (null at the back)

        private Node(T data) {
            this.data = data;
        }
    }

    private Node<T> front; // first node; null when empty
    private Node<T> back; // last node; null when empty
    private int size; // number of items

    /**
     * Creates an empty queue.
     */
    public LinkedQueue() {
        front = null;
        back = null;
        size = 0;
    }

    /**
     * Adds item at the back in O(1).
     *
     * @param item
     *            the item to add
     * @throws IllegalArgumentException
     *             if item is null
     */
    public void enqueue(T item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null");
        }
        Node<T> node = new Node<>(item);
        if (back == null) {
            // Empty queue: the new node is both the front and the back.
            front = node;
        }
        else {
            back.next = node;
        }
        back = node;
        size++;
    }

    /**
     * Removes and returns the front item in O(1).
     *
     * @return the front item
     * @throws NoSuchElementException
     *             if the queue is empty
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }
        T item = front.data;
        front = front.next;
        if (front == null) {
            // That was the last item, so back must not point at a removed node.
            back = null;
        }
        size--;
        return item;
    }

    /**
     * Returns the front item without removing it.
     *
     * @return the front item
     * @throws NoSuchElementException
     *             if the queue is empty
     */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }
        return front.data;
    }

    /**
     * @return true when the queue holds no items
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * @return the number of items in the queue
     */
    public int size() {
        return size;
    }

    /**
     * Removes every item.
     */
    public void clear() {
        front = null;
        back = null;
        size = 0;
    }

    /**
     * Returns a new array of the items, front to back. Changing the array
     * doesn't change the queue.
     *
     * @return a new array of the items (empty, never null, if the queue is
     *         empty)
     */
    public Object[] toArray() {
        Object[] items = new Object[size];
        Node<T> current = front;
        for (int i = 0; i < size; i++) {
            items[i] = current.data;
            current = current.next;
        }
        return items;
    }
}
