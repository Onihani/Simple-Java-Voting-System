import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SignUpForm extends JDialog {
    public User user;
    private JPanel signUpPanel;
    private JTextField signUpNameTextField;
    private JTextField signUpEmailTextField;
    private JTextField signUpIndexNoTextField;
    private JTextField signUpPhoneTextField;
    private JPasswordField signUpPasswordField;
    private JPasswordField signUpConfirmPasswordField;
    private JButton signUpButton;
    private JButton cancelSignUpButton;

    private SignUpForm(JFrame parent) {
        super(parent);
        setTitle("Sign Up");
        setContentPane(signUpPanel);
        setMinimumSize(new Dimension(650, 700));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        signUpButton.addActionListener(e -> {
            String name = signUpNameTextField.getText();
            String email = signUpEmailTextField.getText();
            String indexNumber = signUpIndexNoTextField.getText();
            String phone = signUpPhoneTextField.getText();
            String password = String.valueOf(signUpPasswordField.getPassword());
            String confirmPassword = String.valueOf(signUpConfirmPasswordField.getPassword());

            // checking if any of the field are empty
            if (checkFieldEmptyOrBlank("Name", name)) return;
            if (checkFieldEmptyOrBlank("Email", email)) return;
            if (checkFieldEmptyOrBlank("Index Number", indexNumber)) return;
            if (checkFieldEmptyOrBlank("Phone", phone)) return;
            if (checkFieldEmptyOrBlank("Password", password)) return;
            if (checkFieldEmptyOrBlank("Confirm Password", confirmPassword)) return;

            // checking if password and confirm password fields match
            if (!password.equals(confirmPassword)) {
                showAlertErrorMessage("Password fields do not match");
                return;
            }

            user = getNewlyRegisteredUser(name, email, indexNumber, phone, password);
        });

        cancelSignUpButton.addActionListener(e -> this.dispose());

        setVisible(true);
    }

    public static void main(JFrame parent, VotingSystem votingSystem) {
        SignUpForm signUpForm = new SignUpForm(parent);
        User user = signUpForm.user;

        if (user != null) {
            votingSystem.setUser(user);
            System.out.println("Successful registration of " + user.name);
        } else {
            System.out.println("Registration failed");
        }
    }

    private User getNewlyRegisteredUser(String name, String email, String indexNumber, String phone, String password) {
        User user = null;

        final String DB_URL = "jdbc:mysql://localhost/voting_system?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection dbConnection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            // Connected to DB successfully

            Statement stmt = dbConnection.createStatement();
            String insertUserSqlQuery = "INSERT INTO user (name, email, indexNumber, phone, password) VALUES (?,?,?,?,?)";
            String checkExistingUserEmailSqlQuery = "SELECT * FROM user WHERE email=?";
            String checkExistingUserIndexNoSqlQuery = "SELECT * FROM user WHERE indexNumber=?";

            PreparedStatement checkUserEmailPreparedStatement = dbConnection.prepareStatement(checkExistingUserEmailSqlQuery);
            checkUserEmailPreparedStatement.setString(1, email);
            ResultSet checkUserEmailResultSet = checkUserEmailPreparedStatement.executeQuery();

            if (checkUserEmailResultSet.next()) {
                showAlertErrorMessage("There is an account already connected to this email");
                throw new Exception("user email already exists");
            }

            PreparedStatement checkUserIndexNumberPreparedStatement = dbConnection.prepareStatement(checkExistingUserIndexNoSqlQuery);
            checkUserIndexNumberPreparedStatement.setString(1, indexNumber);
            ResultSet checkUserIndexNumberResultSet = checkUserIndexNumberPreparedStatement.executeQuery();

            if (checkUserIndexNumberResultSet.next()) {
                showAlertErrorMessage("There is an account already connected to this index number");
                throw new Exception("user index number already exists");
            }

            PreparedStatement preparedStatement = dbConnection.prepareStatement(insertUserSqlQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, indexNumber);
            preparedStatement.setString(4, phone);
            preparedStatement.setString(5, password);
            int insertedRows = preparedStatement.executeUpdate();

            if (insertedRows > 0) {
                showAlertSuccessMessage();

                user = new User();
                user.name = name;
                user.email = email;
                user.indexNumber = indexNumber;
                user.phone = phone;
                user.password = password;
            }

            stmt.close();
            dbConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    private void showAlertErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                SignUpForm.this,
                message,
                "Registration Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showAlertSuccessMessage() {
        JOptionPane.showMessageDialog(
                SignUpForm.this,
                "Registration successful. Please login with your credentials to vote",
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private boolean checkFieldEmptyOrBlank(String fieldName, String fieldValue) {
        if (fieldValue.isEmpty() || fieldValue.isBlank()) {
            showAlertErrorMessage(fieldName + " field cannot be empty");
            return true;
        }

        return false;
    }
}
