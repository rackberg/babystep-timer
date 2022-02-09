package de.lrosenberg.kata.babystep_timer.domain;

import de.lrosenberg.kata.babystep_timer.presentation.MotionPanel;
import de.lrosenberg.kata.babystep_timer.presentation.TimerDialog;
import de.lrosenberg.kata.babystep_timer.presentation.TimerDialog.TimerDialogListener;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class BabyStepTimer implements Runnable {
    public static final int TWO_MINUTES = 60 * 2;

    private final TimerExpiredListener expiredListener;

    private int timeLeft = TWO_MINUTES;
    private boolean autoRestart;
    private boolean running;

    public BabyStepTimer(TimerExpiredListener expiredListener) {
        this.expiredListener = expiredListener;
    }

    public void step() {
        timeLeft--;
        long minutes = timeLeft / 60;
        long seconds = timeLeft % 60;
        System.out.printf("\rTime Left: %d minutes, %d seconds", minutes, seconds);
        if (timeLeft == 0) {
            System.out.println();
            expiredListener.timerExpired(this);
            if (autoRestart) {
                restart();
            } else {
                running = false;
            }
        }
    }

    public void restart() {
        timeLeft = TWO_MINUTES;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setAutoRestart(boolean autoRestart) {
        this.autoRestart = autoRestart;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        long delta = 0;

        long minutes = timeLeft / 60;
        long seconds = timeLeft % 60;
        System.out.printf("Time Left: %d minutes, %d seconds", minutes, seconds);

        while (running) {
            delta = System.currentTimeMillis() - startTime;
            if (delta >= 1000) {
                startTime = System.currentTimeMillis();
                delta = 0;
                step();
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    interface TimerExpiredListener {
        void timerExpired(BabyStepTimer timer);
    }

    public static void main(String[] args) {
        System.out.println("Starting BabyStep-Timer...");
        initializeTimer();
    }

    private static void initializeTimer() {
        BabyStepTimer timer = new BabyStepTimer(BabyStepTimer::showAlertDialog);
        timer.setRunning(true);
        Thread thread = new Thread(timer);
        thread.start();

        JButton buttonCommit = new JButton("Commit & Restart");
        buttonCommit.addActionListener(e -> {
            try {
                gitCommit();
                timer.restart();
            } catch (IOException | GitAPIException ex) {
                ex.printStackTrace();
            }
        });

        JButton buttonRestart = new JButton("Restart timer");
        buttonRestart.addActionListener(e -> timer.restart());
        buttonRestart.setOpaque(false);
        buttonRestart.setContentAreaFilled(false);
        buttonRestart.setBorderPainted(false);
        buttonRestart.setForeground(new Color(0, 0, 0, 100));
    
        JFrame f = new JFrame("BabyStep-Timer");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setUndecorated(true);
        f.setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new MotionPanel(f);
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.add(buttonCommit);
        panel.add(buttonRestart);
        f.addMouseListener(new MouseInputAdapter() {
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(255, 255, 255, 255));
            };

            public void mouseExited(MouseEvent e) {
                panel.setBackground(new Color(0, 0, 0, 0));
            };
        });
        
        f.getContentPane().add(panel);
        f.setLocationRelativeTo(null);
        f.setAlwaysOnTop(true);
        f.pack();
        f.setVisible(true);
    }

    private static void showAlertDialog(BabyStepTimer timer) {
        JFrame f = new JFrame("BabyStep-Timer");
        TimerDialog dialog = new TimerDialog(f, "Time is up!");
        dialog.setListener(new TimerDialogListener() {
            @Override
            public void onCommitButtonPressed() {
                try {
                    gitCommit();
                    initializeTimer();
                    dialog.dispose();
                } catch (IOException | GitAPIException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onRevertButtonPressed() {
                try {
                    gitRevert();
                    initializeTimer();
                    dialog.dispose();
                } catch (IOException | GitAPIException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onRestartButtonPressed() {
                initializeTimer();
                dialog.dispose();
            }

            @Override
            public void onExitButtonPressed() {
                System.exit(0);
            }
        });
    }

    private static void gitCommit() throws IOException, GitAPIException {
        try (Git git = Git.init().call()) {
            ObjectId lastCommitId = git.getRepository().resolve(Constants.HEAD);
            if (lastCommitId != null) {
                RevWalk revCommits = new RevWalk(git.getRepository());
                RevCommit lastCommit = revCommits.parseCommit(lastCommitId);

                System.out.println("Using detected git repository:");
                System.out.printf("\tHEAD: #%s%n\tAuthor: %s%n\tMessage: %s%n",
                        lastCommitId.getName(),
                        lastCommit.getAuthorIdent().getName(),
                        lastCommit.getFullMessage());
            }

            Status status = git.status().call();
            if (status.getUncommittedChanges().size() == 0 && status.getUntracked().size() == 0 && status.getAdded().size() == 0) {
                System.out.println("Nothing to commit. Just restarting timer...");
                return;
            }

            git.add().addFilepattern(".").call();
            git.commit().setMessage("Work in progress").call();

            lastCommitId = git.getRepository().resolve(Constants.HEAD);
            System.out.println("Committed changes. Current HEAD is now at #" + lastCommitId.getName());
        }
    }

    private static void gitRevert() throws IOException, GitAPIException {
        try (Git git = Git.init().call()) {
            ObjectId lastCommitId = git.getRepository().resolve(Constants.HEAD);
            if (lastCommitId != null) {
                RevWalk revCommits = new RevWalk(git.getRepository());
                RevCommit lastCommit = revCommits.parseCommit(lastCommitId);

                System.out.println("Using detected git repository:");
                System.out.printf("\tHEAD: #%s%n\tAuthor: %s%n\tMessage: %s%n",
                        lastCommitId.getName(),
                        lastCommit.getAuthorIdent().getName(),
                        lastCommit.getFullMessage());
            }

            Status status = git.status().call();
            if (status.getUncommittedChanges().size() == 0 && status.getUntracked().size() == 0 && status.getAdded().size() == 0) {
                System.out.println("Nothing to revert. Just restarting timer...");
                return;
            }

            git.checkout().setAllPaths(true).call();

            lastCommitId = git.getRepository().resolve(Constants.HEAD);
            System.out.println("Reverted all changes. Current HEAD is now at #" + lastCommitId.getName());
        }
    }
}
