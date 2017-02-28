package exjobb.selfannotationsystem;

/**
 * Created by Marten on 2017-02-28.
 */

public class LabelWrapper {

    private int id;
    private String textValue;

    public LabelWrapper(int id, String textValue){
        this.id = id;
        this.textValue = textValue;
    }

    public int getId() {
        return id;
    }

    public String getTextValue() {
        return textValue;
    }
}
