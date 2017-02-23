package exjobb.selfannotationsystem;

public class ActivityWrapper {

    private int distance;
    private String activityType;
    private int steps;
    private String date;
    private String time;
    private String labelID;

    public ActivityWrapper(String date, String time, int steps, int distance, String activityType) {
        this.activityType = activityType;
        this.steps = steps;
        this.date = date;
        this.distance = distance;
        this.time = time;
    }

    public ActivityWrapper(String date, String time, int steps, int distance, String activityType, String label) {
        this(date, time, steps, distance, activityType);
        this.labelID = label;
    }

    public int getSteps() {
        return steps;
    }

    public String getActivityType() {
        return activityType;
    }

    public String getDate() {
        return date;
    }

    public int getDistance() {
        return distance;
    }

    public String getTime() {
        return time;
    }

    public String getLabel() { return labelID; }
}
