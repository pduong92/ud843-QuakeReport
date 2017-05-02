package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by phduo on 4/30/2017.
 */

public class EarthquakeLoader extends AsyncTaskLoader<ArrayList<Earthquake>> {

    private final String URL_STRING = "queryurl";

    private final String LOG_TAG = EarthquakeLoader.class.getName();

    private String url;


    public EarthquakeLoader(Context content, Bundle args){
        super(content);

        this.url = args.getString(URL_STRING);
    }

    @Override
    protected void onStartLoading() {
        Log.v(LOG_TAG, "onStartLoading called.");
        forceLoad();
    }

    @Override
    public ArrayList<Earthquake> loadInBackground() {
        Log.v(LOG_TAG, "loadInBackground called.");
        if(TextUtils.isEmpty(url) || url == null) {
            return null;
        }

        ArrayList<Earthquake> result = QueryUtils.fetchEarthquakes(url);

        return result;
    }
/*
    @Override
    public void deliverResult(ArrayList<Earthquake> result) {
            super.deliverResult(result);
    }
    */
}
