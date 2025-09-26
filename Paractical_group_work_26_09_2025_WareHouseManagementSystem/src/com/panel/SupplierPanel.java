package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.util.DB;
import java.awt.event.*;
import java.sql.*;

public class SupplierPanel extends JPanel implements ActionListener {
    JTextField idTxt = new JTextField(),
               ownerTxt = new JTextField(),
               categoryTxt = new JTextField(),
               detailTxt = new JTextField(),
               locationTxt = new JTextField();

    JButton addBtn = new JButton("Add"),
            updateBtn = new JButton("Update"),
            deleteBtn = new JButton("Delete"),
            loadBtn = new JButton("Load");

    JTable table;
    DefaultTableModel model;

    public SupplierPanel() {
        setLayout(null);

        // Table columns match database fields
        String[] cols = {"ID","Owner","Category","Detail","Location"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 300, 900, 260);

        // Input fields
        addLabelField("ID", idTxt, 20);
        addLabelField("Owner", ownerTxt, 60);
        addLabelField("Category", categoryTxt, 100);
        addLabelField("Detail", detailTxt, 140);
        addLabelField("Location", locationTxt, 180);

        idTxt.setEditable(false); // auto-generated ID

        addButtons();
        add(sp);

        loadSuppliers();

        // Click row to load data into text fields
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    idTxt.setText(model.getValueAt(r, 0).toString());
                    ownerTxt.setText(model.getValueAt(r, 1).toString());
                    categoryTxt.setText(model.getValueAt(r, 2).toString());
                    detailTxt.setText(model.getValueAt(r, 3).toString());
                    locationTxt.setText(model.getValueAt(r, 4).toString());
                }
            }
        });
    }

    private void addLabelField(String label, JComponent comp, int y) {
        JLabel l = new JLabel(label);
        l.setBounds(20, y, 100, 25);
        comp.setBounds(130, y, 200, 25);
        add(l);
        add(comp);
    }

    private void addButtons() {
        addBtn.setBounds(360, 20, 100, 30);
        updateBtn.setBounds(360, 60, 100, 30);
        deleteBtn.setBounds(360, 100, 100, 30);
        loadBtn.setBounds(360, 140, 100, 30);

        add(addBtn); add(updateBtn); add(deleteBtn); add(loadBtn);

        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        loadBtn.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        try (Connection con = DB.getConnection()) {
            if (e.getSource() == addBtn) {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO supplier(owner, category, detail, location) VALUES (?, ?, ?, ?)"
                );
                ps.setString(1, ownerTxt.getText());
                ps.setString(2, categoryTxt.getText());
                ps.setString(3, detailTxt.getText());
                ps.setString(4, locationTxt.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Supplier Added");
                loadSuppliers();

            } else if (e.getSource() == updateBtn) {
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE supplier SET owner=?, category=?, detail=?, location=? WHERE supplierid=?"
                );
                ps.setString(1, ownerTxt.getText());
                ps.setString(2, categoryTxt.getText());
                ps.setString(3, detailTxt.getText());
                ps.setString(4, locationTxt.getText());
                ps.setInt(5, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Supplier Updated");
                loadSuppliers();

            } else if (e.getSource() == deleteBtn) {
                PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM supplier WHERE supplierid=?"
                );
                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Supplier Deleted");
                loadSuppliers();

            } else if (e.getSource() == loadBtn) {
                loadSuppliers();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void loadSuppliers() {
        try (Connection con = DB.getConnection()) {
            model.setRowCount(0);
            ResultSet rs = con.createStatement().executeQuery(
                    "SELECT supplierid, owner, category, detail, location FROM supplier"
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("supplierid"),
                        rs.getString("owner"),
                        rs.getString("category"),
                        rs.getString("detail"),
                        rs.getString("location")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
