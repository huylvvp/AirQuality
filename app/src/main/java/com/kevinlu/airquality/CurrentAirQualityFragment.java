package com.kevinlu.airquality;


import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * This Fragment is part of the MainActivity class
 * It is 1 of the 2 tabs available for use.
 * This Fragment displays air quality information based
 * on the user's IP address location.
 */
public class CurrentAirQualityFragment extends Fragment {

    private final String url = "http://api.airvisual.com/v2/nearest_city?key=5X5FwBMHiD2DDKWBf";
    //private final String url = "http://api.airvisual.com/v2/nearest_city?key=5zbAzdPBu2RftKbus";
    //private final String url = "http://api.airvisual.com/v2/nearest_city?key=ag85mSsqaj2Y24HvQ";

    ImageView airQualityPicture;
    TextView cityName, countryName, cityTimeStamp, cityAQI, cityAQIRating;
    LinearLayout currentPanel;
    ProgressBar currentProgressBar;
    SwipeRefreshLayout pullToRefresh;
    RelativeLayout currentLayout;

    static CurrentAirQualityFragment instance;

    /**
     * This is just to make sure the CurrentAirQualityFragment is not null
     * when it is created.
     * @return - the instance of the CurrentAirQualityFragment
     */
    public static CurrentAirQualityFragment getInstance() {
        if (instance == null) {
            instance = new CurrentAirQualityFragment();
        }
        return instance;
    }

    public CurrentAirQualityFragment() {
        // Empty constructor
    }

    /**
     * Creates and returns the view hierarchy associated with the fragment
     * @param inflater - The LayoutInflater object that can be used to
     *                 inflate any views in the fragment
     * @param container - a ViewGroup, if non-null, this is the parent view
     *                  that the fragment's UI should be attached to. The
     *                  fragment should not add the view itself, but this
     *                  can be used to generate the LayoutParams of the view.
     * @param savedInstanceState - a Bundle, if non-null, this fragment
     *                           is being re-constructed from a previous
     *                           saved state as given here.
     * @return - Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_current_air_quality, container, false);

        currentLayout = itemView.findViewById(R.id.current_bg);

        //airQualityPicture = itemView.findViewById(R.id.airquality_picture);
        cityName = itemView.findViewById(R.id.cityName);
        //countryName = itemView.findViewById(R.id.countryName);
        cityTimeStamp = itemView.findViewById(R.id.cityTimestamp);
        cityAQI = itemView.findViewById(R.id.cityAQI);
        cityAQIRating = itemView.findViewById(R.id.cityAQIRating);

        currentPanel = itemView.findViewById(R.id.current_panel);
        currentProgressBar = itemView.findViewById(R.id.current_progress_bar);
        pullToRefresh = itemView.findViewById(R.id.current_swipeRefresh);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(), "Successfully refreshed!", Toast.LENGTH_SHORT).show();
                getCurrentAirQualityData();
                pullToRefresh.setRefreshing(false);
            }
        });

        getCurrentAirQualityData();

        return itemView;
    }

    /**
     * This function sends a request to the API to get the current data.
     */
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

    /**
     * This function loads in the current air quality information based on the
     * user's IP address location.
     * @param station - a Station object that contains information from the API
     */
    private void loadCurrentData(Station station) {
        cityName.setText(station.getData().getCity());
        cityTimeStamp.setText(decodeTimestamp(station.getData().getCurrent().getPollution().getTs()));
        cityAQI.setText(station.getData().getCurrent().getPollution().getAqius().toString());
        cityAQIRating.setText(rankAQIUS(station.getData().getCurrent().getPollution().getAqius()));

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        Log.d("Time", "24 hours: " + hour);

        //Changing the background image depending on the time of day
        //Also changes the status bar color if the device has
        //Android Lollipop or above
        if (hour >= 20 || hour < 5) {
            currentLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_night));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#041b20"));
            }
        } else if (hour >= 5 || hour < 7) {
            currentLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_sunrise));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#060f18"));
            }
        } else if (hour >= 7 || hour < 17) {
            currentLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_sunny));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#08253b"));
            }
        } else if (hour >= 17 || hour < 20) {
            currentLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_sunset));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#20244c"));
            }
        }

        //Display the information after it's been loaded
        //Hide the progress bar and show the info.
        currentPanel.setVisibility(View.VISIBLE);
        currentProgressBar.setVisibility(View.GONE);
    }

    /**
     * This method converts a ISO-8601 timestamp to a readable date.
     * @param timestamp - a String of ISO-8601 compliant timestamp
     * @return - a String of the formatted date in words
     */
    private String decodeTimestamp(String timestamp) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH);
        LocalDate date = LocalDate.parse(timestamp, inputFormatter);
        String formattedDate = outputFormatter.format(date);
        return formattedDate;
    }

    /**
     * This method converts a numerical AQI value to its severity ranking in words
     * @param aqius - This is the air quality index by U.S. EPA standards
     * @return the rank of the air quality index, a String
     */
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
