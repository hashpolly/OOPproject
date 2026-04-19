abstract class HealthRecord {
    private String date;

    public HealthRecord(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public abstract String toFile();
}