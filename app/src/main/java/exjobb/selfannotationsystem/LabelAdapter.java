package exjobb.selfannotationsystem;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Marten on 2017-02-23.
 */

public class LabelAdapter  extends ArrayAdapter<LabelWrapper> {

    private int mResourceId = 0;
    private RadioButton mSelectedRB;
    private int mSelectedPosition = -1;
    private List<LabelWrapper> labels;
    private int defaultID = -1;
    private LayoutInflater mLayoutInflater;
    private int activityID;
    private Context context;

    public LabelAdapter(Context context, int resource, List<LabelWrapper> labels, int defaultID, int acticityID) {
        super(context, resource, labels);
        Log.d("DEAFULT ID", defaultID + "");
        mResourceId = resource;
        this.labels = labels;
        this.context = context;
        this.defaultID = defaultID;
        this.activityID = acticityID;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(final int position, final View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if(view == null){
            view = mLayoutInflater.inflate(mResourceId, parent, false);
            holder = new ViewHolder();
            holder.radioBtn = (RadioButton)view.findViewById(R.id.radioButtonLabel);
            holder.radioBtn.setText(labels.get(position).getTextValue());
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }
        Log.d("DEAFULT", defaultID + "");
        Log.d("DEFAULT LABEL",(labels.get(position).getId() - 1) + " : "+ position);

        if(labels.get(position).getId() == defaultID){
            mSelectedPosition = position; // VÅR RAD
            mSelectedRB = holder.radioBtn;
        }

        holder.radioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position != mSelectedPosition && mSelectedRB != null){
                    mSelectedRB.setChecked(false);
                    Log.d("POSITION i dubbel if", position + "");
                }
                mSelectedPosition = position;
                mSelectedRB = (RadioButton)v;
                defaultID = labels.get(position).getId();
                Log.d("nu",""+defaultID);
                LifeLogActivity l = (LifeLogActivity)context;
                l.setLabel(activityID, labels.get(position).getId() );
            }
        });

        if(mSelectedPosition != position){
            holder.radioBtn.setChecked(false);
        }else{
            holder.radioBtn.setChecked(true);
            if(mSelectedRB != null && holder.radioBtn != mSelectedRB){
                mSelectedRB = holder.radioBtn;
            }
        }
        return view;
    }

    private class ViewHolder{
        RadioButton radioBtn;
    }
}
