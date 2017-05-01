/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String QUERY_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2014-04-01&endtime=2017-04-25&minfelt=0&minmagnitude=5";

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private ArrayList<Earthquake> earthquakes;
    private EarthquakeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Create a fake list of earthquake locations.
        /*
        earthquakes = new ArrayList<Earthquake>();
        earthquakes.add(new Earthquake(7.2, "San Francisco", "2016-02-02"));
        earthquakes.add(new Earthquake(6.1, "London", "2015-07-20"));
        earthquakes.add(new Earthquake(3.9, "Tokyo", "2014-11-10"));
        earthquakes.add(new Earthquake(5.4, "Mexico City", "2014-05-03"));
        earthquakes.add(new Earthquake(2.8, "Moscow", "2013-01-31"));
        earthquakes.add(new Earthquake(4.9, "Rio de Janeiro", "2012-08-19"));
        earthquakes.add(new Earthquake(1.6, "Paris", "2011-10-30"));
        */
    /*    earthquakes = QueryUtils.fetchEarthquakes(QUERY_URL);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
        EarthquakeAdapter adapter = new EarthquakeAdapter(EarthquakeActivity.this, earthquakes);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                        Earthquake selected = earthquakes.get(position);
                        String query = selected.getURL();

                        searchWeb(query);
                    }

        }); */

        new EarthquakeAsyncTask().execute(QUERY_URL);
    }

    public void searchWeb(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void updateUI(ArrayList<Earthquake> list) {
        earthquakes = list;
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        adapter = new EarthquakeAdapter(EarthquakeActivity.this, list);
        earthquakeListView.setAdapter(adapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Earthquake selected = earthquakes.get(position);
                String query = selected.getURL();

                searchWeb(query);
            }

        });
    }

    private class EarthquakeAsyncTask extends AsyncTask<String, Void, ArrayList<Earthquake>> {
        @Override
        protected ArrayList<Earthquake> doInBackground(String... Urls) {
            String url = Urls[0];
            if(TextUtils.isEmpty(url) || url == null) {
                return null;
            }

            ArrayList<Earthquake> result = QueryUtils.fetchEarthquakes(url);

            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Earthquake> list) {
            if(list != null) {
                updateUI(list);
            }

        }
    }
}
