package com.form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import com.panel.InventoryPanel;
import com.panel.OrdersPanel;
import com.panel.ProductPanel;
import com.panel.PurchasePanel;
import com.panel.SupplierPanel;
import com.panel.UserPanel;
import com.panel.WarehousePanel;
import com.panel.DashboardPanel;

public class MainFrame extends JFrame {
    private JPanel contentPanel = new JPanel(new BorderLayout());
    private String username;
    private String role;

    public MainFrame(String username, String role) {
        this.username = username;
        this.role = role;

        setTitle("Warehouse Management System — Logged in: " + username + " (" + role + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(createTopPanel(), BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // default panel
        showPanel(new DashboardPanel());

        setJMenuBar(createMenuBar());
    }

    private JPanel createTopPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel title = new JLabel("Warehouse Management System");
        p.add(title);
        return p;
    }

    private JMenuBar createMenuBar() {
        JMenuBar mb = new JMenuBar();

        JMenu manage = new JMenu("Manage");

        final JMenuItem users = new JMenuItem("Users");
        final JMenuItem warehouses = new JMenuItem("Warehouses");
        final JMenuItem products = new JMenuItem("Products");
        final JMenuItem inventory = new JMenuItem("Inventory");
        final JMenuItem suppliers = new JMenuItem("Suppliers");
        final JMenuItem purchases = new JMenuItem("Purchases");
        final JMenuItem orders = new JMenuItem("Orders");

        // action listeners (Java 7 compatible)
        users.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPanel(new UserPanel());
            }
        });
        warehouses.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPanel(new WarehousePanel());
            }
        });
        products.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPanel(new ProductPanel());
            }
        });
        inventory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPanel(new InventoryPanel());
            }
        });
        suppliers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPanel(new SupplierPanel());
            }
        });
        purchases.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPanel(new PurchasePanel());
            }
        });
        orders.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPanel(new OrdersPanel());
            }
        });

        // role-based menu visibility
        if ("admin".equalsIgnoreCase(role) || "manager".equalsIgnoreCase(role)) {
            manage.add(users);
        }
        manage.add(warehouses);
        manage.add(products);
        manage.add(inventory);
        manage.add(suppliers);
        manage.add(purchases);
        manage.add(orders);

        mb.add(manage);

        JMenu account = new JMenu("Account");
        JMenuItem logout = new JMenuItem("Logout");
        logout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        account.add(logout);
        mb.add(account);

        return mb;
    }

    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
