package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.util.DB;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class ProductPanel extends JPanel implements ActionListener {

    JTextField idTxt = new JTextField(), titleTxt = new JTextField(), statusTxt = new JTextField(), valueTxt = new JTextField(), notesTxt = new JTextField();
    JComboBox<String> warehouseCmb = new JComboBox<>();
    JButton addBtn = new JButton("Add"), updateBtn = new JButton("Update"), deleteBtn = new JButton("Delete"), loadBtn = new JButton("Load");
    JTable table;
    DefaultTableModel model;
    Vector<Integer> warehouseIds = new Vector<>(); // To map combo box index to warehouse id

    public ProductPanel() {
        setLayout(null);

        String[] cols = {"ID", "Warehouse", "Title", "Date", "Status", "Value", "Notes"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 300, 900, 300);

        addLabelField("ID", idTxt, 20);
        addLabelField("Warehouse", warehouseCmb, 60);
        addLabelField("Title", titleTxt, 100);
        addLabelField("Status", statusTxt, 140);
        addLabelField("Value", valueTxt, 180);
        addLabelField("Notes", notesTxt, 220);

        idTxt.setEditable(false);

        addButtons();
        add(sp);

        loadWarehouses();
        loadProducts();

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    idTxt.setText(model.getValueAt(r, 0).toString());
                    String whName = model.getValueAt(r, 1).toString();
                    warehouseCmb.setSelectedItem(whName);
                    titleTxt.setText(model.getValueAt(r, 2).toString());
                    statusTxt.setText(model.getValueAt(r, 4).toString());
                    valueTxt.setText(model.getValueAt(r, 5).toString());
                    notesTxt.setText(model.getValueAt(r, 6).toString());
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
        add(addBtn);
        add(updateBtn);
        add(deleteBtn);
        add(loadBtn);
        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        loadBtn.addActionListener(this);
    }

    private void loadWarehouses() {
        warehouseCmb.removeAllItems();
        warehouseIds.clear();
        try (Connection con = DB.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT warehouseid, name FROM warehouse");
            while (rs.next()) {
                warehouseIds.add(rs.getInt("warehouseid"));
                warehouseCmb.addItem(rs.getString("name"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading warehouses: " + ex.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (warehouseCmb.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Please select a warehouse!");
            return;
        }
        int warehouseId = warehouseIds.get(warehouseCmb.getSelectedIndex());
        String title = titleTxt.getText().trim();
        String status = statusTxt.getText().trim();
        String valueStr = valueTxt.getText().trim();
        String notes = notesTxt.getText().trim();

        if (title.isEmpty() || status.isEmpty() || valueStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields!");
            return;
        }

        double value;
        try {
            value = Double.parseDouble(valueStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Value must be a number!");
            return;
        }

        try (Connection con = DB.getConnection()) {
            if (e.getSource() == addBtn) {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO product(warehouseid, title, status, value, notes) VALUES(?,?,?,?,?)");
                ps.setInt(1, warehouseId);
                ps.setString(2, title);
                ps.setString(3, status);
                ps.setDouble(4, value);
                ps.setString(5, notes);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Product added!");
            } else if (e.getSource() == updateBtn) {
                if (idTxt.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Select a product to update!");
                    return;
                }
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE product SET warehouseid=?, title=?, status=?, value=?, notes=? WHERE productid=?");
                ps.setInt(1, warehouseId);
                ps.setString(2, title);
                ps.setString(3, status);
                ps.setDouble(4, value);
                ps.setString(5, notes);
                ps.setInt(6, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Product updated!");
            } else if (e.getSource() == deleteBtn) {
                if (idTxt.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Select a product to delete!");
                    return;
                }
                PreparedStatement ps = con.prepareStatement("DELETE FROM product WHERE productid=?");
                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Product deleted!");
            } else if (e.getSource() == loadBtn) {
                clearFields();
            }
            loadProducts();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadProducts() {
        try (Connection con = DB.getConnection()) {
            model.setRowCount(0);
            String query = "SELECT p.productid, w.name as warehouse_name, p.title, p.date, p.status, p.value, p.notes " +
                    "FROM product p LEFT JOIN warehouse w ON p.warehouseid = w.warehouseid";
            ResultSet rs = con.createStatement().executeQuery(query);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("productid"),
                        rs.getString("warehouse_name"),
                        rs.getString("title"),
                        rs.getDate("date"),
                        rs.getString("status"),
                        rs.getDouble("value"),
                        rs.getString("notes")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products: " + ex.getMessage());
        }
    }

    private void clearFields() {
        idTxt.setText("");
        warehouseCmb.setSelectedIndex(-1);
        titleTxt.setText("");
        statusTxt.setText("");
        valueTxt.setText("");
        notesTxt.setText("");
    }
}
