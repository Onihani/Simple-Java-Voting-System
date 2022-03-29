import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginForm extends JDialog {
    public User user;
    private JTextField loginIndexNoTextField;
    private JPasswordField loginPasswordField;
    private JButton loginButton;
    private JPanel loginPanel;
    private JButton cancelLoginButton;

    private LoginForm(JFrame parent) {
        super(parent);
        setTitle("Login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(650, 700));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        loginButton.addActionListener(e -> {
            String indexNumber = loginIndexNoTextField.getText();
            String password = String.valueOf(loginPasswordField.getPassword());

            if (indexNumber.isEmpty() || indexNumber.isBlank()) {
                JOptionPane.showMessageDialog(
                        LoginForm.this,
                        "Email field cannot be empty",
                        "Please try again",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (password.isEmpty() || password.isBlank()) {
                JOptionPane.showMessageDialog(
                        LoginForm.this,
                        "Password field cannot be empty",
                        "Please try again",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            user = getAuthenticatedUser(indexNumber, password);

            if (user != null) {
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        LoginForm.this,
                        "Email or Password Invalid",
                        "Please try again",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        cancelLoginButton.addActionListener(e -> this.dispose());

        setVisible(true);
    }

    public static void main(JFrame parent, VotingSystem votingSystem) {
        LoginForm loginForm = new LoginForm(parent);
        User user = loginForm.user;

        if (user != null) {
            votingSystem.setUser(user);
            System.out.println("Successful authentication of " + user.name);
        } else {
            System.out.println("Authentication failed");
        }
    }

    private User getAuthenticatedUser(String indexNumber, String password) {
        User user = null;

        final String DB_URL = "jdbc:mysql://localhost/voting_system?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection dbConnection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            // Connected to DB successfully

            Statement stmt = dbConnection.createStatement();
            String findUserSqlQuery = "SELECT * FROM user WHERE indexNumber=? AND password=?";
            PreparedStatement preparedStatement = dbConnection.prepareStatement(findUserSqlQuery);
            preparedStatement.setString(1, indexNumber);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User();
                user.name = resultSet.getString("name");
                user.email = resultSet.getString("email");
                user.indexNumber = resultSet.getString("indexNumber");
                user.phone = resultSet.getString("phone");
                user.password = resultSet.getString("password");
            }

            stmt.close();
            dbConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }
}
