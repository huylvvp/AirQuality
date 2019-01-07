package com.kevinlu.airquality;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentAirQualityFragment extends Fragment {

    private final String url = "http://api.airvisual.com/v2/nearest_city?key=ag85mSsqaj2Y24HvQ";

    ImageView airQualityPicture;
    TextView cityName, countryName, cityTimeStamp, cityAQI, cityAQIRating;
    LinearLayout currentPanel;
    ProgressBar currentProgressBar;
    SwipeRefreshLayout pullToRefresh;

    static CurrentAirQualityFragment instance;

    public static CurrentAirQualityFragment getInstance() {
        if (instance == null) {
            instance = new CurrentAirQualityFragment();
        }
        return instance;
    }

    public CurrentAirQualityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_current_air_quality, container, false);

        airQualityPicture = itemView.findViewById(R.id.airquality_picture);
        cityName = itemView.findViewById(R.id.cityName);
        countryName = itemView.findViewById(R.id.countryName);
        cityTimeStamp = itemView.findViewById(R.id.cityTimestamp);
        cityAQI = itemView.findViewById(R.id.cityAQI);
        cityAQIRating = itemView.findViewById(R.id.cityAQIRating);

        currentPanel = itemView.findViewById(R.id.current_panel);
        currentProgressBar = itemView.findViewById(R.id.current_progress_bar);
        pullToRefresh = itemView.findViewById(R.id.current_swipeRefresh);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(), "refreshed eh", Toast.LENGTH_SHORT).show();
                //getCurrentAirQualityData();
                pullToRefresh.setRefreshing(false);
            }
        });

        getCurrentAirQualityData();

        return itemView;
    }

    private void getCurrentAirQualityData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        //Request a string response from the provided URL, create a new StringRequest object
        /*
         * @param response - This is the response (JSON file) from the API
         */
        //This is what will happen when there is an error during the response
        /*
         * @param error - This is the error that Volley encountered
         */
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    //Using Gson to turn JSON to Java object of Station
                    //Create new GsonBuilder and Gson objects
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    //Create a new Station object and use Gson to deserialize JSON data
                    //into the Station object
                    Station station = gson.fromJson(response, Station.class);
                    //Call the loadCurrentData method to display the data
                    loadCurrentData(station);

                    //Write the API response data to the log console
                    Log.d("API RESPONSE", response);
                }, error -> {
            //Write the error from Volley to the log console
            Log.d("VOLLEY ERROR", error.toString());
        });
        //Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }

    private void loadCurrentData(Station station) {
        cityName.setText(station.getData().getCity());
        countryName.setText(station.getData().getCountry());
        cityTimeStamp.setText(decodeTimestamp(station.getData().getCurrent().getPollution().getTs()));
        cityAQI.setText(station.getData().getCurrent().getPollution().getAqius().toString());
        cityAQIRating.setText(rankAQIUS(station.getData().getCurrent().getPollution().getAqius()));

        currentPanel.setVisibility(View.VISIBLE);
        currentProgressBar.setVisibility(View.GONE);
    }

    private String decodeTimestamp(String timestamp) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH);
        LocalDate date = LocalDate.parse(timestamp, inputFormatter);
        String formattedDate = outputFormatter.format(date);
        return formattedDate;
    }

    private String rankAQIUS(int aqius) {
        if (aqius >= 0 && aqius <= 50) {
            return "Good";
        } else if (aqius >= 51 && aqius <= 100) {
            return "Moderate";
        } else if (aqius >= 101 && aqius <= 150) {
            return "Unhealthy for Sensitive Groups";
        } else if (aqius >= 151 && aqius <= 200) {
            return "Unhealthy";
        } else if (aqius >= 201 && aqius <= 300) {
            return "Very Unhealthy";
        } else if (aqius >= 301) {
            return "Hazardous";
        } else {
            return "ERROR";
        }
    }
}
