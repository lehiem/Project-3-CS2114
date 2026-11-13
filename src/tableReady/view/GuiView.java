package tableReady.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import tableReady.model.Party;
import tableReady.model.Waitlist;

/**
 * Graphical interface for TableReady.
 * Displays the waitlist and a sample restaurant floor plan.
 */
public class GuiView extends JFrame {

    private static final long serialVersionUID = 1L;

    private final Waitlist waitlist;

    private JTextField nameField;
    private JTextField sizeField;

    private JTable waitlistTable;
    private DefaultTableModel tableModel;

    private JLabel waitingLabel;
    private JLabel seatedLabel;

    private JButton addButton;
    private JButton editButton;
    private JButton seatButton;
    private JButton peekButton;
    private JButton restartButton;
    private JButton exitButton;

    private final Color navy = new Color(31, 49, 65);
    private final Color green = new Color(38, 130, 102);

    /**
     * Creates the GUI.
     *
     * @param waitlist the waitlist model
     */
    public GuiView(Waitlist waitlist) {
        this.waitlist = waitlist;

        setTitle("TableReady - Restaurant Waitlist");
        setSize(1050, 650);
        setMinimumSize(new Dimension(850, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildInterface();
        refreshDisplay();
    }

    /**
     * Builds the main window.
     */
    private void buildInterface() {
        setLayout(new BorderLayout(12, 12));

        getContentPane().setBackground(
            new Color(245, 247, 250)
        );

        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    /**
     * Creates the top header.
     *
     * @return header panel
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(
            new BorderLayout()
        );

        header.setBackground(navy);
        header.setBorder(
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel title = new JLabel("TableReady");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel(
            "Restaurant Host Dashboard"
        );
        subtitle.setForeground(
            new Color(205, 220, 230)
        );

        JPanel titlePanel = new JPanel(
            new GridLayout(2, 1)
        );
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        header.add(titlePanel, BorderLayout.WEST);

        return header;
    }

    /**
     * Creates the waitlist and floor plan sections.
     *
     * @return main panel
     */
    private JPanel createMainPanel() {
        JPanel main = new JPanel(
            new GridLayout(1, 2, 15, 0)
        );

        main.setOpaque(false);
        main.setBorder(
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        );

        main.add(createWaitlistPanel());
        main.add(createFloorPlanPanel());

        return main;
    }

    /**
     * Creates the waitlist management panel.
     *
     * @return waitlist panel
     */
    private JPanel createWaitlistPanel() {
        JPanel panel = new JPanel(
            new BorderLayout(10, 10)
        );

        panel.setBackground(Color.WHITE);
        panel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(220, 225, 230)
                ),
                BorderFactory.createEmptyBorder(
                    15, 15, 15, 15
                )
            )
        );

        JLabel heading = new JLabel("Current Waitlist");
        heading.setFont(
            new Font("Arial", Font.BOLD, 20)
        );

        waitingLabel = new JLabel("0 parties waiting");

        JPanel top = new JPanel(
            new GridLayout(2, 1, 0, 5)
        );
        top.setOpaque(false);
        top.add(heading);
        top.add(waitingLabel);

        panel.add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
            new Object[] {
                "Position", "Party Name", "Guests"
            },
            0
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(
                int row,
                int column) {
                return false;
            }
        };

        waitlistTable = new JTable(tableModel);
        waitlistTable.setRowHeight(32);
        waitlistTable.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        waitlistTable.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        panel.add(
            new JScrollPane(waitlistTable),
            BorderLayout.CENTER
        );

        JPanel controls = new JPanel(
            new BorderLayout(8, 8)
        );
        controls.setOpaque(false);

        JPanel fields = new JPanel(
            new GridLayout(2, 2, 8, 8)
        );
        fields.setOpaque(false);

        fields.add(new JLabel("Party name:"));
        nameField = new JTextField();
        fields.add(nameField);

        fields.add(new JLabel("Party size:"));
        sizeField = new JTextField();
        fields.add(sizeField);

        controls.add(fields, BorderLayout.NORTH);

        JPanel buttons = new JPanel(
            new GridLayout(3, 2, 8, 8)
        );
        buttons.setOpaque(false);

        addButton = createButton("Add Party", green);
        editButton = createButton("Edit Party", navy);
        seatButton = createButton("Seat Next", green);
        peekButton = createButton("Peek Next", navy);
        restartButton = createButton(
            "Restart Night",
            new Color(170, 95, 45)
        );
        exitButton = createButton(
            "End Night",
            new Color(150, 55, 55)
        );

        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(seatButton);
        buttons.add(peekButton);
        buttons.add(restartButton);
        buttons.add(exitButton);

        controls.add(buttons, BorderLayout.CENTER);

