package exjobb.selfannotationsystem;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;

import java.util.List;

/**
 * Created by Marten on 2017-02-23.
 */

public class LabelAdapter  extends ArrayAdapter<String> {

    private Context context;
    private List<String> labels;

    public LabelAdapter(Context context, int resource, List<String> labels) {
        super(context, resource, labels);
        this.labels = labels;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View labelOptionsView = inflater.inflate(R.layout.radio_row, parent, false);

        RadioButton radioButton = (RadioButton)labelOptionsView.findViewById(R.id.radioButtonLabel);
        radioButton.setText(labels.get(position));

        return labelOptionsView;

    }
}
