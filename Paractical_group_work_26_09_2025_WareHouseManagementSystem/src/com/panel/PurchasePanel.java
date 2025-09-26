package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.util.DB;
import java.awt.event.*;
import java.sql.*;

public class PurchasePanel extends JPanel implements ActionListener {
    JTextField idTxt = new JTextField(),
               refNoTxt = new JTextField(),
               amountTxt = new JTextField(),
               dateTxt = new JTextField(),
               methodTxt = new JTextField(),
               statusTxt = new JTextField();

    JComboBox<String> productCombo = new JComboBox<>(); // For foreign key ProductID

    JButton addBtn = new JButton("Add"),
            updateBtn = new JButton("Update"),
            deleteBtn = new JButton("Delete"),
            loadBtn = new JButton("Load");

    JTable table;
    DefaultTableModel model;

    public PurchasePanel() {
        setLayout(null);

        String[] cols = {"ID","ProductID","Ref No","Amount","Date","Method","Status"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 360, 900, 240);

        addLabelField("ID", idTxt, 20);
        addLabelField("Product", productCombo, 60); // dropdown instead of text field
        addLabelField("Reference No", refNoTxt, 100);
        addLabelField("Amount", amountTxt, 140);
        addLabelField("Date (YYYY-MM-DD)", dateTxt, 180);
        addLabelField("Method", methodTxt, 220);
        addLabelField("Status", statusTxt, 260);

        idTxt.setEditable(false);

        addControls();
        add(sp);

        loadProducts();   // Load product foreign key values
        loadPurchases();  // Load purchases

        // When table row is clicked, load values into fields
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    idTxt.setText(model.getValueAt(r, 0).toString());
                    productCombo.setSelectedItem(model.getValueAt(r, 1).toString());
                    refNoTxt.setText(model.getValueAt(r, 2).toString());
                    amountTxt.setText(model.getValueAt(r, 3).toString());
                    dateTxt.setText(model.getValueAt(r, 4).toString());
                    methodTxt.setText(model.getValueAt(r, 5).toString());
                    statusTxt.setText(model.getValueAt(r, 6).toString());
                }
            }
        });
    }

    private void addLabelField(String label, JComponent comp, int y) {
        JLabel l = new JLabel(label);
        l.setBounds(20, y, 140, 25);
        comp.setBounds(170, y, 150, 25);
        add(l);
        add(comp);
    }

    private void addControls() {
        addBtn.setBounds(350, 20, 100, 30);
        updateBtn.setBounds(350, 60, 100, 30);
        deleteBtn.setBounds(350, 100, 100, 30);
        loadBtn.setBounds(350, 140, 100, 30);

        add(addBtn);
        add(updateBtn);
        add(deleteBtn);
        add(loadBtn);

        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        loadBtn.addActionListener(this);
    }

    private void loadProducts() {
        try (Connection con = DB.getConnection()) {
            productCombo.removeAllItems();
            ResultSet rs = con.createStatement().executeQuery("SELECT productid FROM product");
            while (rs.next()) {
                productCombo.addItem(String.valueOf(rs.getInt("productid")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        try (Connection con = DB.getConnection()) {
            if (e.getSource() == addBtn) {
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO purchase(productid, reference_no, amount, date, method, status) VALUES (?, ?, ?, ?, ?, ?)"
                );
                ps.setInt(1, Integer.parseInt(productCombo.getSelectedItem().toString()));
                ps.setString(2, refNoTxt.getText());
                ps.setDouble(3, Double.parseDouble(amountTxt.getText()));
                ps.setDate(4, java.sql.Date.valueOf(dateTxt.getText()));
                ps.setString(5, methodTxt.getText());
                ps.setString(6, statusTxt.getText());

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Purchase Added");
                loadPurchases();
                clearFields();

            } else if (e.getSource() == updateBtn) {
                PreparedStatement ps = con.prepareStatement(
                    "UPDATE purchase SET productid=?, reference_no=?, amount=?, date=?, method=?, status=? WHERE purchaseid=?"
                );
                ps.setInt(1, Integer.parseInt(productCombo.getSelectedItem().toString()));
                ps.setString(2, refNoTxt.getText());
                ps.setDouble(3, Double.parseDouble(amountTxt.getText()));
                ps.setDate(4, java.sql.Date.valueOf(dateTxt.getText()));
                ps.setString(5, methodTxt.getText());
                ps.setString(6, statusTxt.getText());
                ps.setInt(7, Integer.parseInt(idTxt.getText()));

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Purchase Updated");
                loadPurchases();
                clearFields();

            } else if (e.getSource() == deleteBtn) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM purchase WHERE purchaseid=?");
                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Purchase Deleted");
                loadPurchases();
                clearFields();

            } else if (e.getSource() == loadBtn) {
                loadPurchases();
                clearFields();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void loadPurchases() {
        try (Connection con = DB.getConnection()) {
            model.setRowCount(0);
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM purchase");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("purchaseid"),
                    rs.getInt("productid"),
                    rs.getString("reference_no"),
                    rs.getDouble("amount"),
                    rs.getDate("date"),
                    rs.getString("method"),
                    rs.getString("status")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        idTxt.setText("");
        refNoTxt.setText("");
        amountTxt.setText("");
        dateTxt.setText("");
        methodTxt.setText("");
        statusTxt.setText("");
        productCombo.setSelectedIndex(-1);
    }
}
