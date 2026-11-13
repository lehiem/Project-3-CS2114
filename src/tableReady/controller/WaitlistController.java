package tableReady.controller;

import java.util.Scanner;

import tableReady.model.Party;
import tableReady.model.Waitlist;
import tableReady.view.TextView;

// -------------------------------------------------------------------------
/**
 *  Write a one-sentence summary of your class here.
 *  Follow it with additional details about its purpose, what abstraction
 *  it represents, and how to use it.
 * 
 *  @author lehie
 *  @version Nov 12, 2026
 */
public class WaitlistController {

    private final Waitlist waitlist;
    private final TextView view;
    private boolean running;

    /**
     * Starts the TableReady application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Waitlist waitlist = new Waitlist();

        TextView view = new TextView(
            waitlist,
            new Scanner(System.in),
            System.out
        );

        WaitlistController controller =
            new WaitlistController(waitlist, view);

        controller.run();
    }

    /**
     * Creates the controller.
     *
     * @param waitlist the waitlist model
     * @param view the text view
     */
    public WaitlistController(Waitlist waitlist, TextView view) {
        if (waitlist == null || view == null) {
            throw new IllegalArgumentException(
                "Waitlist and view cannot be null."
            );
        }

        this.waitlist = waitlist;
        this.view = view;
        running = true;
    }

    /**
     * Runs the main menu until the program ends.
     */
    public void run() {
        while (running) {
            String command = view.promptCommand();
            handleCommand(command);
        }
    }

    /**
     * Handles the user's menu selection.
     *
     * @param input command entered
     * @return true if the program is still running
     */
    public boolean handleCommand(String input) {

        if (!running) {
            return false;
        }

        if (input == null) {
            stop();
            return running;
        }

        String command = input.trim().toUpperCase();

        switch (command) {
            case "A":
                handleAdd();
                break;

            case "V":
                view.showWaitlist();
                break;

            case "P":
                handlePeek();
                break;

            case "S":
                handleSeat();
                break;

            case "C":
                handleEdit();
                break;

            case "R":
                handleRestart();
                break;

            case "E":
                stop();
                break;

            default:
                view.showUnknownCommand();
                break;
        }

        return running;
    }

    /**
     * Converts a String to an integer.
     *
     * @param text input text
     * @return integer or -1 if invalid
     */
    public static int parseSize(String text) {

        if (text == null || text.trim().isEmpty()) {
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
     * Returns whether the program is running.
     *
     * @return true if running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Ends the program.
     */
    private void stop() {

        if (running) {
            running = false;
            view.showGoodbye(waitlist.getSeatedCount());
        }
    }

    /**
     * Handles end of input.
     *
     * @param answer user input
     * @return true if input ended
     */
    private boolean ended(String answer) {

        if (answer == null) {
            stop();
            return true;
        }

        return false;
    }

    /**
     * Adds a party to the waitlist.
     */
    private void handleAdd() {

        if (waitlist.isFull()) {
            view.showFull();
            return;
        }

        String name;

        while (true) {

            name = view.promptName();

            if (ended(name)) {
                return;
            }

            if (!Party.isValidName(name)) {
                view.showInvalidName();
                continue;
            }

            int position = waitlist.positionOf(name);

            if (position != -1) {
                view.showDuplicate(name, position);
                return;
            }

            break;
        }

        int size;

        while (true) {

            String sizeText = view.promptSize();

            if (ended(sizeText)) {
                return;
            }

            size = parseSize(sizeText);

            if (!Party.isValidSize(size)) {
                view.showInvalidSize();
                continue;
            }

            break;
        }

        boolean added = waitlist.addParty(name, size);

        if (added) {

            String trimmedName = name.trim();

            int position =
                waitlist.positionOf(trimmedName);

            view.showAdded(trimmedName, size, position);
            view.showCount();
        }
        else if (waitlist.isFull()) {
            view.showFull();
        }
        else {
            int position = waitlist.positionOf(name);

            if (position != -1) {
                view.showDuplicate(name, position);
            }
        }
    }

    /**
     * Edits a party without changing its position.
     */
    private void handleEdit() {

        String currentName = view.promptPartyToChange();

        if (ended(currentName)) {
            return;
        }

        int originalPosition =
            waitlist.positionOf(currentName);

        if (originalPosition == -1) {
            view.showNotFound(currentName);
            return;
        }

        Party original =
            waitlist.getParties()[originalPosition - 1];

        String newName;

        while (true) {

            String answer =
                view.promptNewName(original.getName());

            if (ended(answer)) {
                return;
            }

            if (answer.trim().isEmpty()) {
                newName = original.getName();
            }
            else {
                newName = answer.trim();
            }

            if (!Party.isValidName(newName)) {
                view.showInvalidName();
                continue;
            }

            int duplicatePosition =
                waitlist.positionOf(newName);

            if (duplicatePosition != -1
                && duplicatePosition != originalPosition) {

                view.showDuplicate(
                    newName,
                    duplicatePosition
                );
                return;
            }

            break;
        }

        int newSize;

        while (true) {

            String answer =
                view.promptNewSize(original.getSize());

            if (ended(answer)) {
                return;
            }

            if (answer.trim().isEmpty()) {
                newSize = original.getSize();
            }
            else {
                newSize = parseSize(answer);
            }

            if (!Party.isValidSize(newSize)) {
                view.showInvalidSize();
                continue;
            }

            break;
        }

        boolean changed = waitlist.editParty(
            original.getName(),
            newName,
            newSize
        );

        if (changed) {

            int position = waitlist.positionOf(newName);

            Party updated =
                waitlist.getParties()[position - 1];

            view.showChanged(updated, position);
            view.showCount();
        }
        else {
            int duplicatePosition =
                waitlist.positionOf(newName);

            if (duplicatePosition != -1
                && duplicatePosition != originalPosition) {

                view.showDuplicate(
                    newName,
                    duplicatePosition
                );
            }
            else {
                view.showNotFound(original.getName());
            }
        }
    }

    /**
     * Seats the first party in the waitlist.
     */
    private void handleSeat() {

        if (waitlist.isEmpty()) {
            view.showNobodyWaiting();
            return;
        }

        Party seated = waitlist.seatNext();

        view.showSeated(seated);
        view.showCount();
    }

    /**
     * Displays the next party without removing it.
     */
    private void handlePeek() {

        if (waitlist.isEmpty()) {
            view.showNobodyWaiting();
            return;
        }

        Party next = waitlist.peekNext();

        view.showNext(next);
    }

    /**
     * Restarts the night after confirmation.
     */
    private void handleRestart() {

        String answer = view.promptConfirmRestart();

        if (ended(answer)) {
            return;
        }

        if (answer.trim().equalsIgnoreCase("y")) {

            waitlist.restartNight();

            view.showRestarted();
            view.showCount();
        }
    }
}
