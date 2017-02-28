package exjobb.selfannotationsystem;

import android.util.Log;

/**
 * Created by Marten on 2017-02-28.
 */

public class LabelWrapper {

    private int id;
    private String textValue;

    public LabelWrapper(int id, String textValue){
        this.id = id;
        this.textValue = textValue;
        Log.d("LABEL SKA INTE 0 ÄR : ", id + " ");
    }

    public int getId() {
        return id;
    }

    public String getTextValue() {
        return textValue;
    }
}
