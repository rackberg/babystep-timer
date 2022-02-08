package de.lrosenberg.kata.babystep_timer.presentation;

import javax.swing.*;

public class TimerDialog extends JDialog {

    public interface TimerDialogListener {
        void onCommitButtonPressed();

        void onRevertButtonPressed();

        void onRestartButtonPressed();

        void onExitButtonPressed();
    }

    private TimerDialogListener listener;

    public TimerDialog(JFrame frame, String title) {
        super(frame, title);
        JButton buttonCommit = new JButton("Commit & Restart");
        buttonCommit.setMnemonic('c');
        JButton buttonRevert = new JButton("Revert & Restart");
        buttonRevert.setMnemonic('v');
        JButton buttonRestart = new JButton("Restart timer");
        buttonRestart.setMnemonic('r');
        JButton buttonExit = new JButton("Exit");
        buttonExit.setMnemonic('x');

        buttonCommit.addActionListener(e -> listener.onCommitButtonPressed());
        buttonRevert.addActionListener(e -> listener.onRevertButtonPressed());
        buttonRestart.addActionListener(e -> listener.onRestartButtonPressed());
        buttonExit.addActionListener(e -> listener.onExitButtonPressed());

        JPanel panel = new JPanel();
        panel.add(buttonCommit);
        panel.add(buttonRevert);
        panel.add(buttonRestart);
        panel.add(buttonExit);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setSize(400, 300);
        setLocationRelativeTo(null);
        add(panel);
        pack();
        setVisible(true);
    }

    public void setListener(TimerDialogListener listener) {
        this.listener = listener;
    }
}
