package exjobb.selfannotationsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Marten on 2017-02-07.
 */

public class ActivitysFeedAdapter extends ArrayAdapter<Label> {

    private Context context;
    private List<Label> feeds;

    public ActivitysFeedAdapter(Context context, int textViewResource, List<Label> objects) {
        super(context, textViewResource, objects);
        this.feeds = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_view, parent, false);

        TextView activityValueTextView = (TextView) rowView.findViewById(R.id.tvActivityValue);
        TextView activityTypeTextView = (TextView) rowView.findViewById(R.id.tvActivityType);
        TextView timeTextView = (TextView) rowView.findViewById(R.id.tvTime);

        activityTypeTextView.setText(feeds.get(position).getActivityType().toUpperCase());
        activityValueTextView.setText(String.valueOf(feeds.get(position).getValue()));
        activityTypeTextView.setTextColor(getContext().getResources().getColor(R.color.colortwitter));
        timeTextView.setText(feeds.get(position).getTime());


//        if(feeds.get(position).getActivityType().equals("steps")){
//            activityTypeTextView.setText(feeds.get(position).getActivityType().toUpperCase());
//            activityValueTextView.setText(String.valueOf(feeds.get(position).getValue()));
//            activityTypeTextView.setTextColor(getContext().getResources().getColor(R.color.colortwitter));
//        }
//        else if(feeds.get(position).getActivityType().equals("weight")){
//            activityTypeTextView.setText(feeds.get(position).getActivityType().toUpperCase());
//            activityValueTextView.setText(String.valueOf(feeds.get(position).getValue()));
//            activityTypeTextView.setTextColor(getContext().getResources().getColor(R.color.colorfacebook));
//        }


        return rowView;
    }
}
