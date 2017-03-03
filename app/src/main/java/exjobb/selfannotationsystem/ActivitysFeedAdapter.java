package exjobb.selfannotationsystem;

import android.content.Context;
import android.util.Log;
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

public class ActivitysFeedAdapter extends ArrayAdapter<ActivityWrapper> {

    private Context context;
    private List<ActivityWrapper> feeds;

    public ActivitysFeedAdapter(Context context, int textViewResource, List<ActivityWrapper> objects) {
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
        ImageView imgView = (ImageView) rowView.findViewById(R.id.imageViewActivity);

        if(feeds.get(position).getActivityType().equals("walk")) {
            activityTypeTextView.setText(feeds.get(position).getActivityType().toUpperCase());
            activityValueTextView.setText(String.valueOf("Steg: " + feeds.get(position).getSteps() +"\nDist: " +
                    (double)feeds.get(position).getDistance() /1000 + " km"));
            activityTypeTextView.setTextColor(getContext().getResources().getColor(R.color.colortwitter));
            imgView.setImageResource(R.mipmap.walk);
            timeTextView.setText("Klockan " + feeds.get(position).getTime());
        }
        else if(feeds.get(position).getActivityType().equals("run")) {
            activityTypeTextView.setText(feeds.get(position).getActivityType().toUpperCase());
            activityValueTextView.setText(String.valueOf("Steg: " + feeds.get(position).getSteps() +"\nDist: " +
                    (double)feeds.get(position).getDistance() /1000 + " km"));
            activityTypeTextView.setTextColor(getContext().getResources().getColor(R.color.colortwitter));
            imgView.setImageResource(R.mipmap.runner);
            timeTextView.setText("Klockan " + feeds.get(position).getTime());
        }
        else if(feeds.get(position).getActivityType().equals("cycle")) {
            activityTypeTextView.setText(feeds.get(position).getActivityType().toUpperCase());
            activityValueTextView.setText(String.valueOf("Steg: " + feeds.get(position).getSteps() +"\nDist: " +
                    (double)feeds.get(position).getDistance() /1000 + " km"));
            activityTypeTextView.setTextColor(getContext().getResources().getColor(R.color.colortwitter));
            imgView.setImageResource(R.mipmap.cycling);
            timeTextView.setText("Klockan " + feeds.get(position).getTime());
        }

        return rowView;
    }
}
