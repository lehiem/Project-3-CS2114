package tableReady.controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import tableReady.model.Party;
import tableReady.model.Waitlist;
import tableReady.view.TextView;

/**
 * Tests WaitlistController with typed input fed in as a String, so every
 * bad-input case from our scope runs without anyone typing. Each test
 * checks what the model holds afterward and what message the host saw.
 */
public class WaitlistControllerTest extends student.TestCase {

    private static final long START = 1000000L;
    private static final long ONE_MINUTE = 60L * 1000L;

    private Waitlist waitlist;
    private long[] now;
    private WaitlistController controller;

    public void setUp() {
        now = new long[] {START};
        waitlist = new Waitlist(() -> now[0]);
    }

    // Runs the text menu on the given typed input and returns what it
    // printed. Input that runs out acts like Ctrl+D.
    private String run(String input) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TextView view = new TextView(
            waitlist,
            new Scanner(input),
            new PrintStream(bytes));
        controller = new WaitlistController(waitlist, view);
        controller.run();
        return bytes.toString();
    }

    // Counts how many times a message appears in the output.
    private int count(String output, String message) {
        int found = 0;
        int index = output.indexOf(message);
        while (index != -1) {
            found++;
            index = output.indexOf(message, index + 1);
        }
        return found;
    }

    private String nameOfLength(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append('a');
        }
        return builder.toString();
    }

    // ----- constructor -----

    public void testConstructorStartsRunning() {
        TextView view = new TextView(
            waitlist,
            new Scanner(""),
            new PrintStream(new ByteArrayOutputStream()));
        assertTrue(new WaitlistController(waitlist, view).isRunning());
    }

    public void testConstructorRejectsNull() {
        Exception thrown = null;
        try {
            new WaitlistController(null, null);
        }
        catch (Exception e) {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }

    // ----- menu commands -----

    public void testMenuCommandsWorkInLowerCase() {
        run("a\nPatel\n4\nv\ne\n");
        assertEquals(1, waitlist.size());
        assertEquals("Patel", waitlist.peekNext().getName());
    }

    public void testMistypedOrBlankCommandShowsUnknown() {
        String output = run("x\n\nhello\nE\n");
        assertEquals(3, count(output, "Unknown command"));
        assertTrue(waitlist.isEmpty());
    }

    public void testEndCommandStops() {
        String output = run("E\nA\nPatel\n4\n");
        assertFalse(controller.isRunning());
        assertTrue(output.contains("Goodbye!"));
        assertTrue(waitlist.isEmpty());
    }

    public void testHandleCommandAfterStopDoesNothing() {
        run("E\n");
        assertFalse(controller.handleCommand("A"));
        assertTrue(waitlist.isEmpty());
    }

    // ----- adding: bad names -----

    public void testAddValidParty() {
        String output = run("A\nPatel\n4\nE\n");
        assertTrue(output.contains("Patel, party of 4, #1 in line."));
        assertEquals(1, waitlist.size());
    }

    public void testBlankAllSpacesAndLongNamesReprompt() {
        String output = run(
            "A\n\n   \n" + nameOfLength(5000) + "\nPatel\n4\nE\n");
        assertEquals(3, count(output, "Invalid name"));
        assertEquals(1, waitlist.size());
        assertEquals("Patel", waitlist.peekNext().getName());
    }

    public void testDuplicateNameIgnoringCaseAndSpacesNotAdded() {
        String output = run("A\nPatel\n4\nA\npatel \nE\n");
        assertTrue(output.contains("patel is already #1."));
        assertEquals(1, waitlist.size());
    }

    // ----- adding: bad sizes -----

    public void testSizeTypedAsWordOrBlankReprompts() {
        String output = run("A\nPatel\nfour\n\n4\nE\n");
        assertEquals(2, count(output, "Invalid size"));
        assertEquals(4, waitlist.peekNext().getSize());
    }

    public void testSizeOutOfRangeReprompts() {
        String output = run("A\nPatel\n0\n-3\n500\n13\n12\nE\n");
        assertEquals(4, count(output, "Invalid size"));
        assertEquals(12, waitlist.peekNext().getSize());
    }

    public void testSizeTooBigForIntReprompts() {
        String output = run("A\nPatel\n99999999999\n4\nE\n");
        assertEquals(1, count(output, "Invalid size"));
        assertEquals(4, waitlist.peekNext().getSize());
    }

    // ----- adding: full waitlist -----

    public void testFullWaitlistRejectsAdd() {
        for (int i = 1; i <= 50; i++) {
            waitlist.addParty("Party" + i, 2);
        }
        String output = run("A\nE\n");
        assertTrue(output.contains("Waitlist is full."));
        assertEquals(50, waitlist.size());
    }

    // ----- seat and peek -----

    public void testSeatAndPeekWorkInOrder() {
        waitlist.addParty("Patel", 4);
        waitlist.addParty("Lee", 2);
        String output = run("P\nS\nE\n");
        assertTrue(output.contains("Next up: Patel (4)."));
        assertTrue(output.contains("Seat the Patel party (4)."));
        assertEquals(1, waitlist.getSeatedCount());
        assertEquals("Lee", waitlist.peekNext().getName());
    }

    public void testSeatAndPeekWithNobodyWaiting() {
        String output = run("S\nP\nE\n");
        assertEquals(2, count(output, "No one is waiting."));
        assertEquals(0, waitlist.getSeatedCount());
    }

    // ----- changing a party -----

    public void testChangeMissingParty() {
        String output = run("C\nKim\nE\n");
        assertTrue(output.contains("Kim isn't on the waitlist."));
        assertTrue(waitlist.isEmpty());
    }

    public void testChangeToAnotherPartysName() {
        waitlist.addParty("Patel", 4);
        waitlist.addParty("Lee", 2);
        String output = run("C\nLee\nPatel\nE\n");
        assertTrue(output.contains("Patel is already #1."));
        assertEquals(2, waitlist.positionOf("Lee"));
    }

    public void testChangeEnterKeepsBothValues() {
        waitlist.addParty("Lee", 2);
        String output = run("C\nLee\n\n\nE\n");
        assertTrue(output.contains("Updated: Lee, party of 2, still #1."));
        assertEquals(2, waitlist.peekNext().getSize());
    }

    public void testChangeBadSizeReprompts() {
        waitlist.addParty("Lee", 2);
        String output = run("C\nLee\nLeigh\nfour\n13\n6\nE\n");
        assertEquals(2, count(output, "Invalid size"));
        Party changed = waitlist.peekNext();
        assertEquals("Leigh", changed.getName());
        assertEquals(6, changed.getSize());
    }

    // ----- restart -----

    public void testRestartWithYClearsLine() {
        waitlist.addParty("Patel", 4);
        String output = run("R\ny\nE\n");
        assertTrue(output.contains("Night restarted."));
        assertTrue(waitlist.isEmpty());
    }

    public void testRestartWithoutYKeepsLine() {
        waitlist.addParty("Patel", 4);
        run("R\nn\nR\nmaybe\nE\n");
        assertEquals(1, waitlist.size());
    }

    // ----- end of input (Ctrl+D) -----

    public void testEndOfInputAtMenuExitsCleanly() {
        String output = run("A\nPatel\n4\n");
        assertTrue(output.contains("Goodbye!"));
        assertFalse(controller.isRunning());
        assertEquals(1, waitlist.size());
    }

    public void testEndOfInputWhileAddingExitsCleanly() {
        String output = run("A\nPatel\n");
        assertTrue(output.contains("Goodbye!"));
        assertTrue(waitlist.isEmpty());
    }

    public void testEndOfInputWhileChangingExitsCleanly() {
        waitlist.addParty("Patel", 4);
        String output = run("C\nPatel\n");
        assertTrue(output.contains("Goodbye!"));
        assertEquals("Patel", waitlist.peekNext().getName());
    }

    // ----- no-shows (N) -----

    public void testNoShowMovesFrontPartyToBack() {
        waitlist.addParty("Patel", 4);
        waitlist.addParty("Lee", 2);
        String output = run("N\nE\n");
        assertTrue(output.contains("Patel moved to the back."));
        Party[] parties = waitlist.getParties();
        assertEquals("Lee", parties[0].getName());
        assertEquals("Patel", parties[1].getName());
        assertTrue(parties[1].hasMissed());
    }

    public void testNoShowTwiceRemovesParty() {
        waitlist.addParty("Patel", 4);
        String output = run("N\nN\nE\n");
        assertTrue(output.contains("Patel moved to the back."));
        assertTrue(output.contains("Patel removed (no-show)."));
        assertTrue(waitlist.isEmpty());
        assertEquals(0, waitlist.getSeatedCount());
    }

    public void testNoShowWithNobodyWaiting() {
        String output = run("N\nn\nE\n");
        assertEquals(2, count(output, "No one is waiting."));
        assertTrue(waitlist.isEmpty());
    }

    public void testExpiredNoShowRemovedBeforeNextCommand() {
        waitlist.addParty("Patel", 4);
        waitlist.addParty("Lee", 2);
        waitlist.markNoShow();
        now[0] += Waitlist.NO_SHOW_LIMIT_MINUTES * ONE_MINUTE;
        String output = run("V\nE\n");
        assertTrue(output.contains("1 no-show removed."));
        assertEquals(1, waitlist.size());
        assertEquals("Lee", waitlist.peekNext().getName());
    }

    public void testNoShowKeptBeforeTimeLimit() {
        waitlist.addParty("Patel", 4);
        waitlist.addParty("Lee", 2);
        waitlist.markNoShow();
        now[0] += (Waitlist.NO_SHOW_LIMIT_MINUTES - 1) * ONE_MINUTE;
        String output = run("V\nE\n");
        assertFalse(output.contains("removed"));
        assertEquals(2, waitlist.size());
    }

    // ----- parseSize -----

    public void testParseSizeNumbers() {
        assertEquals(4, WaitlistController.parseSize("4"));
        assertEquals(7, WaitlistController.parseSize("  7 "));
    }

    public void testParseSizeBadText() {
        assertEquals(-1, WaitlistController.parseSize(null));
        assertEquals(-1, WaitlistController.parseSize(""));
        assertEquals(-1, WaitlistController.parseSize("four"));
        assertEquals(-1, WaitlistController.parseSize("99999999999"));
    }
}