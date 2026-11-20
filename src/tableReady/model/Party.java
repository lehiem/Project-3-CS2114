package tableReady.model;

/**
 * One walk-in party: an unchangeable name and size, plus the rules for what
 * makes a valid name and size.
 *
 * Also tracks arrival time and a missed-call mark for the no-show feature.
 */
public class Party {

    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 12;
    private static final int MAX_NAME_LENGTH = 30;
    /** Value of missedAt for a party that has not missed a call. */
    public static final long NOT_MISSED = -1;

    private final String name;
    private final int size;
    private final long arrivalTime; // when the party joined the line
    private final long missedAt; // time of missed call, or NOT_MISSED

    /**
     * Creates a party with the trimmed name.
     *
     * @param name
     *            the party's name (1-30 characters after trimming)
     * @param size
     *            the party's size (1-12)
     * @throws IllegalArgumentException
     *             if the name or size is invalid
     */
    public Party(String name, int size) {
        this(name, size, 0, NOT_MISSED);
    }

    // ----- No-show support (replaces the old stub block) -----

    /**
     * Creates a party with an arrival time and a missed-call mark.
     *
     * @param name
     *            the party's name (1-30 characters after trimming)
     * @param size
     *            the party's size (1-12)
     * @param arrivalTime
     *            when the party joined the line
     * @param missedAt
     *            when the party missed its call, or NOT_MISSED (-1)
     * @throws IllegalArgumentException
     *             if the name or size is invalid, if arrivalTime is negative,
     *             or if missedAt is less than NOT_MISSED
     */
    public Party(String name, int size, long arrivalTime, long missedAt) {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("invalid name");
        }
        if (!isValidSize(size)) {
            throw new IllegalArgumentException("invalid size");
        }
        if (arrivalTime < 0) {
            throw new IllegalArgumentException("invalid arrival time");
        }
        if (missedAt < NOT_MISSED) {
            throw new IllegalArgumentException("invalid missed time");
        }
        this.name = name.trim();
        this.size = size;
        this.arrivalTime = arrivalTime;
        this.missedAt = missedAt;
    }

    /**
     * @return the time this party joined the line
     */
    public long getArrivalTime() {
        return arrivalTime;
    }

    /**
     * @return the time of this party's missed call, or NOT_MISSED (-1)
     */
    public long getMissedAt() {
        return missedAt;
    }

    /**
     * @return true if this party has missed a call
     */
    public boolean hasMissed() {
        return missedAt != NOT_MISSED;
    }

    /**
     * Parties are unchangeable, so this returns a marked copy.
     *
     * @param time
     *            the time of the missed call
     * @return a new Party with the same name, size and arrival time, marked
     *         as missed at time
     * @throws IllegalArgumentException
     *             if time is negative
     */
    public Party withMissedAt(long time) {
        if (time < 0) {
            throw new IllegalArgumentException("invalid missed time");
        }
        return new Party(name, size, arrivalTime, time);
    }

    /**
     * @return the trimmed name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the party size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param name
     *            the name to check
     * @return true if name isn't null and is 1-30 characters after trimming
     */
    public static boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        int length = name.trim().length();
        return length >= 1 && length <= MAX_NAME_LENGTH;
    }

    /**
     * @param size
     *            the size to check
     * @return true if size is from 1 to 12
     */
    public static boolean isValidSize(int size) {
        return size >= MIN_SIZE && size <= MAX_SIZE;
    }
}
