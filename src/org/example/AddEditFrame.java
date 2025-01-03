package org.example;

import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

public class AddEditFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	JTextField txtName,txtEmail,txtAge;
	JButton btnSave;
	MainFrame mainFrame;
	Integer userId;
	
	public AddEditFrame(MainFrame mainFrame, Integer userId) {
		
		this.mainFrame = mainFrame;
		this.userId = userId;
		
		setTitle(userId == null ? "Add User" : "Edit User");
		setSize(300,200);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new GridLayout(4,2));
		setLocationRelativeTo(null);
		
		// Form fields
		add(new JLabel("Name: "));
		txtName = new JTextField();
		add(txtName);
		
		add(new JLabel("Email: "));
		txtEmail = new JTextField();
		add(txtEmail);
		
		add(new JLabel("Age: "));
		txtAge = new JTextField();
		add(txtAge);
		
		// If editing, load existing data
        if (userId != null) {
            loadData(userId);
        }
		
		btnSave = new JButton("Save");
		add(new JLabel());
		add(btnSave);
		btnSave.addActionListener(e -> saveUser());
		
		setVisible(true);
	}
	
	// Load existing data for editing
    private void loadData(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmnt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            stmnt.setInt(1, userId);
            ResultSet rs = stmnt.executeQuery();
            if (rs.next()) {
                txtName.setText(rs.getString("name"));
                txtAge.setText(String.valueOf(rs.getInt("age")));
                txtEmail.setText(rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	private void saveUser() {
		
		String name = txtName.getText();
		int age = Integer.parseInt(txtAge.getText());
		String email = txtEmail.getText();
		
		try(Connection conn = DBConnection.getConnection()){
			
			if(userId == null) {
				
				PreparedStatement stmnt = conn.prepareStatement("INSERT INTO users (name, age, email) VALUES (?, ?, ?)");
				stmnt.setString(1, name);
				stmnt.setInt(2, age);
				stmnt.setString(3, email);
				
				stmnt.executeUpdate();
				setVisible(false);
				
			}else {
				PreparedStatement stmnt = conn.prepareStatement("UPDATE users SET name = ?, age = ?, email = ? WHERE id = ?");
				stmnt.setString(1, name);
				stmnt.setInt(2, age);
				stmnt.setString(3, email);
				stmnt.setInt(4, userId);
				
				stmnt.executeUpdate();
				setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				setVisible(false);
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		mainFrame.loadData();
		dispose();
	}
	
}
