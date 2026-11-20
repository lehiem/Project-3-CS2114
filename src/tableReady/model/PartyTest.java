package tableReady.model;

/**
 * Tests for Party, following the Party test plan in the spec: each public
 * method gets a normal case and a bad-input / edge case.
 */
public class PartyTest extends student.TestCase {

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

    // Builds a name of exactly the given length.
    private String nameOfLength(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append('a');
        }
        return builder.toString();
    }

    // ----- Party(String, int) -----

    public void testConstructorStoresNameAndSize() {
        Party party = new Party("Patel", 4);
        assertEquals("Patel", party.getName());
        assertEquals(4, party.getSize());
    }

    public void testConstructorTrimsName() {
        Party party = new Party("  Patel  ", 4);
        assertEquals("Patel", party.getName());
    }

    public void testConstructorBlankNameAndZeroSizeThrows() {
        Exception thrown = thrownBy(() -> new Party("", 0));
        assertTrue(thrown instanceof IllegalArgumentException);
    }

    public void testConstructorBadNameThrows() {
        assertTrue(thrownBy(() -> new Party(null, 4)) instanceof IllegalArgumentException);
        assertTrue(thrownBy(() -> new Party("", 4)) instanceof IllegalArgumentException);
        assertTrue(thrownBy(() -> new Party("    ", 4)) instanceof IllegalArgumentException);
        assertTrue(thrownBy(() -> new Party(nameOfLength(31), 4)) instanceof IllegalArgumentException);
    }

    public void testConstructorBadSizeThrows() {
        assertTrue(thrownBy(() -> new Party("Patel", 0)) instanceof IllegalArgumentException);
        assertTrue(thrownBy(() -> new Party("Patel", -3)) instanceof IllegalArgumentException);
        assertTrue(thrownBy(() -> new Party("Patel", 13)) instanceof IllegalArgumentException);
    }

    public void testConstructorBoundaryValuesAreAllowed() {
        Party small = new Party("A", 1);
        Party big = new Party(nameOfLength(30), 12);
        assertEquals(1, small.getSize());
        assertEquals(12, big.getSize());
        assertEquals(30, big.getName().length());
    }

    // ----- getName -----

    public void testGetNameRemovesSurroundingSpaces() {
        Party party = new Party(" Patel ", 4);
        assertEquals("Patel", party.getName());
    }

    public void testGetNameKeepsInnerSpacesAndCase() {
        Party party = new Party("  Van Der Berg ", 2);
        assertEquals("Van Der Berg", party.getName());
    }

    public void testGetNameOneCharacter() {
        Party party = new Party("K", 2);
        assertEquals("K", party.getName());
    }

    // ----- getSize -----

    public void testGetSizeNormal() {
        Party party = new Party("Patel", 4);
        assertEquals(4, party.getSize());
    }

    public void testGetSizeMaximum() {
        Party party = new Party("Patel", 12);
        assertEquals(12, party.getSize());
    }

    public void testGetSizeMinimum() {
        Party party = new Party("Patel", 1);
        assertEquals(1, party.getSize());
    }

    // ----- isValidName -----

    public void testIsValidNameNormal() {
        assertTrue(Party.isValidName("Patel"));
    }

    public void testIsValidNameNull() {
        assertFalse(Party.isValidName(null));
    }

    public void testIsValidNameBlank() {
        assertFalse(Party.isValidName(""));
        assertFalse(Party.isValidName("   "));
    }

    public void testIsValidNameLengthBoundary() {
        assertTrue(Party.isValidName(nameOfLength(1)));
        assertTrue(Party.isValidName(nameOfLength(30)));
        assertFalse(Party.isValidName(nameOfLength(31)));
        assertFalse(Party.isValidName(nameOfLength(5000)));
    }

    public void testIsValidNameCountsLengthAfterTrimming() {
        // 30 real characters plus padding is still valid ...
        assertTrue(Party.isValidName("  " + nameOfLength(30) + "  "));
        // ... but 31 real characters is not, however it is padded.
        assertFalse(Party.isValidName("  " + nameOfLength(31) + "  "));
    }

    // ----- isValidSize -----

    public void testIsValidSizeNormal() {
        assertTrue(Party.isValidSize(4));
    }

    public void testIsValidSizeBoundaries() {
        assertTrue(Party.isValidSize(1));
        assertTrue(Party.isValidSize(12));
        assertFalse(Party.isValidSize(0));
        assertFalse(Party.isValidSize(13));
    }

    public void testIsValidSizeNegativeAndHuge() {
        assertFalse(Party.isValidSize(-1));
        assertFalse(Party.isValidSize(500));
        assertFalse(Party.isValidSize(Integer.MAX_VALUE));
        assertFalse(Party.isValidSize(Integer.MIN_VALUE));
    }

    // ----- hasMissed -----

    public void testHasMissedFalseForNewParty() {
        assertFalse(new Party("Patel", 4).hasMissed());
    }

    public void testHasMissedFalseWhenFourArgConstructorUsesNotMissed() {
        assertFalse(new Party("Patel", 4, 100L, Party.NOT_MISSED).hasMissed());
    }

    public void testHasMissedTrueAfterWithMissedAt() {
        assertTrue(new Party("Patel", 4).withMissedAt(5000).hasMissed());
    }

    // ----- withMissedAt -----

    public void testWithMissedAtReturnsMarkedCopy() {
        Party original = new Party("Patel", 4, 100L, Party.NOT_MISSED);
        Party marked = original.withMissedAt(5000);
        assertEquals("Patel", marked.getName());
        assertEquals(4, marked.getSize());
        assertEquals(5000L, marked.getMissedAt());
        assertEquals(100L, marked.getArrivalTime());
    }

    public void testWithMissedAtLeavesOriginalUnchanged() {
        Party original = new Party("Patel", 4);
        Party marked = original.withMissedAt(5000);
        assertNotSame(original, marked);
        assertFalse(original.hasMissed());
        assertEquals(Party.NOT_MISSED, original.getMissedAt());
    }

    public void testWithMissedAtCanBeCalledOnAlreadyMissedParty() {
        Party first = new Party("Patel", 4).withMissedAt(5000);
        Party second = first.withMissedAt(9000);
        assertEquals(9000L, second.getMissedAt());
        assertEquals(5000L, first.getMissedAt());
    }

    public void testWithMissedAtNegativeThrows() {
        Party party = new Party("Patel", 4);
        assertTrue(thrownBy(() -> party.withMissedAt(-1))
            instanceof IllegalArgumentException);
        assertTrue(thrownBy(() -> party.withMissedAt(-500))
            instanceof IllegalArgumentException);
        assertFalse(party.hasMissed());
    }

    // ----- getMissedAt -----

    public void testGetMissedAtDefaultsToNotMissed() {
        assertEquals(-1L, new Party("Patel", 4).getMissedAt());
    }

    public void testGetMissedAtReturnsValueFromConstructor() {
        assertEquals(5000L, new Party("Patel", 4, 100L, 5000L).getMissedAt());
    }

    public void testGetMissedAtAfterWithMissedAtZero() {
        // 0 is a real time, so it must count as a miss.
        Party party = new Party("Patel", 4).withMissedAt(0);
        assertTrue(party.hasMissed());
        assertEquals(0L, party.getMissedAt());
    }

    // ----- getArrivalTime -----

    public void testGetArrivalTimeDefaultsToZero() {
        assertEquals(0L, new Party("Patel", 4).getArrivalTime());
    }

    // ----- Party(String, int, long, long) -----

    public void testFourArgConstructorStoresAllFields() {
        Party party = new Party("  Patel ", 4, 100L, 5000L);
        assertEquals("Patel", party.getName());
        assertEquals(4, party.getSize());
        assertEquals(100L, party.getArrivalTime());
        assertEquals(5000L, party.getMissedAt());
        assertTrue(party.hasMissed());
    }

    public void testFourArgConstructorBadNameThrows() {
        assertTrue(thrownBy(() -> new Party(null, 4, 0L, -1L))
            instanceof IllegalArgumentException);
        assertTrue(thrownBy(() -> new Party("", 4, 0L, -1L))
            instanceof IllegalArgumentException);
        assertTrue(thrownBy(() -> new Party("   ", 4, 0L, -1L))
            instanceof IllegalArgumentException);
        assertTrue(thrownBy(() -> new Party(nameOfLength(31), 4, 0L, -1L))
            instanceof IllegalArgumentException);
    }

    public void testFourArgConstructorBadSizeThrows() {
        assertTrue(thrownBy(() -> new Party("Patel", 0, 0L, -1L))
            instanceof IllegalArgumentException);
        assertTrue(thrownBy(() -> new Party("Patel", -3, 0L, -1L))
            instanceof IllegalArgumentException);
        assertTrue(thrownBy(() -> new Party("Patel", 13, 0L, -1L))
            instanceof IllegalArgumentException);
    }

    public void testFourArgConstructorBadTimesThrow() {
        assertTrue(thrownBy(() -> new Party("Patel", 4, -1L, -1L))
            instanceof IllegalArgumentException);
        assertTrue(thrownBy(() -> new Party("Patel", 4, 0L, -2L))
            instanceof IllegalArgumentException);
    }

    public void testFourArgConstructorBoundaryValuesAreAllowed() {
        Party party = new Party(nameOfLength(30), 12, 0L, 0L);
        assertEquals(12, party.getSize());
        assertEquals(0L, party.getMissedAt());
        assertTrue(party.hasMissed());
    }
}
