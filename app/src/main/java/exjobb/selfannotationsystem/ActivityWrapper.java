package exjobb.selfannotationsystem;

public class ActivityWrapper {

    private int distance;
    private String activityType;
    private int steps;
    private String date;
    private String time;


    public ActivityWrapper(String activityType, int steps, String date, String time, int distance) {
        this.activityType = activityType;
        this.steps = steps;
        this.date = date;
        this.distance = distance;
        this.time = time;
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
}
