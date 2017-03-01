package exjobb.selfannotationsystem;

import android.util.Log;

public class ActivityWrapper {

    private int distance;
    private String activityType;
    private int steps;
    private String date;
    private String time;
    private int labelID = -1;
    private int activityID;

    public ActivityWrapper(String date, String time, int steps, int distance, String activityType, int labelID) {
        this.activityType = activityType;
        this.steps = steps;
        this.date = date;
        this.distance = distance;
        this.time = time;
        this.labelID = labelID;
    }

    public ActivityWrapper(String date, String time, int steps, int distance, String activityType, int labelID, int activityID) {
        this(date,time,steps,distance,activityType,labelID);
        this.activityID = activityID;
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

    public int getLabelID() { return labelID; }

    public int getActivityID() {
        return activityID;
    }
}
