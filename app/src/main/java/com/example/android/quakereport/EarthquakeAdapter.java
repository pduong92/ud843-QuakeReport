package com.example.android.quakereport;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.text.DecimalFormat;

/**
 * Created by phduo on 4/8/2017.
 */

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    private static final String LOCATION_SEPERATOR = " of ";
    private static DecimalFormat formatter = new DecimalFormat("0.0");

    public EarthquakeAdapter(Activity content, List<Earthquake> objects) {
        super(content, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View earthquakeView = convertView;
        if(earthquakeView == null) {
            earthquakeView = LayoutInflater.from(getContext()).inflate(R.layout.earthquake_list_item, parent, false);
        }

        Earthquake earthquake = getItem(position);

        double currentMag = earthquake.getMagnitude();

        TextView magnitudeView = (TextView)earthquakeView.findViewById(R.id.magnitude);
        magnitudeView.setText(formatMagnitude(currentMag));

        GradientDrawable magnitudeCircle = (GradientDrawable)magnitudeView.getBackground();
        magnitudeCircle.setColor(setMagnitudeColor(currentMag));

        TextView distanceView = (TextView)earthquakeView.findViewById(R.id.distance);

        TextView locationView = (TextView)earthquakeView.findViewById(R.id.location);

        String[] locationComponents = splitLocation(earthquake.getLocation());
        distanceView.setText(locationComponents[0]);
        locationView.setText(locationComponents[1]);

        TextView dateView = (TextView)earthquakeView.findViewById(R.id.date);
        dateView.setText(earthquake.getDate());

        TextView timeView = (TextView)earthquakeView.findViewById(R.id.time);
        timeView.setText(earthquake.getTime());
/*
//Attempt to set MaxWidth for Location TextView to avoid long locations from overlapping the date/time View

        RelativeLayout rootLayout = (RelativeLayout)earthquakeView.findViewById(R.id.earthquake_list_layout);

        int rootWidth = rootLayout.getMeasuredWidth();
        int magWidth = magnitudeView.getMeasuredWidth();
        int dateWidth = dateView.getMeasuredWidth();

        int maxLocationWidth = rootLayout.getMeasuredWidth() - magnitudeView.getMeasuredWidth() - dateView.getMeasuredWidth();
        //locationView.setMaxWidth(maxLocationWidth);

        Log.d("DEBUG WIDTH", "maxLocationWidth: " + maxLocationWidth + ", rootWidth: " + rootWidth + ", magWidth: " + magWidth + ", dateWidth: " + dateWidth);
*/

        return earthquakeView;
    }

    public int setMagnitudeColor(double magnitude) {
        int colorID;
        int truncated = (int)Math.floor(magnitude);
        int[] colorIDs = {R.color.magnitude1,
                R.color.magnitude1,
                R.color.magnitude2,
                R.color.magnitude3,
                R.color.magnitude4,
                R.color.magnitude5,
                R.color.magnitude6,
                R.color.magnitude7,
                R.color.magnitude8,
                R.color.magnitude9,
                R.color.magnitude10};
        colorID = colorIDs[truncated];

        return ContextCompat.getColor(getContext(), colorID);
    }

    public String[] splitLocation(String location) {
        String[] components = new String[2];

        if(location.contains("of")) {
            components = location.split(LOCATION_SEPERATOR);
            components[0] = (components[0] + LOCATION_SEPERATOR).trim().toUpperCase();
        }

        /*
        int index = location.indexOf("of");
        if(index != -1) {
            components[0] = location.substring(0, index + 2).trim().toUpperCase();
            components[1] = location.substring(index + 2).trim();
        }
        */
        else {
            components[0] = "";
            components[1] = location;
        }

        return components;
    }

    public String formatMagnitude(double magnitude) {
        return formatter.format(magnitude);
    }

}
