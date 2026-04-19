class ExerciseRecord extends HealthRecord {
    private String exercise;

    public ExerciseRecord(String date, String exercise) {
        super(date);
        this.exercise = exercise;
    }

    public String getExercise() {
        return exercise;
    }

    @Override
    public String toFile() {
        return "E," + getDate() + "," + exercise;
    }

    @Override
    public String toString() {
        return "Exercise | " + getDate() + " | " + exercise;
    }
}