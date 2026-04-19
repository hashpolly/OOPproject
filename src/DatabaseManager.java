import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:health.db";

    public static void init() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS records (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "type TEXT NOT NULL, " +
                    "date TEXT NOT NULL, " +
                    "value TEXT NOT NULL)";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void save(ArrayList<HealthRecord> records) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            conn.setAutoCommit(false);

            Statement stmt = conn.createStatement();

            stmt.execute("DELETE FROM records");

            for (HealthRecord r : records) {
                String typeCode = "";
                String val = "";

                if (r instanceof WeightRecord) {
                    typeCode = "W";
                    val = String.valueOf(((WeightRecord) r).getWeight());
                } else if (r instanceof BloodPressureRecord) {
                    typeCode = "B";
                    val = ((BloodPressureRecord) r).getPressure();
                } else if (r instanceof ExerciseRecord) {
                    typeCode = "E";
                    val = ((ExerciseRecord) r).getExercise();
                }

                String sql = "INSERT INTO records (type, date, value) VALUES " +
                        "('" + typeCode + "', '" + r.getDate() + "', '" + val + "')";
                stmt.execute(sql);
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<HealthRecord> load() {
        ArrayList<HealthRecord> records = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM records")) {

            while (rs.next()) {
                String type = rs.getString("type");
                String date = rs.getString("date");
                String value = rs.getString("value");

                try {
                    if ("W".equals(type)) {
                        records.add(new WeightRecord(date, Double.parseDouble(value)));
                    } else if ("B".equals(type)) {
                        records.add(new BloodPressureRecord(date, value));
                    } else if ("E".equals(type)) {
                        records.add(new ExerciseRecord(date, value));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }
}