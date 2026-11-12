package tableReady.model;
 
import java.util.NoSuchElementException;
 
public class Waitlist {
 
    private static final int MAX_PARTIES = 50;
 
    private final LinkedQueue<Party> line;
    private int seatedCount;
 
    public Waitlist() {
        line = new LinkedQueue<>();
        seatedCount = 0;
    }
 
    public boolean addParty(String name, int size) {
        if (!Party.isValidName(name) || !Party.isValidSize(size)) {
            return false;
        }
        if (positionOf(name) != -1 || isFull()) {
            return false;
        }
        line.enqueue(new Party(name, size));
        return true;
    }
 
    public boolean editParty(String currentName, String newName, int newSize) {
        int position = positionOf(currentName);
        if (position == -1) {
            return false;
        }
        if (!Party.isValidName(newName) || !Party.isValidSize(newSize)) {
            return false;
        }
        int newNamePosition = positionOf(newName);
        if (newNamePosition != -1 && newNamePosition != position) {
            return false;
        }
 
        Party replacement = new Party(newName, newSize);
        int count = line.size();
        for (int i = 1; i <= count; i++) {
            Party party = line.dequeue();
            if (i == position) {
                line.enqueue(replacement);
            }
            else {
                line.enqueue(party);
            }
        }
        return true;
    }
 
    public Party seatNext() {
        if (isEmpty()) {
            throw new NoSuchElementException("No one is waiting.");
        }
        Party party = line.dequeue();
        seatedCount++;
        return party;
    }
 
    public Party peekNext() {
        if (isEmpty()) {
            throw new NoSuchElementException("No one is waiting.");
        }
        return line.peek();
    }
 
    public boolean isEmpty() {
        return line.isEmpty();
    }
 
    public int size() {
        return line.size();
    }
 
    public boolean isFull() {
        return line.size() >= MAX_PARTIES;
    }
 
    public Party[] getParties() {
        Object[] items = line.toArray();
        Party[] parties = new Party[items.length];
        for (int i = 0; i < items.length; i++) {
            parties[i] = (Party) items[i];
        }
        return parties;
    }
 
    public int positionOf(String name) {
        if (name == null) {
            return -1;
        }
        String target = name.trim();
        Party[] parties = getParties();
        for (int i = 0; i < parties.length; i++) {
            if (parties[i].getName().equalsIgnoreCase(target)) {
                return i + 1;
            }
        }
        return -1;
    }
 
    public int getSeatedCount() {
        return seatedCount;
    }
 
    public void restartNight() {
        line.clear();
        seatedCount = 0;
    }
}
 