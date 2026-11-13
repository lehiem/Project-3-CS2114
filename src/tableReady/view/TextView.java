
package tableReady.view;

import java.io.PrintStream;
import java.util.Scanner;

import tableReady.model.Party;
import tableReady.model.Waitlist;

/**
 * Displays the TableReady terminal interface.
 * Reads input and displays messages.
 * Does not modify the Waitlist model.
 */
public class TextView {

    private final Waitlist waitlist;
    private final Scanner in;
    private final PrintStream out;

    /**
     * Creates the text view.
     *
     * @param waitlist model to display
     * @param in scanner for input
     * @param out stream for output
     */
    public TextView(
        Waitlist waitlist,
        Scanner in,
        PrintStream out) {

        if (waitlist == null || in == null || out == null) {
            throw new IllegalArgumentException(
                "View dependencies cannot be null."
            );
        }

        this.waitlist = waitlist;
        this.in = in;
        this.out = out;
    }

    /**
     * Reads one line or returns null at end of input.
     *
     * @return input line or null
     */
    private String readLine() {
        if (!in.hasNextLine()) {
            return null;
        }

        return in.nextLine();
    }

    /**
     * Displays the main menu and asks for a command.
     *
     * @return command entered or null
     */
    public String promptCommand() {
        out.println();
        out.println("===== TableReady =====");
        out.println("A - Add a party");
        out.println("V - View waitlist");
        out.println("P - Peek next party");
        out.println("S - Seat next party");
        out.println("C - Change a party");
        out.println("R - Restart night");
        out.println("E - End night");
        out.print("Choose an option: ");
        out.flush();

        return readLine();
    }

    /**
     * Prompts for a party name.
     *
     * @return name entered or null
     */
    public String promptName() {
        out.print("Party name: ");
        out.flush();

        return readLine();
    }

    /**
     * Prompts for a party size.
     *
     * @return size entered or null
     */
    public String promptSize() {
        out.print("Party size: ");
        out.flush();

        return readLine();
    }

    /**
     * Asks which party should be changed.
     *
     * @return party name or null
     */
    public String promptPartyToChange() {
        out.print("Which party? (name): ");
        out.flush();

        return readLine();
    }

    /**
     * Prompts for a new party name.
     *
     * @param current current name
     * @return new name or null
     */
    public String promptNewName(String current) {
        out.print(
            "New name (Enter keeps " + current + "): "
        );
        out.flush();

        return readLine();
    }

    /**
     * Prompts for a new party size.
     *
     * @param current current size
     * @return new size or null
     */
    public String promptNewSize(int current) {
        out.print(
            "New size (Enter keeps " + current + "): "
        );
        out.flush();

        return readLine();
    }

    /**
     * Asks for confirmation before restarting.
     *
     * @return response or null
     */
    public String promptConfirmRestart() {
        out.print("Clear the whole waitlist? (y/n): ");
        out.flush();

        return readLine();
    }

    /**
     * Displays a successfully added party.
     *
     * @param name party name
     * @param size party size
     * @param position position in line
     */
    public void showAdded(
        String name,
        int size,
        int position) {

        out.println(
            name + ", party of " + size
                + ", #" + position + " in line."
        );
    }

    /**
     * Displays an updated party.
     *
     * @param party updated party
     * @param position current position
     */
    public void showChanged(Party party, int position) {
        out.println(
            "Updated: " + party.getName()
                + ", party of " + party.getSize()
                + ", still #" + position + "."
        );
    }

    /**
     * Displays the party being seated.
     *
     * @param party seated party
     */
    public void showSeated(Party party) {
        out.println(
            "Seat the " + party.getName()
                + " party (" + party.getSize() + ")."
        );
    }

    /**
     * Displays the next party in line.
     *
     * @param party next party
     */
    public void showNext(Party party) {
        out.println(
            "Next up: " + party.getName()
                + " (" + party.getSize() + ")."
        );
    }

    /**
     * Displays every party in waiting order.
     */
    public void showWaitlist() {

        Party[] parties = waitlist.getParties();

        if (parties.length == 0) {
            out.println("No one is waiting.");
            return;
        }

        out.println();
        out.println("===== Current Waitlist =====");

        for (int i = 0; i < parties.length; i++) {
            Party party = parties[i];

            out.println(
                (i + 1) + ". " + party.getName()
                    + " - party of " + party.getSize()
            );
        }
    }

    /**
     * Displays the number of waiting parties.
     */
    public void showCount() {
        int count = waitlist.size();

        out.println(
            count + (count == 1
                ? " party waiting."
                : " parties waiting.")
        );
    }

    /**
     * Displays an invalid name message.
     */
    public void showInvalidName() {
        out.println(
            "Invalid name. Enter 1 to 30 characters."
        );
    }

    /**
     * Displays an invalid size message.
     */
    public void showInvalidSize() {
        out.println(
            "Invalid size. Enter a number from 1 to 12."
        );
    }

    /**
     * Displays a duplicate party message.
     *
     * @param name duplicate name
     * @param position existing position
     */
    public void showDuplicate(String name, int position) {
        out.println(
            name.trim() + " is already #" + position + "."
        );
    }

    /**
     * Displays a full waitlist message.
     */
    public void showFull() {
        out.println("Waitlist is full.");
    }

    /**
     * Displays a party not found message.
     *
     * @param name party name
     */
    public void showNotFound(String name) {
        out.println(
            name + " isn't on the waitlist."
        );
    }

    /**
     * Displays a message when nobody is waiting.
     */
    public void showNobodyWaiting() {
        out.println("No one is waiting.");
    }

    /**
     * Displays an unknown command message.
     */
    public void showUnknownCommand() {
        out.println(
            "Unknown command. Please choose a menu option."
        );
    }

    /**
     * Confirms that the night has restarted.
     */
    public void showRestarted() {
        out.println(
            "Night restarted. Waitlist and seated count cleared."
        );
    }

    /**
     * Displays a goodbye message.
     *
     * @param seatedTonight number of parties seated
     */
    public void showGoodbye(int seatedTonight) {
        out.println();
        out.println("Thank you for using TableReady!");
        out.println(
            "Parties seated tonight: " + seatedTonight
        );
        out.println("Goodbye!");
    }

    /**
     * Stretch goal: displays a party moved to the back.
     *
     * @param party party that missed its call
     */
    public void showMovedToBack(Party party) {
        out.println(
            party.getName() + " moved to the back."
        );
    }

    /**
     * Stretch goal: displays a removed no-show.
     *
     * @param party removed party
     */
    public void showRemoved(Party party) {
        out.println(
            party.getName() + " removed (no-show)."
        );
    }

    /**
     * Stretch goal: displays expired no-show count.
     *
     * @param count number removed
     */
    public void showExpired(int count) {
        out.println(
            count + (count == 1
                ? " no-show removed."
                : " no-shows removed.")
        );
    }

    /**
     * Stretch goal: displays an estimated wait time.
     *
     * @param minutes estimated minutes or -1
     */
    public void showQuote(int minutes) {
        if (minutes == -1) {
            out.println("No estimate yet.");
        }
        else {
            out.println("About " + minutes + " min.");
        }
    }
}
