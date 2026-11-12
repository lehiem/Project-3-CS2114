package tableReady.model;

import java.util.NoSuchElementException;

/**
 * Tests for LinkedQueue, following the LinkedQueue test plan in the spec:
 * each public method gets a normal case and a bad-input / edge case.
 */
public class LinkedQueueTest extends student.TestCase {

    private LinkedQueue<String> queue;

    public void setUp() {
        queue = new LinkedQueue<>();
    }

    // Runs the action and returns the exception it threw, or null if none.
    private Exception thrownBy(Runnable action) {
        try {
            action.run();
        }
        catch (Exception e) {
            return e;
        }
        return null;
    }

    // ----- LinkedQueue() -----

    public void testConstructorStartsEmpty() {
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    public void testConstructorQueuesAreIndependent() {
        LinkedQueue<String> other = new LinkedQueue<>();
        queue.enqueue("Patel");
        assertEquals(1, queue.size());
        assertTrue(other.isEmpty());
        assertEquals(0, other.size());
    }

    // ----- enqueue -----

    public void testEnqueueOneItem() {
        queue.enqueue("Patel");
        assertEquals(1, queue.size());
        assertEquals("Patel", queue.peek());
    }

    public void testEnqueueKeepsFrontAndAddsAtBack() {
        queue.enqueue("Patel");
        queue.enqueue("Kim");
        queue.enqueue("Lee");
        assertEquals(3, queue.size());
        assertEquals("Patel", queue.peek());
        Object[] items = queue.toArray();
        assertEquals("Lee", items[2]);
    }

    public void testEnqueueNullThrows() {
        Exception thrown = thrownBy(() -> queue.enqueue(null));
        assertTrue(thrown instanceof IllegalArgumentException);
    }

    public void testEnqueueNullLeavesQueueUnchanged() {
        queue.enqueue("Patel");
        thrownBy(() -> queue.enqueue(null));
        assertEquals(1, queue.size());
        assertEquals("Patel", queue.peek());
    }

    public void testEnqueueAfterQueueEmptiedAgain() {
        // Catches a stale back pointer after the last item is dequeued.
        queue.enqueue("Patel");
        queue.dequeue();
        queue.enqueue("Kim");
        queue.enqueue("Lee");
        assertEquals(2, queue.size());
        assertEquals("Kim", queue.dequeue());
        assertEquals("Lee", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    // ----- dequeue -----

    public void testDequeueReturnsFrontAndLeavesNext() {
        queue.enqueue("Patel");
        queue.enqueue("Kim");
        assertEquals("Patel", queue.dequeue());
        assertEquals(1, queue.size());
        assertEquals("Kim", queue.peek());
    }

    public void testDequeueIsFifoOrder() {
        queue.enqueue("Patel");
        queue.enqueue("Kim");
        queue.enqueue("Lee");
        assertEquals("Patel", queue.dequeue());
        assertEquals("Kim", queue.dequeue());
        assertEquals("Lee", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    public void testDequeueWhenEmptyThrows() {
        Exception thrown = thrownBy(() -> queue.dequeue());
        assertTrue(thrown instanceof NoSuchElementException);
    }

    public void testDequeueAfterDrainingThrows() {
        queue.enqueue("Patel");
        queue.dequeue();
        Exception thrown = thrownBy(() -> queue.dequeue());
        assertTrue(thrown instanceof NoSuchElementException);
        assertEquals(0, queue.size());
    }

    // ----- peek -----

    public void testPeekDoesNotRemove() {
        queue.enqueue("Patel");
        queue.enqueue("Kim");
        assertEquals("Patel", queue.peek());
        assertEquals(2, queue.size());
        assertEquals("Patel", queue.peek());
    }

    public void testPeekWhenEmptyThrows() {
        Exception thrown = thrownBy(() -> queue.peek());
        assertTrue(thrown instanceof NoSuchElementException);
    }

    // ----- isEmpty -----

    public void testIsEmptyOnNewQueue() {
        assertTrue(queue.isEmpty());
    }

    public void testIsEmptyAfterEnqueue() {
        queue.enqueue("Patel");
        assertFalse(queue.isEmpty());
    }

    public void testIsEmptyAfterDequeueingLastItem() {
        queue.enqueue("Patel");
        queue.dequeue();
        assertTrue(queue.isEmpty());
    }

    // ----- size -----

    public void testSizeAfterThreeEnqueues() {
        queue.enqueue("Patel");
        queue.enqueue("Kim");
        queue.enqueue("Lee");
        assertEquals(3, queue.size());
    }

    public void testSizeAfterDequeueingOnlyItem() {
        queue.enqueue("Patel");
        queue.dequeue();
        assertEquals(0, queue.size());
    }

    public void testSizeUnchangedByPeekAndToArray() {
        queue.enqueue("Patel");
        queue.peek();
        queue.toArray();
        assertEquals(1, queue.size());
    }

    // ----- clear -----

    public void testClearRemovesEverything() {
        queue.enqueue("Patel");
        queue.enqueue("Kim");
        queue.enqueue("Lee");
        queue.clear();
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    public void testClearWhenEmpty() {
        Exception thrown = thrownBy(() -> queue.clear());
        assertNull(thrown);
        assertEquals(0, queue.size());
    }

    public void testQueueWorksAfterClear() {
        queue.enqueue("Patel");
        queue.enqueue("Kim");
        queue.clear();
        queue.enqueue("Lee");
        assertEquals(1, queue.size());
        assertEquals("Lee", queue.peek());
        assertEquals("Lee", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    // ----- toArray -----

    public void testToArrayInFifoOrder() {
        queue.enqueue("Patel");
        queue.enqueue("Kim");
        Object[] items = queue.toArray();
        assertEquals(2, items.length);
        assertEquals("Patel", items[0]);
        assertEquals("Kim", items[1]);
    }

    public void testToArrayWhenEmptyIsEmptyNotNull() {
        Object[] items = queue.toArray();
        assertNotNull(items);
        assertEquals(0, items.length);
    }

    public void testToArrayReturnsCopy() {
        queue.enqueue("Patel");
        queue.enqueue("Kim");
        Object[] items = queue.toArray();
        items[0] = "Changed";
        items[1] = null;
        assertEquals(2, queue.size());
        assertEquals("Patel", queue.peek());
        assertEquals("Patel", queue.toArray()[0]);
        assertEquals("Kim", queue.toArray()[1]);
    }

    public void testToArrayDoesNotRemoveItems() {
        queue.enqueue("Patel");
        queue.toArray();
        assertEquals("Patel", queue.dequeue());
    }

    // ----- generic reuse (stretch goal stores wait times as Long) -----

    public void testWorksWithLong() {
        LinkedQueue<Long> waits = new LinkedQueue<>();
        waits.enqueue(10L);
        waits.enqueue(20L);
        assertEquals(Long.valueOf(10L), waits.dequeue());
        assertEquals(Long.valueOf(20L), waits.peek());
        assertEquals(1, waits.size());
    }
}
