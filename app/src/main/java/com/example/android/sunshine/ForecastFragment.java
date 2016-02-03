package com.example.android.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    final String LOG_TAG = ForecastFragment.class.getSimpleName();
    //                              Format  Unit     Days   AppId

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] forecastArray = {
                "Today - Sunny - 88/63",
                "Tomorrow - Cloudy - 76/65",
                "02/02 - Rainy - 69/53",
                "03/02 - Rainy - 70/51",
                "04/02 - Sunny - 86/61",
                "05/02 - Cloudy - 88/45",
                "06/02 - Sunny - 92/67"
        };

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(forecastArray));

        ArrayAdapter<String> mForecastAdapter =
                new ArrayAdapter<String>(
                        this.getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        weekForecast);

        ListView mListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        mListView.setAdapter(mForecastAdapter);

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            final String postalCode = "09047";
            final String city = "Selargius";
            final String countryCode = "IT";
            final String[] forecastParameters = new String[] {postalCode, city, countryCode};
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute(forecastParameters);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class FetchWeatherTask extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String...params) {
            final String weatherJsonStr = "{\"city\":{\"id\":6537912,\"name\":\"Selargius\",\"coord\":{\"lon\":9.16755,\"lat\":39.256641},\"country\":\"IT\",\"population\":0},\"cod\":\"200\",\"message\":0.0125,\"cnt\":7,\"list\":[{\"dt\":1454497200,\"temp\":{\"day\":286.32,\"min\":285.43,\"max\":286.32,\"night\":285.43,\"eve\":286.32,\"morn\":286.32},\"pressure\":1026.66,\"humidity\":89,\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03n\"}],\"speed\":10.92,\"deg\":325,\"clouds\":44},{\"dt\":1454583600,\"temp\":{\"day\":287.13,\"min\":284.34,\"max\":287.13,\"night\":284.46,\"eve\":285.4,\"morn\":284.34},\"pressure\":1031.5,\"humidity\":81,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":7.81,\"deg\":324,\"clouds\":0},{\"dt\":1454670000,\"temp\":{\"day\":287.78,\"min\":283.32,\"max\":287.78,\"night\":283.32,\"eve\":286.75,\"morn\":286.23},\"pressure\":1031.28,\"humidity\":86,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":6.99,\"deg\":308,\"clouds\":88},{\"dt\":1454756400,\"temp\":{\"day\":288.25,\"min\":282.41,\"max\":288.27,\"night\":285.02,\"eve\":287.42,\"morn\":282.41},\"pressure\":1030.55,\"humidity\":86,\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\",\"icon\":\"02d\"}],\"speed\":6.46,\"deg\":139,\"clouds\":24},{\"dt\":1454842800,\"temp\":{\"day\":289.78,\"min\":284.54,\"max\":289.78,\"night\":285.52,\"eve\":286.97,\"morn\":284.54},\"pressure\":1023.01,\"humidity\":78,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":6.3,\"deg\":221,\"clouds\":88,\"rain\":1.35},{\"dt\":1454929200,\"temp\":{\"day\":289.14,\"min\":285.95,\"max\":289.14,\"night\":288.39,\"eve\":288.21,\"morn\":285.95},\"pressure\":1023.54,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":7.82,\"deg\":259,\"clouds\":30,\"rain\":0.76},{\"dt\":1455015600,\"temp\":{\"day\":288.23,\"min\":286.86,\"max\":288.23,\"night\":286.86,\"eve\":286.89,\"morn\":287.74},\"pressure\":1026.73,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":15.01,\"deg\":309,\"clouds\":2}]}";

            try {
                final JSONObject reader = new JSONObject(weatherJsonStr);
                final JSONArray forecastList = reader.getJSONArray("list");
                final JSONObject forecastDay = forecastList.getJSONObject(0);
                final JSONObject forecastTemp = forecastDay.getJSONObject("temp");
                Double maxTemp = forecastTemp.getDouble("max");
            }
            catch (JSONException e) {
                Log.e(LOG_TAG, "JSONException found");
            }

            return null;
        }

        protected Void doInBackgroundTRUE(String... params) {
            //These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //Will contain the raw JSON response as a string;
            String forecastJsonStr = null;

            final String units = "metric";
            final String format = "json";
            final String appid = "9779acdcfa230154081c5d5f1ef29c2f";
            final String days = "7";

            try {
                //Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=Cagliari&units=metric&cnt=7&appid=9779acdcfa230154081c5d5f1ef29c2f");
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "appid";

                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder = Uri.parse(FORECAST_BASE_URL).buildUpon();
                uriBuilder.appendQueryParameter(QUERY_PARAM, params[1]).
                        appendQueryParameter(UNITS_PARAM, units).
                        appendQueryParameter(FORMAT_PARAM, format).
                        appendQueryParameter(DAYS_PARAM, days).
                        appendQueryParameter(APPID_PARAM, appid);
                URL url = new URL(uriBuilder.build().toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    //nothing to do
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    //Stream was empty. No point in parsing.
                    return null;
                }

                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Forecast JSON String: " + forecastJsonStr);
            } catch (IOException e)

            {
                Log.e(LOG_TAG, "Error ", e);
                //if the code didn't successfully getthe data, there's no point in
                //attempting to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return null;
        }
    }

}