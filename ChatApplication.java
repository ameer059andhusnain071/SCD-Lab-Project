package com.mycompany.scdproject;

//yooooooooooo
//it is now in git


import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

class GUI{
int gitHUB;
    private JFrame loginFrame;
    private JPanel loginPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Connection connection;
    private String currentUser;
    private JTextArea chatArea;

    public GUI() {
        loginFrame = new JFrame("Login or Sign Up");
        loginPanel = new JPanel();
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 250);
        loginFrame.setResizable(false);
        connection = establishConnection();
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Sign Up");
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(new JLabel("Username:"), gbc);

        gbc.gridy++;
        loginPanel.add(usernameField, gbc);

        gbc.gridy++;
        loginPanel.add(new JLabel("Password:"), gbc);

        gbc.gridy++;
        loginPanel.add(passwordField, gbc);

        gbc.gridy++;
        loginPanel.add(loginButton, gbc);

        gbc.gridy++;
        loginPanel.add(signupButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (isUserAuthenticated(username, password)) {
                    loginFrame.setVisible(false);
                    currentUser = username;
                    openChatRoom();
                    fetchAndDisplayChatMessages();
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Login Failed. Please check your credentials.");
                }
            }
        });

        signupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginFrame.setVisible(false);
                createSignupGUI();
            }
        });

        loginFrame.add(loginPanel);
        loginFrame.setVisible(true);
    }

    private Connection establishConnection() {
        Connection connection = null;
        String URL = "jdbc:mysql://localhost/scd_project";
        String USER = "root";
        String PASSWORD = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    private boolean isUserAuthenticated(String username, String password) {
        if (connection != null) {
            try {
                String sql = "SELECT * FROM user_details WHERE usernames = ? AND password = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void openChatRoom() {
        JFrame chatFrame = new JFrame("Chat Room");
        JPanel chatPanel = new JPanel();
        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatFrame.setSize(800, 600);
        chatFrame.setResizable(true);

        chatArea = new JTextArea(20, 60);
        chatArea.setEditable(false);
        JTextField messageField = new JTextField(40);
        JButton sendButton = new JButton("Send");

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem deleteMenuItem = new JMenuItem("Delete Chat");
        fileMenu.add(deleteMenuItem);
        menuBar.add(fileMenu);
        chatFrame.setJMenuBar(menuBar);

        chatPanel.add(new JScrollPane(chatArea));
        chatPanel.add(messageField);
        chatPanel.add(sendButton);

        chatFrame.add(chatPanel);
        chatFrame.setVisible(true);

        deleteMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (deleteChatConfirmation()) {
                    clearChat();
                    deleteChatFromDatabase();
                }
            }
        });

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String messageText = messageField.getText();
                if (!messageText.isEmpty()) {
                    chatArea.append(currentUser + ": " + messageText + "\n");
                    saveMessageToDatabase(currentUser, messageText);
                    messageField.setText("");
                }
            }
        });
    }

    private boolean deleteChatConfirmation() {
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the chat?", "Warning", dialogButton);
        return dialogResult == JOptionPane.YES_OPTION;
    }

    private void clearChat() {
        chatArea.setText("");
    }

    private void deleteChatFromDatabase() {
        if (connection != null) {
            try {
                String sql = "DELETE FROM chat";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Chat deleted from the database.");
                } else {
                    System.out.println("Failed to delete chat.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(loginFrame, "Failed to connect to the database.");
        }
    }

    private void createSignupGUI() {
        JFrame signupFrame = new JFrame("Sign Up");
        JPanel signupPanel = new JPanel();
        signupFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        signupFrame.setSize(400, 500);
        signupFrame.setResizable(false);

        JTextField signupUsernameField = new JTextField(20);
        JPasswordField signupPasswordField = new JPasswordField(20);
        JTextField signupEmailField = new JTextField(20);
        JTextField signupPhoneField = new JTextField(20);
        JButton createAccountButton = new JButton("Create Account");
        signupPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        signupPanel.add(new JLabel("Username:"), gbc);

        gbc.gridy++;
        signupPanel.add(signupUsernameField, gbc);

        gbc.gridy++;
        signupPanel.add(new JLabel("Password:"), gbc);

        gbc.gridy++;
        signupPanel.add(signupPasswordField, gbc);

        gbc.gridy++;
        signupPanel.add(new JLabel("Email:"), gbc);

        gbc.gridy++;
        signupPanel.add(signupEmailField, gbc);

        gbc.gridy++;
        signupPanel.add(new JLabel("Phone Number:"), gbc);

        gbc.gridy++;
        signupPanel.add(signupPhoneField, gbc);

        gbc.gridy++;
        signupPanel.add(createAccountButton, gbc);

        createAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = signupUsernameField.getText();
                String password = new String(signupPasswordField.getPassword());
                String email = signupEmailField.getText();
                String phone = signupPhoneField.getText();

                if (username.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                    JOptionPane.showMessageDialog(signupFrame, "Please fill in all fields.");
                } else if (!isValidEmail(email)) {
                    JOptionPane.showMessageDialog(signupFrame, "Invalid email address");
                } /* else if (!isValidPhoneNumber(phone)) {
                    JOptionPane.showMessageDialog(signupFrame, "Invalid phone number");
                } */ else {
                    saveUserData(username, password, email, phone);
                    JOptionPane.showMessageDialog(signupFrame, "Account created");
                    signupFrame.setVisible(false);
                    loginFrame.setVisible(true);
                }
            }
        });

        signupFrame.add(signupPanel);
        signupFrame.setVisible(true);
    }

    private void saveUserData(String username, String password, String email, String phone) {
        if (connection != null) {
            try {
                String sql = "INSERT INTO user_details (usernames, password, emails, phonenumber) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);
                preparedStatement.setString(4, phone);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Data inserted successfully.");
                } else {
                    System.out.println("Failed to insert data.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(loginFrame, "Failed to connect to the database.");
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.contains("@");
    }

    private void saveMessageToDatabase(String username, String message) {
        if (connection != null) {
            try {
                String sql = "INSERT INTO chat (usernames, message) VALUES (?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, message);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Message inserted into chat table.");
                } else {
                    System.out.println("Failed to insert message.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(loginFrame, "Failed to connect to the database.");
        }
    }

    private void fetchAndDisplayChatMessages() {
        if (connection != null) {
            try {
                String sql = "SELECT * FROM chat";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    String username = resultSet.getString("usernames");
                    String message = resultSet.getString("message");
                    chatArea.append(username + ": " + message + "\n");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(loginFrame, "Failed to connect to the database.");
        }
    }

}
class decorator{


}

public class ChatApplication {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUI gui= new GUI();
            }
        });
    }
}