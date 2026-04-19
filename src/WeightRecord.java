class WeightRecord extends HealthRecord {
    private double weight;

    public WeightRecord(String date, double weight) {
        super(date);
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toFile() {
        return "W," + getDate() + "," + weight;
    }

    @Override
    public String toString() {
        return "Weight | " + getDate() + " | " + weight + " kg";
    }
}