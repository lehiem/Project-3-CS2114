package tableReady.model;

public class Party {

    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 12;
    private static final int MAX_NAME_LENGTH = 30;

    private final String name;
    private final int size;

    public Party(String name, int size) {
        // TODO: throw IllegalArgumentException if !isValidName(name) or
        // !isValidSize(size). Store the trimmed name.
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public static boolean isValidName(String name) {
        // TODO: true if name isn't null and is 1-30 characters after trimming
        return false;
    }

    public static boolean isValidSize(int size) {
        // TODO: true if size is from MIN_SIZE to MAX_SIZE (1-12)
        return false;
    }
}