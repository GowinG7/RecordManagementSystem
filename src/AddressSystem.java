import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class AddressSystem {

    AddressSystem() {

        JFrame loginFrame = new JFrame("Record Management System Admin Login ");

        JLabel lblUser = new JLabel("Username");
        lblUser.setBounds(20, 30, 100, 20);
        loginFrame.add(lblUser);

        JTextField txtUser = new JTextField();
        txtUser.setBounds(120, 30, 150, 20);
        loginFrame.add(txtUser);

        JLabel lblPass = new JLabel("Password");
        lblPass.setBounds(20, 70, 100, 20);
        loginFrame.add(lblPass);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setBounds(120, 70, 150, 20);
        loginFrame.add(txtPass);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(120, 110, 80, 25);
        loginFrame.add(btnLogin);

        loginFrame.setSize(350, 220);
        loginFrame.setLayout(null);
        loginFrame.setVisible(true);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // LOGIN BUTTON
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/addressdb", "root", "");

                    String sql = "SELECT * FROM users WHERE username=? AND password=?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, txtUser.getText());
                    ps.setString(2, txtPass.getText());

                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "Login Successful");
                        loginFrame.dispose();
                        openMainWindow();
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid Username or Password");
                    }

                    con.close();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        });
    }

    public void openMainWindow() {

        JFrame f = new JFrame("Record Management System");

        JLabel lblName = new JLabel("Name");
        lblName.setBounds(10, 20, 100, 20);
        f.add(lblName);

        JTextField txtName = new JTextField();
        txtName.setBounds(120, 20, 200, 20);
        f.add(txtName);

        JLabel lblPhone = new JLabel("Phone");
        lblPhone.setBounds(10, 50, 100, 20);
        f.add(lblPhone);

        JTextField txtPhone = new JTextField();
        txtPhone.setBounds(120, 50, 200, 20);
        f.add(txtPhone);

        JLabel lblEmail = new JLabel("Email");
        lblEmail.setBounds(10, 80, 100, 20);
        f.add(lblEmail);

        JTextField txtEmail = new JTextField();
        txtEmail.setBounds(120, 80, 200, 20);
        f.add(txtEmail);

        JLabel lblAddress = new JLabel("Address");
        lblAddress.setBounds(10, 110, 100, 20);
        f.add(lblAddress);

        JTextField txtAddress = new JTextField();
        txtAddress.setBounds(120, 110, 200, 20);
        f.add(txtAddress);

        JButton btnAdd = new JButton("Add");
        btnAdd.setBounds(10, 150, 80, 25);
        f.add(btnAdd);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(100, 150, 90, 25);
        f.add(btnUpdate);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(200, 150, 90, 25);
        f.add(btnDelete);

        JButton btnSearch = new JButton("Search");
        btnSearch.setBounds(300, 150, 90, 25);
        f.add(btnSearch);

        String columns[] = {"ID", "Name", "Phone", "Email", "Address"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(10, 230, 650, 500);
        f.add(sp);

        JButton btnView = new JButton("View All");
        btnView.setBounds(10, 190, 90, 25);
        f.add(btnView);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(110, 190, 90, 25);
        f.add(btnLogout);

        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                f.dispose();            // Close main window
                new AddressSystem();    // Reopen login window
            }
        });

        // ADD with duplicate check
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/addressdb", "root", "");

                    // Check for duplicates
                    String checkSql = "SELECT * FROM addressbook WHERE phone=? OR email=?";
                    PreparedStatement checkPs = con.prepareStatement(checkSql);
                    checkPs.setString(1, txtPhone.getText());
                    checkPs.setString(2, txtEmail.getText());
                    ResultSet rsCheck = checkPs.executeQuery();

                    if (rsCheck.next()) {
                        JOptionPane.showMessageDialog(null, "Duplicate phone or email found. Record not added.");
                    } else {
                        String sql = "INSERT INTO addressbook(name,phone,email,address) VALUES(?,?,?,?)";
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setString(1, txtName.getText());
                        ps.setString(2, txtPhone.getText());
                        ps.setString(3, txtEmail.getText());
                        ps.setString(4, txtAddress.getText());
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Record Added");
                    }

                    con.close();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        });

// UPDATE with duplicate check
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/addressdb", "root", "");

                    // Check if another record has the same phone or email
                    String checkSql = "SELECT * FROM addressbook WHERE (phone=? OR email=?) AND name<>?";
                    PreparedStatement checkPs = con.prepareStatement(checkSql);
                    checkPs.setString(1, txtPhone.getText());
                    checkPs.setString(2, txtEmail.getText());
                    checkPs.setString(3, txtName.getText()); // exclude current record by name
                    ResultSet rsCheck = checkPs.executeQuery();

                    if (rsCheck.next()) {
                        JOptionPane.showMessageDialog(null, "Phone or Email already exists in another record. Update aborted.");
                    } else {
                        String sql = "UPDATE addressbook SET phone=?, email=?, address=? WHERE name=?";
                        PreparedStatement ps = con.prepareStatement(sql);

                        ps.setString(1, txtPhone.getText());
                        ps.setString(2, txtEmail.getText());
                        ps.setString(3, txtAddress.getText());
                        ps.setString(4, txtName.getText());

                        int res = ps.executeUpdate();
                        if (res > 0) JOptionPane.showMessageDialog(null, "Record Updated");
                        else JOptionPane.showMessageDialog(null, "Record Not Found");
                    }

                    con.close();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        });


        // DELETE
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/addressdb", "root", "");

                    String sql = "DELETE FROM addressbook WHERE name=?";
                    PreparedStatement ps = con.prepareStatement(sql);

                    ps.setString(1, txtName.getText());

                    int res = ps.executeUpdate();
                    if (res > 0) JOptionPane.showMessageDialog(null, "Record Deleted");
                    else JOptionPane.showMessageDialog(null, "Record Not Found");

                    con.close();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        });

        // SEARCH
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    model.setRowCount(0);

                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/addressdb", "root", "");

                    String sql = "SELECT * FROM addressbook WHERE name=?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, txtName.getText());

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        model.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getString("phone"), rs.getString("email"), rs.getString("address")});
                    }

                    con.close();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        });

        // VIEW ALL
        btnView.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    model.setRowCount(0);

                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/addressdb", "root", "");

                    String sql = "SELECT * FROM addressbook";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        model.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getString("phone"), rs.getString("email"), rs.getString("address")});
                    }

                    con.close();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        });

        f.setSize(800, 1000);
        f.setLayout(null);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new AddressSystem();
    }
}
