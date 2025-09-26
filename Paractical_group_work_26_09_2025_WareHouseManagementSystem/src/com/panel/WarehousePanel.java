package com.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.util.DB;
import java.awt.event.*;
import java.sql.*;

public class WarehousePanel extends JPanel implements ActionListener {
    JTextField idTxt = new JTextField(),
               nameTxt = new JTextField(),
               tempTxt = new JTextField(),
               capTxt = new JTextField();

    JButton addBtn = new JButton("Add"),
            updateBtn = new JButton("Update"),
            deleteBtn = new JButton("Delete"),
            loadBtn = new JButton("Load");

    JTable table;
    DefaultTableModel model;

    public WarehousePanel() {
        setLayout(null);

        String[] cols = {"ID","Name","Temperature","Capacity"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(20,240,900,350);

        addLabelField("ID", idTxt, 20);
        addLabelField("Name", nameTxt, 60);
        addLabelField("Temperature", tempTxt, 100);
        addLabelField("Capacity", capTxt, 140);

        idTxt.setEditable(false);

        addButtons();
        add(sp);

        loadWarehouses();

        table.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                int r = table.getSelectedRow();
                if(r>=0){
                    idTxt.setText(model.getValueAt(r,0).toString());
                    nameTxt.setText(model.getValueAt(r,1).toString());
                    tempTxt.setText(model.getValueAt(r,2).toString());
                    capTxt.setText(model.getValueAt(r,3).toString());
                }
            }
        });
    }

    private void addLabelField(String label, JComponent comp, int y){
        JLabel l = new JLabel(label);
        l.setBounds(20,y,100,25);
        comp.setBounds(130,y,200,25);
        add(l);
        add(comp);
    }

    private void addButtons() {
        addBtn.setBounds(360,20,100,30);
        updateBtn.setBounds(360,60,100,30);
        deleteBtn.setBounds(360,100,100,30);
        loadBtn.setBounds(360,140,100,30);

        add(addBtn); add(updateBtn); add(deleteBtn); add(loadBtn);
        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        loadBtn.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e){
        try (Connection con = DB.getConnection()) {
            if(e.getSource() == addBtn) {
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO warehouse(name, temperature, capacity) VALUES(?,?,?)"
                );
                ps.setString(1, nameTxt.getText());
                ps.setString(2, tempTxt.getText());
                ps.setInt(3, Integer.parseInt(capTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Warehouse added");
                loadWarehouses();
            } else if(e.getSource() == updateBtn) {
                PreparedStatement ps = con.prepareStatement(
                    "UPDATE warehouse SET name=?, temperature=?, capacity=? WHERE warehouseid=?"
                );
                ps.setString(1, nameTxt.getText());
                ps.setString(2, tempTxt.getText());
                ps.setInt(3, Integer.parseInt(capTxt.getText()));
                ps.setInt(4, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Updated");
                loadWarehouses();
            } else if(e.getSource() == deleteBtn) {
                PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM warehouse WHERE warehouseid=?"
                );
                ps.setInt(1, Integer.parseInt(idTxt.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Deleted");
                loadWarehouses();
            } else if(e.getSource() == loadBtn) {
                loadWarehouses();
            }
        } catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void loadWarehouses() {
        try (Connection con = DB.getConnection()) {
            model.setRowCount(0);
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM warehouse");
            while(rs.next()){
                model.addRow(new Object[]{
                    rs.getInt("warehouseid"),
                    rs.getString("name"),
                    rs.getString("temperature"),
                    rs.getInt("capacity")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
