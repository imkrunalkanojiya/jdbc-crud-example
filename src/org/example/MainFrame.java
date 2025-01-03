package org.example;

import javax.swing.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	JTable table;
	DefaultTableModel model;
	JButton btnAdd,btnDelete,btnEdit,btnRefresh;
	JPanel panel;
	
	public MainFrame() {
		
		// Main Frame Setup
		setTitle("JDBC CRUD Application");
		setSize(900,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		
		//table
		table = new JTable();
		model = new DefaultTableModel();
		model.setColumnIdentifiers(new Object[] {"ID","Name","Age","Email"});
		table.setModel(model);
		JScrollPane pane = new JScrollPane(table);
		
		// get All Data from DB
		loadData();
		
		panel = new JPanel();
		
		// Buttons
		btnAdd = new JButton("Add");
		btnEdit = new JButton("Edit");
		btnDelete = new JButton("Delete");
		btnRefresh = new JButton("Refresh");
		
		//add Listeners and buttons
		btnAdd.addActionListener(e -> new AddEditFrame(this, null));
		btnEdit.addActionListener(e -> editRecord());
		btnDelete.addActionListener(e -> deleteRecord());
		btnRefresh.addActionListener(e -> loadData());
		
		
		panel.add(btnAdd);
		panel.add(btnEdit);
		panel.add(btnDelete);
		panel.add(btnRefresh);
		
		add(pane,BorderLayout.CENTER);
		add(panel,BorderLayout.SOUTH);
		
	}
	
	// Load data from db
	void loadData() {
		
		try(Connection conn = DBConnection.getConnection()){
			
			Statement stmnt = conn.createStatement();
			ResultSet rs = stmnt.executeQuery("SELECT * FROM users");
			
			model.setRowCount(0);
			
			while(rs.next()) {
				model.addRow(new Object[] {
						rs.getInt("id"),
						rs.getString("name"),
						rs.getString("email"),
						rs.getInt("age")
				});
			}
			
		}catch(SQLException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	// Delete user
    private void deleteRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to delete.");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();

            loadData(); // Refresh table data
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // edit user
    private void editRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to edit.");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        new AddEditFrame(this, id);
    }
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(()->{
			MainFrame mainFrame = new MainFrame();
			mainFrame.setVisible(true);
		});
		
	}

}
