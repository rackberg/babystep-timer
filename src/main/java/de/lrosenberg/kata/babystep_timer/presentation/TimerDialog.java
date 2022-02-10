package de.lrosenberg.kata.babystep_timer.presentation;

import javax.swing.*;

import de.lrosenberg.kata.babystep_timer.domain.BabyStepTimer;

public class TimerDialog extends JDialog {

    private final BabyStepTimer babyStepTimer;

    public interface TimerDialogListener {
        void onCommitButtonPressed(BabyStepTimer timer);

        void onRevertButtonPressed(BabyStepTimer timer);

        void onRestartButtonPressed(BabyStepTimer timer);

        void onExitButtonPressed();
    }

    private TimerDialogListener listener;

    public TimerDialog(JFrame frame, String title, BabyStepTimer babyStepTimer) {
        super(frame, title);
        this.babyStepTimer = babyStepTimer;

        JButton buttonCommit = new JButton("Commit & Restart");
        buttonCommit.setMnemonic('c');
        JButton buttonRevert = new JButton("Revert & Restart");
        buttonRevert.setMnemonic('v');
        JButton buttonRestart = new JButton("Restart timer");
        buttonRestart.setMnemonic('r');
        JButton buttonExit = new JButton("Exit");
        buttonExit.setMnemonic('x');

        buttonCommit.addActionListener(e -> listener.onCommitButtonPressed(babyStepTimer));
        buttonRevert.addActionListener(e -> listener.onRevertButtonPressed(babyStepTimer));
        buttonRestart.addActionListener(e -> listener.onRestartButtonPressed(babyStepTimer));
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
