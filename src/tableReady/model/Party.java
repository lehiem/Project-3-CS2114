package tableReady.model;

/**
 * One walk-in party: an unchangeable name and size, plus the rules for what
 * makes a valid name and size.
 *
 * Stretch goals (arrival time, missed-call mark, withMissedAt, and the
 * four-argument constructor) are intentionally not here yet.
 */
public class Party {

    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 12;
    private static final int MAX_NAME_LENGTH = 30;

    private final String name;
    private final int size;

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
        if (!isValidName(name)) {
            throw new IllegalArgumentException("invalid name");
        }
        if (!isValidSize(size)) {
            throw new IllegalArgumentException("invalid size");
        }
        this.name = name.trim();
        this.size = size;
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
