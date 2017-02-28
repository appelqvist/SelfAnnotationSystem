package exjobb.selfannotationsystem;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Layout;
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

public class LabelAdapter  extends ArrayAdapter<String> {

    private int mResourceId = 0;
    private RadioButton mSelectedRB;
    private int mSelectedPosition = -1;
    private Context context;
    private List<String> labels;
    private LayoutInflater mLayoutInflater;

    public LabelAdapter(Context context, int resource, List<String> labels) {
        super(context, resource, labels);
        mResourceId = resource;
        this.labels = labels;
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if(view == null){
            view = mLayoutInflater.inflate(mResourceId, parent, false);
            holder = new ViewHolder();
            holder.radioBtn = (RadioButton)view.findViewById(R.id.radioButtonLabel);
            holder.radioBtn.setText(labels.get(position));
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }

        holder.radioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position != mSelectedPosition && mSelectedRB != null){
                    mSelectedRB.setChecked(false);
                }
                mSelectedPosition = position;
                mSelectedRB = (RadioButton)v;
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

   /* @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View labelOptionsView = inflater.inflate(R.layout.radio_row, parent, false);

        RadioButton radioButton = (RadioButton)labelOptionsView.findViewById(R.id.radioButtonLabel);
        radioButton.setText(labels.get(position));

        return labelOptionsView;

    }
    */

}

/*

private class MyAdapter extends ArrayAdapter<String>{



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if(view == null){

            view = mLayoutInflater.inflate(mResourceId, parent, false);
            holder = new ViewHolder();

            holder.name = (TextView)view.findViewById(R.id.text);
            holder.radioBtn = (RadioButton)view.findViewById(R.id.radioButton1);

            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }


        holder.radioBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if(position != mSelectedPosition && mSelectedRB != null){
                    mSelectedRB.setChecked(false);
                }

                mSelectedPosition = position;
                mSelectedRB = (RadioButton)v;
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




        holder.name.setText(getItem(position));


        return view;
    }

    private class ViewHolder{
        TextView        name;
        RadioButton     radioBtn;
    }
}


 */
