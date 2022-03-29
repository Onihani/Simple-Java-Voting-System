import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.sql.*;

public class VotingSystem {
    private static JFrame mainFrame;
    private static JFrame aboutWindowFrame; // Frame for about window JWindow
    public User user;
    private JPanel mainPanel;
    private JRadioButton voteForCandidateNameRadioButton;
    private JButton presidentialVoteButton;
    private JRadioButton voteForCandidateNameRadioButton1;
    private JRadioButton voteForCandidateNameRadioButton2;
    private JButton loginButton;
    private JButton registerButton;
    private JScrollPane mainScrollPanel;
    private JLabel welcomeText;
    private JButton logoutButton;
    private JButton secretarialVoteButton;
    private JButton treasurerVoteButton;
    private JPanel presidentialCandidatesPanel;
    private JPanel secretarialCandidatesPanel;
    private JPanel treasurerCandidatesPanel;

    private VotingSystem() {
        mainScrollPanel.getVerticalScrollBar().setUnitIncrement(20);
        logoutButton.setVisible(false);
        loginButton.addActionListener(e -> LoginForm.main(mainFrame, this));
        registerButton.addActionListener(e -> SignUpForm.main(mainFrame, this));
        logoutButton.addActionListener(e -> setUser(null));
        presidentialVoteButton.addActionListener(e -> {
            System.out.println("clicked");
        });

//        fetchCandidates("Presidential");
    }

    // function to create menu bar
    private static JMenuBar createAppMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // menus on menu bar
        JMenu fileMenu = new JMenu("File");
        JMenu displayMenu = new JMenu("Display");
        JMenu helpMenu = new JMenu("Help");

        // fileMenu menu items
        JMenuItem addMenuItem = new JMenuItem("Add");
        JMenuItem exitMenuItem = new JMenuItem("Exit");

        // display menu items
        JMenuItem resultMenuItem = new JMenuItem("Result");

        // help menu items
        JMenuItem aboutMenuItem = new JMenuItem("About");

        // add file menu items to fileMenu
        fileMenu.add(addMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        // add display menu items to editMenu
        displayMenu.add(resultMenuItem);

        // add help menu items to helpMenu
        helpMenu.add(aboutMenuItem);

        // adding menus to the menuBar
        menuBar.add(fileMenu);
        menuBar.add(displayMenu);
        menuBar.add(helpMenu);

        // set mnemonics
        fileMenu.setMnemonic(KeyEvent.VK_F);
        displayMenu.setMnemonic(KeyEvent.VK_E);
        helpMenu.setMnemonic(KeyEvent.VK_H);

        // set accelerator to exit program
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));

        // add action listener to about menu item
        aboutMenuItem.addActionListener(e -> {
            // check if aboutWindowFrame is not null and isVisible
            if (aboutWindowFrame != null && aboutWindowFrame.isVisible()) {
                // move about window frame forward
                aboutWindowFrame.toFront();

                return;
            }

            // if aboutWindowFrame is null then show about window;
            showAboutJWindow();
        });

        // add action listener to exit menu item
        exitMenuItem.addActionListener(e -> System.exit(0));

        return menuBar;
    }

    // method/function to show about JWindow
    public static void showAboutJWindow() {
        // create a new frame
        aboutWindowFrame = new JFrame("Group 10 Members");

        // create a panel
        JPanel aboutWindowPanel = new JPanel();

        JTextArea aboutTextArea = new JTextArea(String.join("\n", "Nathaniel Quansah - 01200877D", "Nathaniel Quansah - 01200877D", "Nathaniel Quansah - 01200877D", "Nathaniel Quansah - 01200877D", "Nathaniel Quansah - 01200877D", "Nathaniel Quansah - 01200877D"));

        // add button to panel
        aboutWindowPanel.add(aboutTextArea);

        aboutWindowFrame.add(aboutWindowPanel);

        // set relative to null
        aboutWindowFrame.setLocationRelativeTo(null);
        // set the size of frame
        aboutWindowFrame.setSize(400, 400);
        // make it visible
        aboutWindowFrame.setVisible(true);
    }

    public static void initialize() {
        mainFrame = new JFrame("Voting System");
        mainFrame.setContentPane(new VotingSystem().mainPanel);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setJMenuBar(createAppMenuBar());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1080, 720);
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        initialize();
    }

    public void fetchCandidates(String type) {
        final String DB_URL = "jdbc:mysql://localhost/voting_system?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection dbConnection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            // Connected to DB successfully

            Statement stmt = dbConnection.createStatement();
            String findUserSqlQuery = "SELECT * FROM candidate WHERE type=?";
            PreparedStatement preparedStatement = dbConnection.prepareStatement(findUserSqlQuery);
            preparedStatement.setString(1, type);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String candidateType = resultSet.getString("type");
                String avatar = resultSet.getString("avatar");

                System.out.println(name);
                System.out.println(candidateType);
                System.out.println(avatar);

                if (candidateType.equalsIgnoreCase("Presidential")) {
                    System.out.println(avatar);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUser(User user) {
        this.user = user;

        if (user != null) {
            welcomeText.setText("Welcome " + user.name);
            loginButton.setVisible(false);
            registerButton.setVisible(false);
            logoutButton.setVisible(true);
        } else {
            welcomeText.setText("Welcome user");
            loginButton.setVisible(true);
            registerButton.setVisible(true);
            logoutButton.setVisible(false);
        }
    }
}
