package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.util.DB;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class InventoryPanel extends JPanel implements ActionListener {

    JTextField idTxt = new JTextField();
    JComboBox<String> warehouseCmb = new JComboBox<>();
    JTextField nameTxt = new JTextField();
    JTextField typeTxt = new JTextField();
    JTextField startDateTxt = new JTextField();
    JTextField endDateTxt = new JTextField();
    JTextField statusTxt = new JTextField();
    JComboBox<String> supplierCmb = new JComboBox<>();

    JButton addBtn = new JButton("Add");
    JButton updateBtn = new JButton("Update");
    JButton deleteBtn = new JButton("Delete");
    JButton loadBtn = new JButton("Load");

    JTable table;
    DefaultTableModel model;

    Vector<Integer> warehouseIds = new Vector<>();
    Vector<Integer> supplierIds = new Vector<>();

    public InventoryPanel() {
        setLayout(null);

        String[] cols = {"ID", "Warehouse", "Name", "Type", "Start Date", "End Date", "Status", "Supplier"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20, 350, 900, 300);

        addLabelField("ID", idTxt, 20);
        addLabelField("Warehouse", warehouseCmb, 60);
        addLabelField("Name", nameTxt, 100);
        addLabelField("Type", typeTxt, 140);
        addLabelField("Start Date", startDateTxt, 180);
        addLabelField("End Date", endDateTxt, 220);
        addLabelField("Status", statusTxt, 260);
        addLabelField("Supplier", supplierCmb, 300);

        idTxt.setEditable(false);

        addButtons();
        add(sp);

        loadWarehouses();
        loadSuppliers();
        loadInventory();

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    idTxt.setText(model.getValueAt(r, 0).toString());
                    warehouseCmb.setSelectedItem(model.getValueAt(r, 1).toString());
                    nameTxt.setText(model.getValueAt(r, 2).toString());
                    typeTxt.setText(model.getValueAt(r, 3).toString());
                    startDateTxt.setText(model.getValueAt(r, 4).toString());
                    endDateTxt.setText(model.getValueAt(r, 5).toString());
                    statusTxt.setText(model.getValueAt(r, 6).toString());
                    supplierCmb.setSelectedItem(model.getValueAt(r, 7).toString());
                }
            }
        });
    }

    private void addLabelField(String label, JComponent comp, int y) {
        JLabel l = new JLabel(label);
        l.setBounds(20, y, 120, 25);
        comp.setBounds(150, y, 200, 25);
        add(l);
        add(comp);
    }

    private void addButtons() {
        addBtn.setBounds(370, 20, 100, 30);
        updateBtn.setBounds(370, 60, 100, 30);
        deleteBtn.setBounds(370, 100, 100, 30);
        loadBtn.setBounds(370, 140, 100, 30);
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
                warehouseCmb.addItem(rs.getString("name"));  // correct column
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void loadSuppliers() {
        supplierCmb.removeAllItems();
        supplierIds.clear();
        try (Connection con = DB.getConnection()) {
            // Use 'owner' column instead of 'company_name'
            ResultSet rs = con.createStatement().executeQuery("SELECT supplierid, owner FROM supplier");
            while (rs.next()) {
                supplierIds.add(rs.getInt("supplierid"));
                supplierCmb.addItem(rs.getString("owner"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (warehouseCmb.getSelectedIndex() < 0 || supplierCmb.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Please select warehouse and supplier!");
            return;
        }

        int warehouseId = warehouseIds.get(warehouseCmb.getSelectedIndex());
        int supplierId = supplierIds.get(supplierCmb.getSelectedIndex());
        String name = nameTxt.getText().trim();
        String type = typeTxt.getText().trim();
        String startDate = startDateTxt.getText().trim();
        String endDate = endDateTxt.getText().trim();
        String status = statusTxt.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty!");
            return;
        }

        try (Connection con = DB.getConnection()) {
            if (e.getSource() == addBtn) {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO inventory(warehouseid, name, type, start_date, end_date, status, supplierid) VALUES(?,?,?,?,?,?,?)");
                ps.setInt(1, warehouseId);
                ps.setString(2, name);
                ps.setString(3, type);
                ps.setDate(4, startDate.isEmpty() ? null : Date.valueOf(startDate));
                ps.setDate(5, endDate.isEmpty() ? null : Date.valueOf(endDate));
                ps.setString(6, status);
                ps.setInt(7, supplierId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Inventory added!");
            } else if (e.getSource() == updateBtn) {
                if (idTxt.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Select an inventory record to update!");
                    return;
                }
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE inventory SET warehouseid=?, name=?, type=?, start_date=?, end_date=?, status=?, supplierid=? WHERE inventoryid=?");
                ps.setInt(1, warehouseId);
                ps.setString(2, name);
                ps.setString(3, type);
                ps.setDate(4, startDate.isEmpty() ? null : Date.valueOf(startDate));
                ps.setDate(5, endDate.isEmpty() ? null : Date.valueOf(endDate));
                ps.setString(6, status);
                ps.setInt(7, supplierId);
                ps.setInt(8, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Inventory updated!");
            } else if (e.getSource() == deleteBtn) {
                if (idTxt.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Select an inventory record to delete!");
                    return;
                }
                PreparedStatement ps = con.prepareStatement("DELETE FROM inventory WHERE inventoryid=?");
                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Inventory deleted!");
            } else if (e.getSource() == loadBtn) {
                clearFields();
            }
            loadInventory();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

  private void loadInventory() {
    try (Connection con = DB.getConnection()) {
        model.setRowCount(0);
        String query = "SELECT i.inventoryid, w.name AS warehouse_name, i.name, i.type, i.start_date, i.end_date, i.status, s.owner AS supplier_name " +
                       "FROM inventory i " +
                       "LEFT JOIN warehouse w ON i.warehouseid = w.warehouseid " +
                       "LEFT JOIN supplier s ON i.supplierid = s.supplierid";

        ResultSet rs = con.createStatement().executeQuery(query);
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("inventoryid"),
                rs.getString("warehouse_name"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getDate("start_date"),
                rs.getDate("end_date"),
                rs.getString("status"),
                rs.getString("supplier_name")
            });
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}

private void clearFields() {
        idTxt.setText("");
        warehouseCmb.setSelectedIndex(-1);
        nameTxt.setText("");
        typeTxt.setText("");
        startDateTxt.setText("");
        endDateTxt.setText("");
        statusTxt.setText("");
        supplierCmb.setSelectedIndex(-1);
    }
}
