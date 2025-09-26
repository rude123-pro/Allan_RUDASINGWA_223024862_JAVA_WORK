package com.panel;
	import javax.swing.*;
	import java.awt.*;
	public class DashboardPanel extends JPanel {
	    public DashboardPanel() {
	        setLayout(new BorderLayout());
	        add(new JLabel("<html><h1>Welcome to WMS</h1><p>Use the Manage menu to access modules.</p></html>"), BorderLayout.CENTER);
	    }
	}



