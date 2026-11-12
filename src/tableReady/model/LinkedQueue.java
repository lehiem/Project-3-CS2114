package tableReady.model;
 
import java.util.NoSuchElementException;
 
public class LinkedQueue<T> {
 
    private static class Node<T> {
        private final T data;
        private Node<T> next;
 
        private Node(T data) {
            this.data = data;
        }
    }
 
    private Node<T> front;
    private Node<T> back;
    private int size;
 
    public LinkedQueue() {
        // TODO: start empty (front and back null, size 0)
    }
 
    public void enqueue(T item) {
        // TODO: throw IllegalArgumentException if item is null.
        // Add at the back. If the queue was empty, front AND back both point
        // at the new node.
    }
 
    public T dequeue() {
        // TODO: throw NoSuchElementException if empty.
        // Remove and return the front item. If that empties the queue,
        // set back to null too.
        return null;
    }
 
    public T peek() {
        // TODO: throw NoSuchElementException if empty.
        // Return the front item without removing it.
        return null;
    }
 
    public boolean isEmpty() {
        // TODO: true when size is 0
        return true;
    }
 
    public int size() {
        // TODO: return size
        return 0;
    }
 
    public void clear() {
        // TODO: front and back become null, size becomes 0
    }
 
    public Object[] toArray() {
        // TODO: new array of the items, front to back
        return new Object[0];
    }
}
 