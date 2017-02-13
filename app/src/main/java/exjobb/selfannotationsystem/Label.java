package exjobb.selfannotationsystem;

import java.util.Date;

/**
 * Created by Marten on 2017-02-07.
 */

public class Label {

    private String activityType, activityLabelOptions;
    private int activityValue;
    private String time;


    public Label(String activityType, int activityValue, String time) {
        this.activityType = activityType;
        this.activityValue = activityValue;
        this.time = time;
    }

    public Label(String activityType){
        this.activityType = activityType;
    }

    public void setValue(int value) {
        this.activityValue = value;
    }
    public int getValue() {
        if(activityValue != 0) {
            return this.activityValue;
        }else{
            return 0;
        }

    }
    public String getActivityType() {
        return activityType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }
}
