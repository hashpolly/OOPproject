import javax.swing.*;
import java.awt.*;
import javax.swing.event.ListSelectionEvent;
import java.util.ArrayList;

public class MainGUI {

    private static ArrayList<HealthRecord> list = new ArrayList<>();
    private static DefaultListModel<String> model = new DefaultListModel<>();
    private static String currentRole = null;

    public static void main(String[] args) {
        currentRole = performLogin();
        if (currentRole == null) return;

        DatabaseManager.init();

        JFrame frame = new JFrame("Health Tracker - " + currentRole + " mode (SQLite)");
        frame.setSize(620, 520);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JList<String> listUI = new JList<>(model);
        JScrollPane scroll = new JScrollPane(listUI);

        JTextField dateField = new JTextField(10);
        JTextField valueField = new JTextField(12);

        String[] types = {"Weight", "BloodPressure", "Exercise"};
        JComboBox<String> typeBox = new JComboBox<>(types);

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton saveBtn = new JButton("Save to DB");
        JButton loadBtn = new JButton("Load from DB");

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        topPanel.add(dateField);
        topPanel.add(new JLabel("Value:"));
        topPanel.add(valueField);
        topPanel.add(new JLabel("Type:"));
        topPanel.add(typeBox);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(addBtn);
        bottomPanel.add(updateBtn);
        bottomPanel.add(deleteBtn);
        bottomPanel.add(saveBtn);
        bottomPanel.add(loadBtn);

        JLabel statusLabel = new JLabel("   Logged in as: " + currentRole);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));

        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(statusLabel, BorderLayout.WEST);

        if (!currentRole.equals("Admin")) {
            updateBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
        }

        addBtn.addActionListener(e -> addRecord(frame, dateField, valueField, typeBox));
        updateBtn.addActionListener(e -> updateRecord(frame, listUI, dateField, valueField));
        deleteBtn.addActionListener(e -> deleteRecord(listUI));

        saveBtn.addActionListener(e -> {
            DatabaseManager.save(list);
            JOptionPane.showMessageDialog(frame, "✅ Сохранено в базу данных (health.db)");
        });

        loadBtn.addActionListener(e -> {
            list.clear();
            model.clear();
            for (HealthRecord r : DatabaseManager.load()) {
                list.add(r);
                model.addElement(r.toString());
            }
            JOptionPane.showMessageDialog(frame, "✅ Загружено из базы данных");
        });


        listUI.addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                int selected = listUI.getSelectedIndex();
                if (selected >= 0) {
                    HealthRecord r = list.get(selected);
                    dateField.setText(r.getDate());
                    if (r instanceof WeightRecord wr) {
                        typeBox.setSelectedItem("Weight");
                        valueField.setText(String.valueOf(wr.getWeight()));
                    } else if (r instanceof BloodPressureRecord bpr) {
                        typeBox.setSelectedItem("BloodPressure");
                        valueField.setText(bpr.getPressure());
                    } else if (r instanceof ExerciseRecord er) {
                        typeBox.setSelectedItem("Exercise");
                        valueField.setText(er.getExercise());
                    }
                }
            }
        });

        list.clear();
        model.clear();
        for (HealthRecord r : DatabaseManager.load()) {
            list.add(r);
            model.addElement(r.toString());
        }

        frame.setVisible(true);
    }

    private static String performLogin() {
        while (true) {
            JTextField userField = new JTextField(12);
            JPasswordField passField = new JPasswordField(12);

            JPanel loginPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            loginPanel.add(new JLabel("Username:"));
            loginPanel.add(userField);
            loginPanel.add(new JLabel("Password:"));
            loginPanel.add(passField);

            int result = JOptionPane.showConfirmDialog(null, loginPanel,
                    "Health Tracker - Login", JOptionPane.OK_CANCEL_OPTION);

            if (result != JOptionPane.OK_OPTION) return null;

            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if ("admin".equals(username) && "admin".equals(password)) {
                return "Admin";
            } else if ("user".equals(username) && "user".equals(password)) {
                return "User";
            } else {
                JOptionPane.showMessageDialog(null,
                        "Неверный логин или пароль!\n\nadmin / admin\nuser / user",
                        "Ошибка входа", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void addRecord(JFrame frame, JTextField dateField, JTextField valueField, JComboBox<String> typeBox) {
        String d = dateField.getText().trim();
        String val = valueField.getText().trim();
        String type = (String) typeBox.getSelectedItem();

        if (d.isEmpty() || val.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Заполните все поля!");
            return;
        }

        try {
            java.time.LocalDate.parse(d);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Неверный формат даты! Используйте YYYY-MM-DD");
            return;
        }

        try {
            HealthRecord r = null;

            if ("Weight".equals(type)) {
                double w = Double.parseDouble(val);
                if (w <= 0 || w > 500) throw new Exception();
                r = new WeightRecord(d, w);
            } else if ("BloodPressure".equals(type)) {
                if (!val.matches("\\d{2,3}/\\d{2,3}")) throw new Exception();
                r = new BloodPressureRecord(d, val);
            } else if ("Exercise".equals(type)) {
                if (!val.matches("[a-zA-Z ]{3,30}")) throw new Exception();
                r = new ExerciseRecord(d, val);
            }

            list.add(r);
            model.addElement(r.toString());

            dateField.setText("");
            valueField.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Ошибка ввода! Проверьте данные.");
        }
    }

    private static void updateRecord(JFrame frame, JList<String> listUI, JTextField dateField, JTextField valueField) {
        int i = listUI.getSelectedIndex();
        if (i < 0) return;

        String d = dateField.getText().trim();
        String val = valueField.getText().trim();

        if (d.isEmpty() || val.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Заполните дату и значение!");
            return;
        }

        try {
            HealthRecord old = list.get(i);
            HealthRecord updated;

            if (old instanceof WeightRecord) {
                updated = new WeightRecord(d, Double.parseDouble(val));
            } else if (old instanceof BloodPressureRecord) {
                updated = new BloodPressureRecord(d, val);
            } else {
                updated = new ExerciseRecord(d, val);
            }

            list.set(i, updated);
            model.set(i, updated.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Ошибка обновления!");
        }
    }

    private static void deleteRecord(JList<String> listUI) {
        int i = listUI.getSelectedIndex();
        if (i >= 0) {
            list.remove(i);
            model.remove(i);
        }
    }
}