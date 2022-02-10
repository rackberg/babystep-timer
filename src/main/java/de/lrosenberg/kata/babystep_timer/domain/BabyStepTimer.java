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
import javax.swing.plaf.metal.MetalButtonUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class BabyStepTimer implements Runnable {
    public static final int TWO_MINUTES = 60 * 2;

    private static BabyStepTimer instance = null;

    private final TimerListener timerListener;

    private int timeLeft = TWO_MINUTES;
    private boolean autoRestart;
    private boolean running;

    public static BabyStepTimer createInstance(TimerListener timerListener) {
        instance = new BabyStepTimer(timerListener);
        return instance;
    }

    public static BabyStepTimer getInstance() {
        return instance;
    }

    protected BabyStepTimer(TimerListener timerListener) {
        this.timerListener = timerListener;
    }

    public void step() {
        timeLeft--;
        long minutes = timeLeft / 60;
        long seconds = timeLeft % 60;
        System.out.printf("\rTime Left: %d minutes, %d seconds", minutes, seconds);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                timerListener.onTick(String.format("%02d:%02d", minutes, seconds));
            }
        });

        if (timeLeft == 0) {
            System.out.println();
            timerListener.timerExpired(this);
            if (autoRestart) {
                restart();
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
            if (delta >= 1000 && timeLeft > 0) {
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

    interface TimerListener {
        void timerExpired(BabyStepTimer timer);
        void onTick(String timeString);
    }

    public static void main(String[] args) {
        System.out.println("Starting BabyStep-Timer...");
        initializeTimer();
    }

    private static void initializeTimer() {
        JLabel labelTimeLeft = new JLabel("Time Left: 02:00");
        labelTimeLeft.setFont(new Font("Verdana", Font.BOLD, 16));
        labelTimeLeft.setForeground(new Color(0, 255, 0, 100));
        labelTimeLeft.setVerticalAlignment(SwingConstants.TOP);

        BabyStepTimer timer = BabyStepTimer.createInstance(new TimerListener() {

            @Override
            public void timerExpired(BabyStepTimer timer) {
                showAlertDialog(timer);
            }

            @Override
            public void onTick(String timeString) {
                labelTimeLeft.setText("Time Left: " + timeString);
            }
            
        });
        timer.setRunning(true);
        Thread thread = new Thread(timer);
        thread.start();

        JButton buttonCommit = new JButton("Commit & Restart");
        buttonCommit.setUI(new MetalButtonUI());
        buttonCommit.setBackground(new Color(255, 255, 255, 10));
        buttonCommit.setForeground(new Color(0, 0, 0, 100));
        buttonCommit.addMouseListener(new MouseInputAdapter() {
            public void mouseEntered(MouseEvent e) {
                buttonCommit.setBackground(new Color(255, 255, 255, 255));
                buttonCommit.setForeground(new Color(0, 0, 0, 255));
            };

            public void mouseExited(MouseEvent e) {
                buttonCommit.setBackground(new Color(255, 255, 255, 10));
                buttonCommit.setForeground(new Color(0, 0, 0, 100));
            };
        });
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
        buttonRestart.setUI(new MetalButtonUI());
        buttonRestart.setBackground(new Color(255, 255, 255, 10));
        buttonRestart.setForeground(new Color(0, 0, 0, 100));
        buttonRestart.addMouseListener(new MouseInputAdapter() {
            public void mouseEntered(MouseEvent e) {
                buttonRestart.setBackground(new Color(255, 255, 255, 255));
                buttonRestart.setForeground(new Color(0, 0, 0, 255));
            };

            public void mouseExited(MouseEvent e) {
                buttonRestart.setBackground(new Color(255, 255, 255, 10));
                buttonRestart.setForeground(new Color(0, 0, 0, 100));
            };
        });

        JButton buttonExit = new JButton("Exit timer");
        buttonExit.addActionListener(e -> System.exit(0));
        buttonExit.setUI(new MetalButtonUI());
        buttonExit.setBackground(new Color(255, 255, 255, 10));
        buttonExit.setForeground(new Color(0, 0, 0, 100));
        buttonExit.addMouseListener(new MouseInputAdapter() {
            public void mouseEntered(MouseEvent e) {
                buttonExit.setBackground(new Color(255, 255, 255, 255));
                buttonExit.setForeground(new Color(0, 0, 0, 255));
            };

            public void mouseExited(MouseEvent e) {
                buttonExit.setBackground(new Color(255, 255, 255, 10));
                buttonExit.setForeground(new Color(0, 0, 0, 100));
            };
        });
    
        JFrame f = new JFrame("BabyStep-Timer");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setUndecorated(true);
        f.setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new MotionPanel(f);
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.add(buttonCommit);
        panel.add(buttonRestart);
        panel.add(buttonExit);
        panel.add(labelTimeLeft);
        
        f.getContentPane().add(panel);
        f.setLocationByPlatform(true);
        f.setAlwaysOnTop(true);
        f.pack();
        f.setVisible(true);
    }

    private static void showAlertDialog(BabyStepTimer timer) {
        JFrame f = new JFrame("BabyStep-Timer");
        TimerDialog dialog = new TimerDialog(f, "Time is up!", timer);
        dialog.setListener(new TimerDialogListener() {
            @Override
            public void onCommitButtonPressed(BabyStepTimer timer) {
                try {
                    gitCommit();
                    timer.restart();
                    dialog.dispose();
                } catch (IOException | GitAPIException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onRevertButtonPressed(BabyStepTimer timer) {
                try {
                    gitRevert();
                    timer.restart();
                    dialog.dispose();
                } catch (IOException | GitAPIException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onRestartButtonPressed(BabyStepTimer timer) {
                timer.restart();
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
