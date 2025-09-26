package com.form;

import javax.swing.*;
import com.util.DB;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField userTxt = new JTextField();
    private JPasswordField passTxt = new JPasswordField();
    private JButton loginBtn = new JButton("Login");

    public LoginFrame() {
        setTitle("WMS — Login");
        setSize(360, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel u = new JLabel("Username:");
        u.setBounds(20, 20, 80, 25);
        userTxt.setBounds(110, 20, 200, 25);

        JLabel p = new JLabel("Password:");
        p.setBounds(20, 60, 80, 25);
        passTxt.setBounds(110, 60, 200, 25);

        loginBtn.setBounds(110, 100, 100, 30);
        add(u); add(userTxt); add(p); add(passTxt); add(loginBtn);

        // Java 7 compatible ActionListener
        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });

        getRootPane().setDefaultButton(loginBtn);
    }

    private void attemptLogin() {
        String username = userTxt.getText().trim();
        String password = new String(passTxt.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password");
            return;
        }

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT role FROM user WHERE username=? AND password=?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                // open main frame (after login success)
                MainFrame mf = new MainFrame(username, role);
                mf.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    // For testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
