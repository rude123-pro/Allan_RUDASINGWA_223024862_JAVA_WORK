package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.util.DB;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class OrdersPanel extends JPanel implements ActionListener {
    private JTextField idTxt = new JTextField();
    private JTextField customerTxt = new JTextField();
    private JTextField phoneTxt = new JTextField();
    private JTextField dateTxt = new JTextField(); // format: YYYY-MM-DD

    private JComboBox<String> supplierCmb = new JComboBox<>();
    private Vector<Integer> supplierIds = new Vector<>(); // maps combo index -> supplierid

    private JButton addBtn = new JButton("Add"),
                    updateBtn = new JButton("Update"),
                    deleteBtn = new JButton("Delete"),
                    loadBtn = new JButton("Load");

    private JTable table;
    private DefaultTableModel model;

    public OrdersPanel() {
        setLayout(null);

        String[] cols = {"ID","Customer","Phone","Date","Supplier"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 260, 900, 300);

        addLabelField("ID", idTxt, 20);
        idTxt.setEditable(false);
        addLabelField("Customer", customerTxt, 60);
        addLabelField("Phone", phoneTxt, 100);
        addLabelField("Date (YYYY-MM-DD)", dateTxt, 140);

        JLabel supLabel = new JLabel("Supplier");
        supLabel.setBounds(20, 180, 140, 25);
        supplierCmb.setBounds(170, 180, 200, 25);
        add(supLabel); add(supplierCmb);

        addControls();
        add(sp);

        loadSuppliers(); // populate supplier dropdown (owner + category)
        loadOrders();    // populate table

        // When a row is clicked, populate the form fields
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    idTxt.setText(model.getValueAt(row, 0).toString());
                    customerTxt.setText(String.valueOf(model.getValueAt(row, 1)));
                    phoneTxt.setText(String.valueOf(model.getValueAt(row, 2)));
                    dateTxt.setText(String.valueOf(model.getValueAt(row, 3)));
                    // supplier cell contains same presentation as combo items, so select it
                    Object supObj = model.getValueAt(row, 4);
                    if (supObj != null) {
                        supplierCmb.setSelectedItem(supObj.toString());
                    } else {
                        supplierCmb.setSelectedIndex(-1);
                    }
                }
            }
        });
    }

    private void addLabelField(String label, JComponent comp, int y){
        JLabel l = new JLabel(label);
        l.setBounds(20,y,140,25);
        comp.setBounds(170,y,200,25);
        add(l); add(comp);
    }

    private void addControls() {
        addBtn.setBounds(400,20,120,30);
        updateBtn.setBounds(400,60,120,30);
        deleteBtn.setBounds(400,100,120,30);
        loadBtn.setBounds(400,140,120,30);

        add(addBtn); add(updateBtn); add(deleteBtn); add(loadBtn);

        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        loadBtn.addActionListener(this);
    }

    // Load suppliers into combo (display: "owner (category)"), map supplierIds
    private void loadSuppliers() {
        supplierCmb.removeAllItems();
        supplierIds.clear();
        try (Connection con = DB.getConnection();
             ResultSet rs = con.createStatement().executeQuery(
                 "SELECT supplierid, owner, category FROM supplier ORDER BY supplierid")) {

            while (rs.next()) {
                int id = rs.getInt("supplierid");
                String owner = rs.getString("owner");
                String category = rs.getString("category");
                String display = owner == null ? ("Supplier " + id) : (owner + (category != null ? " (" + category + ")" : ""));
                supplierIds.add(id);
                supplierCmb.addItem(display);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading suppliers: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try (Connection con = DB.getConnection()) {
            if (e.getSource() == addBtn) {
                doAdd(con);
            } else if (e.getSource() == updateBtn) {
                doUpdate(con);
            } else if (e.getSource() == deleteBtn) {
                doDelete(con);
            } else if (e.getSource() == loadBtn) {
                loadSuppliers();
                loadOrders();
                clearFields();
            }
            // refresh table after operations
            loadOrders();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    private void doAdd(Connection con) throws SQLException {
        // validation
        String customer = customerTxt.getText().trim();
        String phone = phoneTxt.getText().trim();
        String dateStr = dateTxt.getText().trim();

        if (customer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Customer name is required.");
            return;
        }
        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Order date is required.");
            return;
        }
        if (supplierCmb.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Please select a supplier.");
            return;
        }

        java.sql.Date sqlDate;
        try {
            sqlDate = java.sql.Date.valueOf(dateStr);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Date must be in YYYY-MM-DD format.");
            return;
        }

        int supplierId = supplierIds.get(supplierCmb.getSelectedIndex());

        String sql = "INSERT INTO orders(customer_name, customer_phone, order_date, supplierid) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customer);
            ps.setString(2, phone.isEmpty() ? null : phone);
            ps.setDate(3, sqlDate);
            ps.setInt(4, supplierId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Order created.");
            clearFields();
        }
    }

    private void doUpdate(Connection con) throws SQLException {
        if (idTxt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select an order to update.");
            return;
        }

        int orderId = Integer.parseInt(idTxt.getText().trim());
        String customer = customerTxt.getText().trim();
        String phone = phoneTxt.getText().trim();
        String dateStr = dateTxt.getText().trim();

        if (customer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Customer name is required.");
            return;
        }
        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Order date is required.");
            return;
        }
        if (supplierCmb.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Please select a supplier.");
            return;
        }

        java.sql.Date sqlDate;
        try {
            sqlDate = java.sql.Date.valueOf(dateStr);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Date must be in YYYY-MM-DD format.");
            return;
        }

        int supplierId = supplierIds.get(supplierCmb.getSelectedIndex());

        String sql = "UPDATE orders SET customer_name=?, customer_phone=?, order_date=?, supplierid=? WHERE orderid=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customer);
            ps.setString(2, phone.isEmpty() ? null : phone);
            ps.setDate(3, sqlDate);
            ps.setInt(4, supplierId);
            ps.setInt(5, orderId);
            int cnt = ps.executeUpdate();
            if (cnt > 0) JOptionPane.showMessageDialog(this, "Order updated.");
            else JOptionPane.showMessageDialog(this, "Order not found.");
            clearFields();
        }
    }

    private void doDelete(Connection con) throws SQLException {
        if (idTxt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select an order to delete.");
            return;
        }
        int orderId = Integer.parseInt(idTxt.getText().trim());
        if (JOptionPane.showConfirmDialog(this, "Delete order #" + orderId + "?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM orders WHERE orderid=?")) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Order deleted.");
            clearFields();
        }
    }

    private void loadOrders() {
        try (Connection con = DB.getConnection()) {
            model.setRowCount(0);
            String sql = "SELECT o.orderid, o.customer_name, o.customer_phone, o.order_date, " +
                         "CONCAT(COALESCE(s.owner,''), (CASE WHEN s.category IS NOT NULL THEN CONCAT(' (', s.category, ')') ELSE '' END)) AS supplier_display " +
                         "FROM orders o LEFT JOIN supplier s ON o.supplierid = s.supplierid ORDER BY o.orderid DESC";
            try (ResultSet rs = con.createStatement().executeQuery(sql)) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("orderid"),
                        rs.getString("customer_name"),
                        rs.getString("customer_phone"),
                        rs.getDate("order_date"),
                        rs.getString("supplier_display")
                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders: " + ex.getMessage());
        }
    }

    private void clearFields() {
        idTxt.setText("");
        customerTxt.setText("");
        phoneTxt.setText("");
        dateTxt.setText("");
        supplierCmb.setSelectedIndex(-1);
    }
}
