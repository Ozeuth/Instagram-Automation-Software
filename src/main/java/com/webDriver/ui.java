package com.webDriver;

import java.awt.Color;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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
            public void run() {
                ui.this.frame = new JFrame("Oz's InstaBot");
                ui.this.l1 = new JLabel("Username");
                ui.this.l2 = new JLabel("Password");
                ui.this.l3 = new JLabel("List of Tags");
                ui.this.t1 = new JTextField();
                ui.this.t2 = new JTextField();
                ui.this.p1 = new JPasswordField();
                ui.this.button = new JButton();
                ui.this.c1 = new JCheckBox("LIKE");
                ui.this.c2 = new JCheckBox("COMMENT");
                ui.this.c3 = new JCheckBox("FOLLOW");
                ui.this.frame.getContentPane().setBackground(new Color(38, 142, 169));
                ui.this.p1.setBounds(160, 70, 200, 30);
                JLabel[] ls = new JLabel[]{ui.this.l1, ui.this.l2, ui.this.l3};

                for(int l = 0; l < ls.length; ++l) {
                    ls[l].setBounds(30, 30 + 40 * l, 150, 30);
                    ls[l].setForeground(Color.WHITE);
                }

                JTextField[] ts = new JTextField[]{ui.this.t1, ui.this.t2};

                for(int t = 0; t < ts.length; ++t) {
                    ts[t].setBounds(160, 30 + 80 * t, 200, 30);
                }

                JCheckBox[] cs = new JCheckBox[]{ui.this.c1, ui.this.c2, ui.this.c3};
                ui.this.c1.setBounds(30, 150, 100, 30);
                ui.this.c2.setBounds(150, 150, 100, 30);
                ui.this.c3.setBounds(285, 150, 100, 30);

                for(int c = 0; c < cs.length; ++c) {
                    cs[c].setForeground(Color.WHITE);
                    cs[c].setBackground(new Color(38, 142, 169));
                }

                BufferedImage logo;
                try {
                    logo = ImageIO.read(new File(System.getProperty("user.dir") + "\\eye.bmp"));
                    ui.this.frame.setIconImage(logo);
                } catch (Exception var9) {
                    Image icon = new BufferedImage(1, 1, 3);
                    ui.this.frame.setIconImage(icon);
                }

                ui.this.frame.add(ui.this.l1);
                ui.this.frame.add(ui.this.t1);
                ui.this.frame.add(ui.this.l2);
                ui.this.frame.add(ui.this.p1);
                ui.this.frame.add(ui.this.l3);
                ui.this.frame.add(ui.this.t2);
                ui.this.frame.add(ui.this.button);
                ui.this.frame.add(ui.this.c1);
                ui.this.frame.add(ui.this.c2);
                ui.this.frame.add(ui.this.c3);
                ui.this.frame.setSize(400, 450);
                ui.this.frame.setLayout((LayoutManager)null);
                ui.this.frame.setVisible(true);

                try {
                    logo = null;
                    if (!ui.this.isMac) {
                        logo = ImageIO.read(new File(System.getProperty("user.dir") + "\\logo.bmp"));
                    } else {
                        logo = ImageIO.read(new File(System.getProperty("user.dir") + "/logo.bmp"));
                    }

                    Image newLogo = logo.getScaledInstance(200, 200, 4);
                    ui.this.button.setIcon(new ImageIcon(newLogo));
                    Image logoActive = null;
                    if (!ui.this.isMac) {
                        logoActive = ImageIO.read(new File(System.getProperty("user.dir") + "\\logoActive.bmp"));
                    } else {
                        logoActive = ImageIO.read(new File(System.getProperty("user.dir") + "/logoActive.bmp"));
                    }

                    Image newLogoActive = logoActive.getScaledInstance(200, 200, 4);
                    ui.this.button.setRolloverIcon(new ImageIcon(newLogoActive));
                    ui.this.button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                    ui.this.button.setBounds(90, 190, 200, 200);
                } catch (Exception var8) {
                    ui.this.button.setText("Submit");
                    ui.this.button.setBounds(150, 190, 100, 30);
                }

                Action action = new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        botTest.user = ui.this.t1.getText();
                        botTest.password = String.valueOf(ui.this.p1.getPassword());
                        botTest.tags = ui.this.t2.getText().split(",");
                        if (ui.this.c1.isSelected()) {
                            botTest.selectionType = 1;
                        } else if (ui.this.c2.isSelected()) {
                            botTest.selectionType = 2;
                        } else if (ui.this.c3.isSelected()) {
                            botTest.selectionType = 3;
                        } else {
                            botTest.selectionType = 1;
                        }

                        ui.this.frame.setVisible(false);
                        ui.this.frame.dispose();
                    }
                };
                ui.this.button.addActionListener(action);
            }
        });
    }
}
