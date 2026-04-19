class BloodPressureRecord extends HealthRecord {
    private String pressure;

    public BloodPressureRecord(String date, String pressure) {
        super(date);
        this.pressure = pressure;
    }

    public String getPressure() {
        return pressure;
    }

    @Override
    public String toFile() {
        return "B," + getDate() + "," + pressure;
    }

    @Override
    public String toString() {
        return "BP | " + getDate() + " | " + pressure;
    }
}