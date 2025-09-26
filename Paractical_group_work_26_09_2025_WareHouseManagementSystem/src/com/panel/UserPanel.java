package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.util.DB;
import java.awt.event.*;
import java.sql.*;
import java.util.regex.Pattern;

public class UserPanel extends JPanel implements ActionListener {
    JTextField idTxt = new JTextField(), nameTxt = new JTextField(), phoneTxt = new JTextField(), emailTxt = new JTextField();
    JPasswordField passTxt = new JPasswordField();
    JComboBox<String> roleCmb = new JComboBox<>(new String[]{"admin", "manager", "staff"});

    JButton addBtn = new JButton("Add"), updateBtn = new JButton("Update"),
            deleteBtn = new JButton("Delete"), loadBtn = new JButton("Load");

    JTable table;
    DefaultTableModel model;

    public UserPanel() {
        setLayout(null);
        String[] labels = {"ID", "Username", "Password", "Phone", "Email", "Role"};
        model = new DefaultTableModel(labels, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 230, 900, 350);

        int y = 20;
        addField("ID", idTxt, y); y += 30;
        addField("Username", nameTxt, y); y += 30;
        addField("Password", passTxt, y); y += 30;
        addField("Phone", phoneTxt, y); y += 30;
        addField("Email", emailTxt, y); y += 30;
        addComboField("Role", roleCmb, y);

        idTxt.setEditable(false);

        addButtons();
        add(sp);

        loadUsers();

        // Table row click -> fill fields
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    idTxt.setText(model.getValueAt(row, 0).toString());     // ID
                    nameTxt.setText(model.getValueAt(row, 1).toString());   // Username
                    passTxt.setText(model.getValueAt(row, 2).toString());   // Password
                    phoneTxt.setText(model.getValueAt(row, 3).toString());  // Phone
                    emailTxt.setText(model.getValueAt(row, 4).toString());  // Email
                    roleCmb.setSelectedItem(model.getValueAt(row, 5).toString()); // Role
                }
            }
        });
    
    }

    private void addField(String lbl, JComponent txt, int y) {
        JLabel l = new JLabel(lbl);
        l.setBounds(20, y, 80, 25);
        txt.setBounds(110, y, 180, 25);
        add(l);
        add(txt);
    }

    private void addComboField(String lbl, JComboBox<String> cmb, int y) {
        JLabel l = new JLabel(lbl);
        l.setBounds(20, y, 80, 25);
        cmb.setBounds(110, y, 180, 25);
        add(l);
        add(cmb);
    }

    private void addButtons() {
        addBtn.setBounds(320, 20, 100, 30);
        updateBtn.setBounds(320, 60, 100, 30);
        deleteBtn.setBounds(320, 100, 100, 30);
        loadBtn.setBounds(320, 140, 100, 30);
        add(addBtn); add(updateBtn); add(deleteBtn); add(loadBtn);
        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        loadBtn.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        try (Connection con = DB.getConnection()) {
            if (e.getSource() == addBtn) {
                if (!validateFields()) return;
                PreparedStatement ps = con.prepareStatement("INSERT INTO user(username,password,phone,email,role) VALUES(?,?,?,?,?)");
                ps.setString(1, nameTxt.getText());
                ps.setString(2, new String(passTxt.getPassword()));
                ps.setString(3, phoneTxt.getText());
                ps.setString(4, emailTxt.getText());
                ps.setString(5, roleCmb.getSelectedItem().toString());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "User Added!");
                loadUsers();

            } else if (e.getSource() == updateBtn) {
                if (idTxt.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Select a user to update.");
                    return;
                }
                if (!validateFields()) return;
                PreparedStatement ps = con.prepareStatement("UPDATE user SET username=?, password=?, phone=?, email=?, role=? WHERE userid=?");
                ps.setString(1, nameTxt.getText());
                ps.setString(2, new String(passTxt.getPassword()));
                ps.setString(3, phoneTxt.getText());
                ps.setString(4, emailTxt.getText());
                ps.setString(5, roleCmb.getSelectedItem().toString());
                ps.setInt(6, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "User Updated!");
                loadUsers();

            } else if (e.getSource() == deleteBtn) {
                if (idTxt.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Select a user to delete.");
                    return;
                }
                PreparedStatement ps = con.prepareStatement("DELETE FROM user WHERE userid=?");
                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "User Deleted!");
                loadUsers();

            } else if (e.getSource() == loadBtn) {
                loadUsers();
                clearFields();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadUsers() {
        try (Connection con = DB.getConnection()) {
            model.setRowCount(0);
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM user");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("userid"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("role")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        idTxt.setText("");
        nameTxt.setText("");
        passTxt.setText("");
        phoneTxt.setText("");
        emailTxt.setText("");
        roleCmb.setSelectedIndex(0);
    }

    // Basic field validation
    private boolean validateFields() {
        if (nameTxt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required.");
            return false;
        }
        if (passTxt.getPassword().length < 4) {
            JOptionPane.showMessageDialog(this, "Password must be at least 4 characters.");
            return false;
        }
        if (!Pattern.matches("\\d{10}", phoneTxt.getText())) {
            JOptionPane.showMessageDialog(this, "Phone must be 10 digits.");
            return false;
        }
        if (!Pattern.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", emailTxt.getText())) {
            JOptionPane.showMessageDialog(this, "Invalid email format.");
            return false;
        }
        return true;
    }
}
