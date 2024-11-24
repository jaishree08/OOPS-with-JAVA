import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class NutritionAssistant {

    private JFrame frame;
    private JTextField nameField, ageField;
    private JComboBox<String> dietComboBox;
    private JButton showPlanButton;
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection connection;

    public NutritionAssistant() {
        frame = new JFrame("Personalized Nutrition Assistant");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout()); // Set layout to BorderLayout for central positioning

        // Set color theme for background
        frame.getContentPane().setBackground(new Color(30, 30, 30)); // Dark background color

        // Bold border style
        LineBorder boldBorder = new LineBorder(Color.CYAN, 2, true); // Cyan color, 2px thickness, rounded

        // Title panel
        JLabel titleLabel = new JLabel("Personalized Nutrition Assistant", JLabel.CENTER);
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(30, 30, 30));
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH); // Place title at the top

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.CYAN);
        inputPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        nameField = new JTextField(15);
        nameField.setBackground(new Color(50, 50, 50));
        nameField.setForeground(Color.CYAN);
        nameField.setBorder(boldBorder);
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setForeground(Color.CYAN);
        inputPanel.add(ageLabel, gbc);

        gbc.gridx = 1;
        ageField = new JTextField(5);
        ageField.setBackground(new Color(50, 50, 50));
        ageField.setForeground(Color.CYAN);
        ageField.setBorder(boldBorder);
        inputPanel.add(ageField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel dietLabel = new JLabel("Diet Type:");
        dietLabel.setForeground(Color.CYAN);
        inputPanel.add(dietLabel, gbc);

        gbc.gridx = 1;
        dietComboBox = new JComboBox<>(new String[]{"Select", "Weight Gain", "Weight Loss", "Balanced"});
        dietComboBox.setBackground(new Color(50, 50, 50));
        dietComboBox.setForeground(Color.CYAN);
        dietComboBox.setBorder(boldBorder);
        inputPanel.add(dietComboBox, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        showPlanButton = new JButton("Show Meal Plan");
        showPlanButton.setBackground(new Color(0, 102, 204)); // Blue color
        showPlanButton.setForeground(Color.WHITE);
        showPlanButton.setBorder(boldBorder);
        inputPanel.add(showPlanButton, gbc);

        frame.add(inputPanel, BorderLayout.CENTER); // Place input panel at the center of frame

        // Table panel
        tableModel = new DefaultTableModel(new String[]{"Day", "Morning Meal", "Afternoon Meal", "Evening Meal", "Total Calories"}, 0);
        table = new JTable(tableModel);
        table.setBackground(new Color(40, 40, 40)); // Darker background for table
        table.setForeground(Color.CYAN);
        table.setGridColor(Color.DARK_GRAY);
        table.setSelectionBackground(new Color(0, 102, 204)); // Blue color for selection
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(new Color(0, 102, 204)); // Blue header
        table.getTableHeader().setForeground(Color.WHITE);
        table.setBorder(boldBorder);

        // Scroll pane for the table with dark background
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(750, 250));
        tableScrollPane.getViewport().setBackground(new Color(30, 30, 30));
        tableScrollPane.setBorder(boldBorder);

        JPanel tablePanel = new JPanel();
        tablePanel.setBackground(new Color(30, 30, 30));
        tablePanel.add(tableScrollPane);

        frame.add(tablePanel, BorderLayout.SOUTH); // Place table at the bottom

        // Connect to the database
        connectToDatabase();

        // Action listener to display meal plan based on selected diet
        showPlanButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String dietType = (String) dietComboBox.getSelectedItem();
                loadMealPlan(dietType);
            }
        });

        frame.setVisible(true);
    }

    private void connectToDatabase() {
        try {
            // Register JDBC driver and open a connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Nutrition", "root", "Anusivaaakpraz1@");
            System.out.println("Connected to the database");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadMealPlan(String dietType) {
        // Clear the table before loading new data
        tableModel.setRowCount(0);

        String tableName;
        switch (dietType) {
            case "Weight Gain":
                tableName = "weight_gain";
                break;
            case "Weight Loss":
                tableName = "weight_loss";
                break;
            case "Balanced":
                tableName = "balanced";
                break;
            default:
                JOptionPane.showMessageDialog(frame, "Please select a valid diet type", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }

        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM " + tableName;
            ResultSet resultSet = statement.executeQuery(query);

            // Populate table with data from the database
            while (resultSet.next()) {
                String day = resultSet.getString("day");
                String morningMeal = resultSet.getString("morning_meal");
                String afternoonMeal = resultSet.getString("afternoon_meal");
                String eveningMeal = resultSet.getString("evening_meal");
                int totalCalories = resultSet.getInt("total_calories");

                tableModel.addRow(new Object[]{day, morningMeal, afternoonMeal, eveningMeal, totalCalories});
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new NutritionAssistant();
    }
}

