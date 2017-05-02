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

import android.app.LoaderManager;
import android.app.SearchManager;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Earthquake>> {

    public static final String QUERY_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2016-04-25&endtime=2017-04-25&minfelt=0";

    //String key when creating Bundle
    public final String URL_STRING = "queryurl";
    //Integer value to give Loader ID
    private static final int EARTHQUAKE_LOADER_ID = 1;
    //String message for when array result returns empty
    private static final String EMPTY_LIST = "No earthquakes found.";
    //String message for when network connection failure
    private static final String NO_NETWORK_CONNECTION = "No internet connection.";
    //String Tag for Logging
    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private ArrayList<Earthquake> earthquakes;
    private EarthquakeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        Log.v(LOG_TAG, "onCreate called.");

        //For Initializing and executing EarthquakeAsyncTask to fetch Earthquake data
        //new EarthquakeAsyncTask().execute(QUERY_URL);

        //Ensuring Loading message is displaying
        showLoading();

        //Declaring the list and to be an empty list
        //Initializing the adapter to the ListView since when adding our fetch data to the adapter will auto update our ListView
        earthquakes = new ArrayList<Earthquake>();
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        adapter = new EarthquakeAdapter(EarthquakeActivity.this, earthquakes);
        earthquakeListView.setAdapter(adapter);

        //Set click event listener on List View for user taps on list items
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Earthquake selected = earthquakes.get(position);
                String query = selected.getURL();

                searchWeb(query);
            }

        });

        earthquakeListView.setEmptyView(findViewById(R.id.alt_text));


        if(hasInternetConnection()) {
            Log.v(LOG_TAG, "initLoader called.");
            getLoaderManager().initLoader(EARTHQUAKE_LOADER_ID, createUrlBundle(QUERY_URL), this);
        }
        else {
            hideLoading();
            setAltResultText(NO_NETWORK_CONNECTION);
        }
    }

    //Method to search String URL in Web browser through an Intent
    public void searchWeb(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    //Method to Update the UI with an Earthquake list
    public void updateUI(ArrayList<Earthquake> list) {
        earthquakes = list;
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        adapter = new EarthquakeAdapter(EarthquakeActivity.this, earthquakes);
        earthquakeListView.setAdapter(adapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Earthquake selected = earthquakes.get(position);
                String query = selected.getURL();

                searchWeb(query);
            }

        });
    }

    //Create Bundle contain URL String arg to pass to AsyncTask Loader
    public Bundle createUrlBundle(String url) {
        Bundle b = new Bundle();
        b.putString(URL_STRING, url);

        return b;
    }

    //Method to enable loading Views (visible)
    public void showLoading() {
        TextView loadingView = (TextView) findViewById(R.id.loading_text);
        loadingView.setVisibility(View.VISIBLE);

        ProgressBar progressView = (ProgressBar) findViewById(R.id.loading_bar);
        progressView.setVisibility(View.VISIBLE);
    }

    //Method to disable loading Views (invisible)
    public void hideLoading() {
        TextView loadingView = (TextView) findViewById(R.id.loading_text);
        loadingView.setVisibility(View.INVISIBLE);

        ProgressBar progressView = (ProgressBar) findViewById(R.id.loading_bar);
        progressView.setVisibility(View.INVISIBLE);
    }

    //Method to input String for Alternate Result View and enable visibility
    public void setAltResultText(String message) {
        TextView altTextView = (TextView)findViewById(R.id.alt_text);
        altTextView.setText(message);
        altTextView.setVisibility(altTextView.VISIBLE);
    }

    //Method to check if the device has acess to internet network connection
    public boolean hasInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected() && (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE || networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    @Override
    public Loader<ArrayList<Earthquake>> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader called.");

        return new EarthquakeLoader(EarthquakeActivity.this, args);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Earthquake>> loader, ArrayList<Earthquake> data) {
        Log.v(LOG_TAG, "onLoadFinished called");
        //Hide loading message when network connection is complete
        hideLoading();

        //Set empty list message to Empty TextView
        setAltResultText(EMPTY_LIST);

        adapter.clear();

        if(data != null && !data.isEmpty()) {
            //updateUI(data);
            //Adding new data to the ArrayList adapter will auto update the ListView
            adapter.addAll(data);

        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Earthquake>> loader) {
        Log.v(LOG_TAG, "onLoaderReset called.");
        adapter.clear();
    }
/*
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
*/


}
