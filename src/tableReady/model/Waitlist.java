package tableReady.model;

import java.util.NoSuchElementException;
import java.util.function.LongSupplier;

public class Waitlist {

    private static final int MAX_PARTIES = 50;
    public static final int NO_SHOW_LIMIT_MINUTES = 15;
    private static final long MILLIS_PER_MINUTE = 60L * 1000L;

    private final LinkedQueue<Party> line;
    private final LongSupplier clock;
    private int seatedCount;

    public Waitlist() {
        this(System::currentTimeMillis);
    }

    public Waitlist(LongSupplier clock) {
        if (clock == null) {
            throw new IllegalArgumentException("Clock can't be null.");
        }
        this.clock = clock;
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

        int count = line.size();
        for (int i = 1; i <= count; i++) {
            Party party = line.dequeue();
            if (i == position) {
                // Keep the arrival time and miss mark so a changed party
                // isn't treated as a new arrival.
                line.enqueue(new Party(newName, newSize,
                    party.getArrivalTime(), party.getMissedAt()));
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

    public boolean markNoShow() {
        if (isEmpty()) {
            throw new NoSuchElementException("No one is waiting.");
        }
        Party front = line.dequeue();
        if (front.hasMissed()) {
            // Second miss: the party leaves the line.
            return false;
        }
        // First miss: back of the line, marked with the time of the miss.
        line.enqueue(front.withMissedAt(clock.getAsLong()));
        return true;
    }

    public int removeExpiredNoShows() {
        long now = clock.getAsLong();
        long limit = NO_SHOW_LIMIT_MINUTES * MILLIS_PER_MINUTE;
        int count = line.size();
        int removed = 0;
        for (int i = 0; i < count; i++) {
            Party party = line.dequeue();
            if (party.hasMissed() && now - party.getMissedAt() >= limit) {
                removed++;
            }
            else {
                line.enqueue(party);
            }
        }
        return removed;
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