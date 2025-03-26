package upt.jt.ia;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MomentumResponse implements Runnable {

    private Thread t;
    private Socket s;
    private int port = 0xBAC0;
    private static final String SERVER_IP = "127.0.0.1";
    //private static final String SERVER_IP = "10.180.11.7";
    private boolean run;
    private JFrame jf;
    private JPanel jMainPanel;
    private JTextField jt;
    private JTextField jplayer;
    private JTextField jc;
    private BufferedReader in = null; // for receiving data from the server
    private BufferedWriter out = null; // for sending data to the server

    public MomentumResponse() {

        jf = new JFrame("UPT - IA - MomentumAB");
        new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int res = confirmExit();
                if (res == JOptionPane.YES_OPTION) { // confirmed?
                    System.exit(0);
                }
            }
        };

        jMainPanel = new JPanel();
        jMainPanel.setLayout(new BorderLayout());
        jf.add(jMainPanel);
        JPanel jnorth = new JPanel();
        jMainPanel.add(jnorth, BorderLayout.NORTH);
        jt = new JTextField("    ", 24);
        jnorth.add(jt);
        jnorth.add(new JLabel("Counting: "));
        jc = new JTextField("      ", 24);
        jnorth.add(jc);
        jnorth.add(new JLabel("Player: "));
        jplayer = new JTextField("  ", 3);
        jnorth.add(jplayer);
        JPanel jsouth = new JPanel();
        jMainPanel.add(jsouth, BorderLayout.SOUTH);
        JButton jok = new JButton("  OK  ");
        jsouth.add(jok);
        JButton jend = new JButton("  Exit  ");
        jsouth.add(jend, BorderLayout.SOUTH);
        jend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int res = confirmExit();
                if (res == JOptionPane.YES_OPTION) { // confirmed?
                    System.exit(0);
                }
            }
        });
        run = false;
        jf.pack();
        jf.setVisible(true);
    }

    public void begin() {
        run = true;
        jt.setEnabled(false);
        t = new Thread(this);
        t.start();
    }

    public void run() {
        try {
            s = new Socket(SERVER_IP, port);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error opening socket");
            System.exit(0);
        }
        try {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error opening socket");
            System.exit(0);
        }
        System.out.println("Start run");

        while (run) {
            String st = null;
            try {
                if (in.ready()) { // is there data to be read?
                    jt.setText("");
                    jc.setText("");
                    st = in.readLine(); // reads a line of text
                    System.out.println("readLine " + st); // for debugging
                    if (st.length() < 4) { // short line: it is the player's number
                        jplayer.setText(st);
                        NodeGameAB.setPlayer(st);
                        continue; // restarts while loop
                    }
                    jt.setBackground(Color.yellow); // yellow: it's my turn to make a move
                    jt.setEnabled(true);
                    GameMomentumAB initial = new GameMomentumAB(st);
                    String res = initial.processAB(jc); // process and get the player's move
                    jt.setText(res);
                    System.out.println(initial.toString()); // for debugging

                    String str = jt.getText();
                    try {
                        out.write(str + "\n"); // sends the move to the server
                        out.flush();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.exit(0);
                    }
                    System.out.println("Sent move: " + str);
                    jt.setBackground(Color.white); // end of my turn
                    jt.setEnabled(false);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(0);
            }
            try {
                Thread.yield(); // suspend to free CPU
            } catch (Exception e) {
            }
        }
    }

    public void stop() {
        run = false;
    }

    protected int confirmExit() {
        return JOptionPane.showConfirmDialog(
                null,
                " Confirm end of program ? ",
                " UPT - IA - Momentum ",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        MomentumResponse r = new MomentumResponse();
        r.begin();
    }
}
