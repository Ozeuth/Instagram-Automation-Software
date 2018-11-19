package com.webDriver;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class ui extends JFrame {
    JFrame frame;
    JLabel l1;
    JLabel l2;
    JLabel l3;
    JTextField t1;
    JTextField t2;
    JPasswordField p1;
    JButton button;
    JCheckBox c1;
    JCheckBox c2;
    JCheckBox c3;

    boolean isMac;

    public ui(boolean isMac) {
        this.isMac = isMac;

    }

    public void go() {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            frame = new JFrame("Oz's InstaBot");
            l1 = new JLabel("Username");
            l2 = new JLabel("Password");
            l3 = new JLabel("List of Tags");
            t1 = new JTextField();
            t2 = new JTextField();
            p1 = new JPasswordField();
            button = new JButton();
            c1 = new JCheckBox("LIKE");
            c2 = new JCheckBox("COMMENT");
            c3 = new JCheckBox("FOLLOW");


                frame.getContentPane().setBackground(new Color(38, 142, 169));

                p1.setBounds(160, 70, 200, 30);

                JLabel[] ls = {l1, l2, l3};
                for (int l = 0; l < ls.length; l++) {
                    ls[l].setBounds(30, 30 + (40 * l), 150, 30);
                    ls[l].setForeground(Color.WHITE);
                }

                JTextField[] ts = {t1, t2};
                for (int t = 0; t < ts.length; t++) {
                    ts[t].setBounds(160, 30 + (80 * t), 200, 30);
                }

                JCheckBox[] cs = {c1, c2, c3};
                c1.setBounds(30, 150, 100, 30);
                c2.setBounds(150, 150, 100, 30);
                c3.setBounds(285, 150, 100, 30);
                for (int c = 0; c < cs.length; c++) {
                    cs[c].setForeground(Color.WHITE);
                    cs[c].setBackground(new Color(38, 142, 169));
                }

                try {
                    Image icon = ImageIO.read(new File(System.getProperty("user.dir") + "\\eye.bmp"));
                    frame.setIconImage(icon);
                } catch (Exception e) {
                    Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
                    frame.setIconImage(icon);
                }
                frame.add(l1);
                frame.add(t1);
                frame.add(l2);
                frame.add(p1);
                frame.add(l3);
                frame.add(t2);
                frame.add(button);
                frame.add(c1);
                frame.add(c2);
                frame.add(c3);

                frame.setSize(400, 450);
                frame.setLayout(null);
                frame.setVisible(true);

                try {
                    Image logo = null;
                    if (!isMac) {
                        logo = ImageIO.read(new File(System.getProperty("user.dir") + "\\logo.bmp"));
                    } else {
                        logo = ImageIO.read(new File(System.getProperty("user.dir") + "/logo.bmp"));
                    }
                    Image newLogo = logo.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    button.setIcon(new ImageIcon(newLogo));

                    Image logoActive = null;
                    if (!isMac) {
                        logoActive = ImageIO.read(new File(System.getProperty("user.dir") + "\\logoActive.bmp"));
                    } else {
                        logoActive = ImageIO.read(new File(System.getProperty("user.dir") + "/logoActive.bmp"));
                    }
                    Image newLogoActive = logoActive.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    button.setRolloverIcon(new ImageIcon(newLogoActive));
                    button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                    button.setBounds(90, 190, 200, 200);

                } catch (Exception e) {
                    button.setText("Submit");
                    button.setBounds(150, 190, 100, 30);
                }

                Action action = new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        botTest.user = t1.getText();
                        botTest.password = String.valueOf(p1.getPassword());
                        botTest.tags = t2.getText().split(",");
                        if (c1.isSelected()) {
                            botTest.selectionType = 1;
                        } else if (c2.isSelected()) {
                            botTest.selectionType = 2;
                        } else if (c3.isSelected()) {
                            botTest.selectionType = 3;
                        } else {
                            botTest.selectionType = 1;
                        }
                        frame.setVisible(false);
                        frame.dispose();
                    }
                };
                button.addActionListener(action);
            }
        });

    }
}