        panel.add(controls, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates a visual sample restaurant floor plan.
     * Tables are illustrative and do not track occupancy.
     *
     * @return floor plan panel
     */
    private JPanel createFloorPlanPanel() {
        JPanel panel = new JPanel(
            new BorderLayout(10, 10)
        );

        panel.setBackground(Color.WHITE);
        panel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(220, 225, 230)
                ),
                BorderFactory.createEmptyBorder(
                    15, 15, 15, 15
                )
            )
        );

        JLabel heading = new JLabel(
            "Restaurant Floor Plan"
        );
        heading.setFont(
            new Font("Arial", Font.BOLD, 20)
        );

        panel.add(heading, BorderLayout.NORTH);

        JPanel floor = new JPanel(
            new GridLayout(3, 3, 18, 18)
        );

        floor.setBackground(
            new Color(241, 245, 247)
        );

        floor.setBorder(
            BorderFactory.createEmptyBorder(
                25, 20, 25, 20
            )
        );

        int[] capacities = {
            2, 4, 4,
            6, 2, 4,
            4, 6
        };

        for (int i = 0; i < capacities.length; i++) {
            int tableNumber = i + 1;

            JButton tableButton = new JButton(
                "<html><center>TABLE "
                    + tableNumber
                    + "<br>"
                    + capacities[i]
                    + " seats</center></html>"
            );

            tableButton.setFont(
                new Font("Arial", Font.BOLD, 13)
            );

            tableButton.setBackground(
                new Color(218, 232, 239)
            );
            tableButton.setForeground(navy);
            tableButton.setFocusPainted(false);

            tableButton.setToolTipText(
                "Illustrative table only"
            );

            tableButton.addActionListener(e ->
                showMessage(
                    "Table " + tableNumber
                        + " is part of the sample floor plan."
                        + "\nTable assignments are not tracked."
                )
            );

            floor.add(tableButton);
        }

        JLabel entrance = new JLabel(
            "<html><center>ENTRANCE<br>"
                + "Host Station</center></html>",
            SwingConstants.CENTER
        );

        entrance.setFont(
            new Font("Arial", Font.BOLD, 12)
        );
        entrance.setForeground(navy);

        floor.add(entrance);

        panel.add(floor, BorderLayout.CENTER);

        JLabel note = new JLabel(
            "<html><center>Sample layout only. "
                + "Table occupancy is not tracked."
                + "</center></html>",
            SwingConstants.CENTER
        );

        note.setForeground(Color.GRAY);
        panel.add(note, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the bottom status panel.
     *
     * @return footer
     */
    private JPanel createFooter() {
        JPanel footer = new JPanel(
            new BorderLayout()
        );

        footer.setBorder(
            BorderFactory.createEmptyBorder(
                10, 20, 15, 20
            )
        );

        seatedLabel = new JLabel(
            "Parties seated tonight: 0"
        );

        footer.add(seatedLabel, BorderLayout.WEST);

        return footer;
    }

    /**
     * Creates a styled button.
     *
     * @param text button label
     * @param color background color
     * @return new button
     */
    private JButton createButton(
        String text,
        Color color) {

        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setFocusPainted(false);

        return button;
    }

    /**
     * Updates the display from the current model.
     */
    public void refreshDisplay() {
        tableModel.setRowCount(0);

        Party[] parties = waitlist.getParties();

        for (int i = 0; i < parties.length; i++) {
            tableModel.addRow(
                new Object[] {
                    i + 1,
                    parties[i].getName(),
                    parties[i].getSize()
                }
            );
        }

        waitingLabel.setText(
            waitlist.size() + " parties waiting"
        );

        seatedLabel.setText(
            "Parties seated tonight: "
                + waitlist.getSeatedCount()
        );
    }

    public String getPartyName() {
        return nameField.getText();
    }

    public String getPartySize() {
        return sizeField.getText();
    }

    /**
     * Returns the selected party name, or null.
     */
    public String getSelectedPartyName() {
        int row = waitlistTable.getSelectedRow();

        if (row == -1) {
            return null;
        }

        int modelRow =
            waitlistTable.convertRowIndexToModel(row);

        return tableModel.getValueAt(
            modelRow, 1
        ).toString();
    }

    public void clearInputs() {
        nameField.setText("");
        sizeField.setText("");
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "TableReady",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "TableReady Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public boolean confirmRestart() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Clear the waitlist and reset the seated count?",
            "Restart Night",
            JOptionPane.YES_NO_OPTION
        );

        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Asks for a party's new name.
     *
     * @param current the current name, shown as the default
     * @return the text typed, or null if the host cancels
     */
    public String promptNewName(String current) {
        return JOptionPane.showInputDialog(
            this,
            "Enter the new party name:",
            current
        );
    }

    /**
     * Asks for a party's new size.
     *
     * @param current the current size, shown as the default
     * @return the text typed, or null if the host cancels
     */
    public String promptNewSize(int current) {
        return JOptionPane.showInputDialog(
            this,
            "Enter the new party size:",
            Integer.toString(current)
        );
    }

    /**
     * Asks the host to confirm seating the next party.
     *
     * @param next the party at the front of the line
     * @return true if the host clicks Yes
     */
    public boolean confirmSeat(Party next) {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Seat " + next.getName()
                + " (party of " + next.getSize() + ")?",
            "Seat Next Party",
            JOptionPane.YES_NO_OPTION
        );

        return choice == JOptionPane.YES_OPTION;
    }

    public boolean confirmExit() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "End the night and close TableReady?",
            "End Night",
            JOptionPane.YES_NO_OPTION
        );

        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Allows the controller to register button actions.
     * @param listener 
     */
    public void onAdd(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void onEdit(ActionListener listener) {
        editButton.addActionListener(listener);
    }

    public void onSeat(ActionListener listener) {
        seatButton.addActionListener(listener);
    }

    public void onPeek(ActionListener listener) {
        peekButton.addActionListener(listener);
    }

    public void onRestart(ActionListener listener) {
        restartButton.addActionListener(listener);
    }

    public void onExit(ActionListener listener) {
        exitButton.addActionListener(listener);
    }
}