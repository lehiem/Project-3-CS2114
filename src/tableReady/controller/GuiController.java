package tableReady.controller;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import tableReady.model.Party;
import tableReady.model.Waitlist;
import tableReady.view.GuiView;

/**
 * Controller for the TableReady graphical interface.
 */
public class GuiController {

    private final Waitlist waitlist;
    private final GuiView view;

    /**
     * Creates the GUI controller.
     *
     * @param waitlist waitlist model
     * @param view graphical view
     */
    public GuiController(
        Waitlist waitlist,
        GuiView view) {

        this.waitlist = waitlist;
        this.view = view;

        registerActions();
    }

    /**
     * Connects GUI buttons to controller methods.
     */
    private void registerActions() {

        view.onAdd(e -> handleAdd());

        view.onEdit(e -> handleEdit());

        view.onSeat(e -> handleSeat());

        view.onPeek(e -> handlePeek());

        view.onRestart(e -> handleRestart());

        view.onExit(e -> handleExit());
    }

    /**
     * Converts text to an integer.
     *
     * @param text input
     * @return number or -1 for invalid input
     */
    public static int parseSize(String text) {

        if (text == null) {
            return -1;
        }

        try {
            return Integer.parseInt(text.trim());
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Adds a party to the waitlist.
     */
    private void handleAdd() {

        String name = view.getPartyName();
        int size = parseSize(view.getPartySize());

        if (!Party.isValidName(name)) {
            view.showError(
                "Enter a valid party name (1-30 characters)."
            );
            return;
        }

        if (!Party.isValidSize(size)) {
            view.showError(
                "Party size must be between 1 and 12."
            );
            return;
        }

        if (waitlist.isFull()) {
            view.showError(
                "The waitlist is full (50 parties)."
            );
            return;
        }

        int existingPosition =
            waitlist.positionOf(name);

        if (existingPosition != -1) {
            view.showError(
                "This party is already on the waitlist"
                    + " at position "
                    + existingPosition + "."
            );
            return;
        }

        boolean added = waitlist.addParty(name, size);

        if (added) {
            int position = waitlist.positionOf(name);

            view.showMessage(
                name.trim()
                    + " added successfully!"
                    + "\nParty size: " + size
                    + "\nPosition: " + position
            );

            view.clearInputs();
            view.refreshDisplay();
        }
        else {
            view.showError(
                "Unable to add the party."
            );
        }
    }

    /**
     * Edits the selected party.
     */
    private void handleEdit() {

        String currentName =
            view.getSelectedPartyName();

        if (currentName == null) {
            view.showError(
                "Select a party from the waitlist first."
            );
            return;
        }

        int position = waitlist.positionOf(currentName);

        if (position == -1) {
            view.showError(
                "The selected party could not be found."
            );
            return;
        }

        Party current =
            waitlist.getParties()[position - 1];

        String newName = JOptionPane.showInputDialog(
            view,
            "Enter the new party name:",
            current.getName()
        );

        if (newName == null) {
            return;
        }

        if (newName.trim().isEmpty()) {
            newName = current.getName();
        }

        if (!Party.isValidName(newName)) {
            view.showError("Invalid party name.");
            return;
        }

        String sizeInput = JOptionPane.showInputDialog(
            view,
            "Enter the new party size:",
            Integer.toString(current.getSize())
        );

        if (sizeInput == null) {
            return;
        }

        int newSize;

        if (sizeInput.trim().isEmpty()) {
            newSize = current.getSize();
        }
        else {
            newSize = parseSize(sizeInput);
        }

        if (!Party.isValidSize(newSize)) {
            view.showError(
                "Party size must be between 1 and 12."
            );
            return;
        }

        int duplicatePosition =
            waitlist.positionOf(newName);

        if (duplicatePosition != -1
            && duplicatePosition != position) {

            view.showError(
                "Another party already has that name."
            );
            return;
        }

        boolean edited = waitlist.editParty(
            currentName,
            newName,
            newSize
        );

        if (edited) {
            view.refreshDisplay();

            view.showMessage(
                "Party updated successfully!"
                    + "\nName: " + newName.trim()
                    + "\nSize: " + newSize
                    + "\nPosition: "
                    + waitlist.positionOf(newName)
            );
        }
        else {
            view.showError(
                "Unable to update the party."
            );
        }
    }

    /**
     * Seats the first party in the queue.
     */
    private void handleSeat() {

        if (waitlist.isEmpty()) {
            view.showMessage(
                "There are no parties waiting."
            );
            return;
        }

        Party next = waitlist.peekNext();

        int choice = JOptionPane.showConfirmDialog(
            view,
            "Seat " + next.getName()
                + " (party of " + next.getSize() + ")?",
            "Seat Next Party",
            JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        Party seated = waitlist.seatNext();

        view.refreshDisplay();

        view.showMessage(
            seated.getName()
                + " has been seated!"
                + "\nParty size: "
                + seated.getSize()
        );
    }

    /**
     * Displays the next waiting party.
     */
    private void handlePeek() {

        if (waitlist.isEmpty()) {
            view.showMessage(
                "There are no parties waiting."
            );
            return;
        }

        Party next = waitlist.peekNext();

        view.showMessage(
            "Next party:"
                + "\nName: " + next.getName()
                + "\nGuests: " + next.getSize()
        );
    }

    /**
     * Clears the waitlist after confirmation.
     */
    private void handleRestart() {

        if (view.confirmRestart()) {

            waitlist.restartNight();

            view.clearInputs();
            view.refreshDisplay();

            view.showMessage(
                "The night has been restarted."
            );
        }
    }

    /**
     * Ends the application after confirmation.
     */
    private void handleExit() {

        if (view.confirmExit()) {

            view.showMessage(
                "Night complete!"
                    + "\nParties seated: "
                    + waitlist.getSeatedCount()
            );

            view.dispose();
        }
    }

    /**
     * Starts the GUI application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Waitlist waitlist = new Waitlist();

            GuiView view = new GuiView(waitlist);

            new GuiController(waitlist, view);

            view.setVisible(true);
        });
    }
}
