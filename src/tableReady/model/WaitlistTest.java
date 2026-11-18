package tableReady.model;

import java.util.NoSuchElementException;

public class WaitlistTest extends student.TestCase {

    private static final long START = 1000000L;
    private static final long ONE_MINUTE = 60L * 1000L;

    private Waitlist waitlist;
    private long[] now;
    private Waitlist timed;

    public void setUp() {
        waitlist = new Waitlist();
        now = new long[] {START};
        timed = new Waitlist(() -> now[0]);
    }

    // Moves the fake clock forward.
    private void advanceMinutes(int minutes) {
        now[0] += minutes * ONE_MINUTE;
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

    private void addPatelLeeKim() {
        waitlist.addParty("Patel", 4);
        waitlist.addParty("Lee", 2);
        waitlist.addParty("Kim", 3);
    }

    // ----- Waitlist() -----

    public void testConstructorStartsEmpty() {
        assertEquals(0, waitlist.size());
        assertEquals(0, waitlist.getSeatedCount());
    }

    public void testConstructorWaitlistsAreIndependent() {
        Waitlist other = new Waitlist();
        assertTrue(waitlist.addParty("Patel", 4));
        assertEquals(1, waitlist.size());
        assertEquals(0, other.size());
    }

    // ----- addParty -----

    public void testAddPartyAddsAtBack() {
        assertTrue(waitlist.addParty("Patel", 4));
        assertTrue(waitlist.addParty("Kim", 2));
        assertEquals(2, waitlist.size());
        assertEquals("Kim", waitlist.getParties()[1].getName());
    }

    public void testAddPartyRejectsDuplicateIgnoringCase() {
        waitlist.addParty("Patel", 4);
        assertFalse(waitlist.addParty("patel ", 2));
        assertEquals(1, waitlist.size());
    }

    public void testAddPartyRejectsBadNameOrSize() {
        assertFalse(waitlist.addParty("", 4));
        assertFalse(waitlist.addParty("   ", 4));
        assertFalse(waitlist.addParty(null, 4));
        assertFalse(waitlist.addParty("Lee", 0));
        assertFalse(waitlist.addParty("Lee", 13));
        assertEquals(0, waitlist.size());
    }

    // ----- editParty -----

    public void testEditPartyKeepsPosition() {
        addPatelLeeKim();
        assertTrue(waitlist.editParty("lee", "Smith", 5));
        Party[] parties = waitlist.getParties();
        assertEquals("Patel", parties[0].getName());
        assertEquals("Smith", parties[1].getName());
        assertEquals(5, parties[1].getSize());
        assertEquals("Kim", parties[2].getName());
    }

    public void testEditPartyRejectsOtherPartysName() {
        addPatelLeeKim();
        assertFalse(waitlist.editParty("Kim", "Patel", 2));
        assertEquals("Kim", waitlist.getParties()[2].getName());
        assertEquals(3, waitlist.size());
    }

    public void testEditPartyRejectsMissingPartyOrBadValues() {
        waitlist.addParty("Patel", 4);
        assertFalse(waitlist.editParty("Nobody", "Kim", 2));
        assertFalse(waitlist.editParty("Patel", "Patel", 0));
        assertFalse(waitlist.editParty("Patel", "", 4));
        assertEquals("Patel", waitlist.getParties()[0].getName());
        assertEquals(4, waitlist.getParties()[0].getSize());
    }

    // ----- seatNext -----

    public void testSeatNextSeatsFrontParty() {
        waitlist.addParty("Patel", 4);
        waitlist.addParty("Kim", 2);
        assertEquals("Patel", waitlist.seatNext().getName());
        assertEquals(1, waitlist.size());
        assertEquals(1, waitlist.getSeatedCount());
    }

    public void testSeatNextWhenEmptyThrows() {
        Exception thrown = thrownBy(() -> waitlist.seatNext());
        assertTrue(thrown instanceof NoSuchElementException);
        assertEquals(0, waitlist.getSeatedCount());
    }

    public void testSeatNextKeepsArrivalOrder() {
        addPatelLeeKim();
        assertEquals("Patel", waitlist.seatNext().getName());
        assertEquals("Lee", waitlist.seatNext().getName());
        assertEquals("Kim", waitlist.seatNext().getName());
        assertTrue(waitlist.isEmpty());
    }

    public void testAddAfterLineEmpties() {
        waitlist.addParty("Patel", 4);
        waitlist.seatNext();
        assertTrue(waitlist.addParty("Ana", 2));
        assertEquals("Ana", waitlist.peekNext().getName());
        assertEquals(1, waitlist.getParties().length);
    }

    // ----- peekNext -----

    public void testPeekNextDoesNotRemove() {
        waitlist.addParty("Patel", 4);
        waitlist.addParty("Kim", 2);
        assertEquals("Patel", waitlist.peekNext().getName());
        assertEquals(2, waitlist.size());
    }

    public void testPeekNextWhenEmptyThrows() {
        Exception thrown = thrownBy(() -> waitlist.peekNext());
        assertTrue(thrown instanceof NoSuchElementException);
    }

    // ----- isEmpty -----

    public void testIsEmptyOnNewWaitlist() {
        assertTrue(waitlist.isEmpty());
    }

    public void testIsEmptyAfterAdd() {
        waitlist.addParty("Patel", 4);
        assertFalse(waitlist.isEmpty());
    }

    // ----- size -----

    public void testSizeAfterThreeAdds() {
        addPatelLeeKim();
        assertEquals(3, waitlist.size());
    }

    public void testSizeAfterSeatingEveryone() {
        addPatelLeeKim();
        waitlist.seatNext();
        waitlist.seatNext();
        waitlist.seatNext();
        assertEquals(0, waitlist.size());
    }

    // ----- isFull -----

    public void testIsFullBelowFifty() {
        for (int i = 1; i <= 49; i++) {
            waitlist.addParty("Party" + i, 2);
        }
        assertFalse(waitlist.isFull());
    }

    public void testIsFullAtFiftyRejectsMore() {
        for (int i = 1; i <= 50; i++) {
            waitlist.addParty("Party" + i, 2);
        }
        assertTrue(waitlist.isFull());
        assertFalse(waitlist.addParty("Party51", 2));
        assertEquals(50, waitlist.size());
    }

    // ----- getParties -----

    public void testGetPartiesInArrivalOrder() {
        waitlist.addParty("Patel", 4);
        waitlist.addParty("Kim", 2);
        Party[] parties = waitlist.getParties();
        assertEquals(2, parties.length);
        assertEquals("Patel", parties[0].getName());
        assertEquals("Kim", parties[1].getName());
    }

    public void testGetPartiesReturnsCopy() {
        waitlist.addParty("Patel", 4);
        Party[] parties = waitlist.getParties();
        parties[0] = null;
        assertNotNull(waitlist.getParties()[0]);
        assertEquals("Patel", waitlist.getParties()[0].getName());
    }

    public void testGetPartiesWhenEmpty() {
        assertEquals(0, waitlist.getParties().length);
    }

    // ----- positionOf -----

    public void testPositionOfIgnoresCase() {
        waitlist.addParty("Patel", 4);
        waitlist.addParty("Kim", 2);
        assertEquals(2, waitlist.positionOf("kim"));
    }

    public void testPositionOfMissingOrNull() {
        waitlist.addParty("Patel", 4);
        assertEquals(-1, waitlist.positionOf("Nobody"));
        assertEquals(-1, waitlist.positionOf(null));
    }

    // ----- getSeatedCount -----

    public void testGetSeatedCountAfterSeatingTwo() {
        addPatelLeeKim();
        waitlist.seatNext();
        waitlist.seatNext();
        assertEquals(2, waitlist.getSeatedCount());
    }

    public void testGetSeatedCountBeforeSeating() {
        waitlist.addParty("Patel", 4);
        assertEquals(0, waitlist.getSeatedCount());
    }

    // ----- restartNight -----

    public void testRestartNightClearsLineAndCount() {
        addPatelLeeKim();
        waitlist.seatNext();
        waitlist.restartNight();
        assertTrue(waitlist.isEmpty());
        assertEquals(0, waitlist.getSeatedCount());
    }

    public void testRestartNightWhenEmpty() {
        Exception thrown = thrownBy(() -> waitlist.restartNight());
        assertNull(thrown);
        assertEquals(0, waitlist.size());
        assertEquals(0, waitlist.getSeatedCount());
    }

    // ----- Waitlist(LongSupplier) -----

    public void testClockConstructorStartsEmpty() {
        assertTrue(timed.isEmpty());
        assertEquals(0, timed.getSeatedCount());
    }

    public void testClockConstructorRejectsNull() {
        Exception thrown = thrownBy(() -> new Waitlist(null));
        assertTrue(thrown instanceof IllegalArgumentException);
    }

    // ----- markNoShow -----

    public void testMarkNoShowMovesFrontPartyToBack() {
        timed.addParty("Patel", 4);
        timed.addParty("Lee", 2);
        timed.addParty("Kim", 3);
        assertTrue(timed.markNoShow());
        Party[] parties = timed.getParties();
        assertEquals("Lee", parties[0].getName());
        assertEquals("Kim", parties[1].getName());
        assertEquals("Patel", parties[2].getName());
        assertTrue(parties[2].hasMissed());
        assertEquals(START, parties[2].getMissedAt());
        assertEquals(0, timed.getSeatedCount());
    }

    public void testMarkNoShowSecondMissRemovesParty() {
        timed.addParty("Patel", 4);
        timed.addParty("Lee", 2);
        assertTrue(timed.markNoShow());
        assertTrue(timed.markNoShow());
        assertFalse(timed.markNoShow());
        assertEquals(1, timed.size());
        assertEquals(-1, timed.positionOf("Patel"));
        assertEquals("Lee", timed.peekNext().getName());
    }

    public void testMarkNoShowWithOnlyOneParty() {
        timed.addParty("Patel", 4);
        assertTrue(timed.markNoShow());
        assertEquals(1, timed.size());
        assertTrue(timed.peekNext().hasMissed());
        assertFalse(timed.markNoShow());
        assertTrue(timed.isEmpty());
    }

    public void testMarkNoShowWhenEmptyThrows() {
        Exception thrown = thrownBy(() -> timed.markNoShow());
        assertTrue(thrown instanceof NoSuchElementException);
        assertEquals(0, timed.size());
    }

    // ----- removeExpiredNoShows -----

    public void testRemoveExpiredNoShowsAtLimit() {
        timed.addParty("Patel", 4);
        timed.addParty("Lee", 2);
        timed.addParty("Kim", 3);
        timed.markNoShow();
        advanceMinutes(Waitlist.NO_SHOW_LIMIT_MINUTES);
        assertEquals(1, timed.removeExpiredNoShows());
        Party[] parties = timed.getParties();
        assertEquals(2, parties.length);
        assertEquals("Lee", parties[0].getName());
        assertEquals("Kim", parties[1].getName());
    }

    public void testRemoveExpiredNoShowsKeepsPartyBeforeLimit() {
        timed.addParty("Patel", 4);
        timed.addParty("Lee", 2);
        timed.markNoShow();
        advanceMinutes(Waitlist.NO_SHOW_LIMIT_MINUTES - 1);
        assertEquals(0, timed.removeExpiredNoShows());
        Party[] parties = timed.getParties();
        assertEquals("Lee", parties[0].getName());
        assertEquals("Patel", parties[1].getName());
    }

    public void testRemoveExpiredNoShowsIgnoresPartiesThatNeverMissed() {
        timed.addParty("Patel", 4);
        timed.addParty("Lee", 2);
        advanceMinutes(120);
        assertEquals(0, timed.removeExpiredNoShows());
        assertEquals(2, timed.size());
    }

    public void testRemoveExpiredNoShowsWhenEmpty() {
        advanceMinutes(Waitlist.NO_SHOW_LIMIT_MINUTES);
        assertEquals(0, timed.removeExpiredNoShows());
        assertTrue(timed.isEmpty());
    }

    // ----- editParty with no-shows -----

    public void testEditPartyKeepsMissMark() {
        timed.addParty("Patel", 4);
        timed.addParty("Lee", 2);
        timed.markNoShow();
        assertTrue(timed.editParty("Patel", "Smith", 5));
        Party changed = timed.getParties()[1];
        assertEquals("Smith", changed.getName());
        assertTrue(changed.hasMissed());
        assertEquals(START, changed.getMissedAt());
        advanceMinutes(Waitlist.NO_SHOW_LIMIT_MINUTES);
        assertEquals(1, timed.removeExpiredNoShows());
        assertEquals(-1, timed.positionOf("Smith"));
    }

    public void testEditPartyLeavesUnmarkedPartyUnmarked() {
        timed.addParty("Patel", 4);
        assertTrue(timed.editParty("Patel", "Smith", 5));
        assertFalse(timed.peekNext().hasMissed());
    }
}