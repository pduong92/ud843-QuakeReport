package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.sql.Date;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by phduo on 4/8/2017.
 */

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getName();


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Earthquake> fetchEarthquakes(String urlQuery) {
        Log.v(LOG_TAG, "fetchEqarthquakes called.");

        //Forcing the network task to pause to have the LoadingView display longer
        try {
            Thread.sleep(2000 /*milliseconds*/);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if(TextUtils.isEmpty(urlQuery) || urlQuery == null)
            return null;

        String jsonResponse = "";
        URL url = createURL(urlQuery);

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Error reading stream", e);
        }

        ArrayList<Earthquake> earthquakes = extractEarthquakeList(jsonResponse);

        // Return the list of earthquakes
        return earthquakes;
    }

    public static URL createURL(String urlQuery) {
        URL url = null;
        try{
            url = new URL(urlQuery);
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "Error while creating URL link.", e);
        }

        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        InputStream inStream = null;
        HttpsURLConnection urlConnection = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inStream = urlConnection.getInputStream();
                jsonResponse = readInputStream(inStream);
            }
        }catch(IOException e) {
            Log.e(LOG_TAG, "Error retrieving JSON response", e);
            Log.getStackTraceString(e);
        }
        finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(inStream != null) {
                inStream.close();
            }
        }
        return jsonResponse;
    }

    public static String readInputStream(InputStream inputstream) throws IOException {
        StringBuilder result = new StringBuilder();

        if(inputstream != null) {
            InputStreamReader isreader = new InputStreamReader(inputstream, Charset.forName("UTF-8"));
            BufferedReader breader = new BufferedReader(isreader);
            String line = breader.readLine();
            while(line != null) {
                result.append(line);
                line = breader.readLine();
            }
        }

        return result.toString();
    }

    public static ArrayList<Earthquake> extractEarthquakeList(String queryJSON) {
        if(TextUtils.isEmpty(queryJSON) || queryJSON == null) {
            return null;
        }

        ArrayList<Earthquake> earthquakeList = new ArrayList<Earthquake>();
        try {
            JSONObject root = new JSONObject(queryJSON);
            JSONArray features = root.optJSONArray("features");

            for(int i = 0; i < features.length(); i++) {
                earthquakeList.add(extractEarthquake(features,i));
            }
        } catch(JSONException e) {
            Log.e(LOG_TAG, "Error parsing the JSON results", e);
        }
        return earthquakeList;
    }

    public static Earthquake extractEarthquake(JSONArray jarray, int index) {
        if(index < 0 || index > jarray.length() || jarray.length() == 0) {
            return null;
        }

        Earthquake result = null;
        try{
            JSONObject feature = jarray.optJSONObject(index);
            JSONObject properties = feature.optJSONObject("properties");
            double mag = properties.optDouble("mag");
            String place = properties.optString("place");
            long time = properties.getLong("time");
            String url = properties.getString("url");

            result = new Earthquake(mag, place, time, url);
        }catch(JSONException e) {
            Log.e(LOG_TAG, "Problem extracting JSON properties", e);
        }

        return result;
    }
}
