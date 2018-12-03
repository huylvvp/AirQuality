package com.kevinlu.airquality;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class FindActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FindAdapter findAdapter;

    private static final String nearestCityApiUrl = "http://api.airvisual.com/v2/nearest_city?key=ag85mSsqaj2Y24HvQ";

//    private static final String apiKeyUrl = "key=ag85mSsqaj2Y24HvQ";
//
//    private static final String countryUrl = "country=";
//    private static final String stateUrl = "state=";
//    private static final String cityUrl = "city=";
//    private static final String countriesApiUrl = "http://api.airvisual.com/v2/countries?" + apiKeyUrl;
//
//    private static final String statesApiUrl = "http://api.airvisual.com/v2/states?";
//    private static final String citiesApiUrl = "http://api.airvisual.com/v2/cities?";
//    private static final String cityApiUrl = "http://api.airvisual.com/v2/city?";
//
//    private static final String countriesUrl = "http://api.airvisual.com/v2/countries?key=ag85mSsqaj2Y24HvQ";

    //private String[] countries = {"Afghanistan","Andorra","Argentina","Australia","Austria","Bahrain","Bangladesh","Belgium","Bosnia Herzegovina","Brazil","Bulgaria","Cambodia","Canada","Chile","China","Colombia","Croatia","Curacao","Cyprus","Czech Republic","Denmark","Ethiopia","Finland","France","Germany","Hong Kong","Hungary","India","Indonesia","Iran","Ireland","Israel","Italy","Japan","Kazakhstan","Kosovo","Kuwait","Latvia","Lithuania","Luxembourg","Macao","Macedonia","Malaysia","Malta","Mexico","Mongolia","Nepal","Netherlands","New Zealand","Nigeria","Norway","Pakistan","Peru","Philippines","Poland","Portugal","Puerto Rico","Romania","Russia","Serbia","Singapore","Slovakia","Slovenia","South Africa","South Korea","Spain","Sri Lanka","Sweden","Switzerland","Taiwan","Thailand","Turkey","USA","Uganda","Ukraine","United Arab Emirates","United Kingdom","Vietnam"};
    //private ArrayList<String> allCities;
    private ArrayList<Station> nearestCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        //Initialize the toolbar object by referencing the toolbar layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        //Set the toolbar object as this activity's SupportActionBar
        setSupportActionBar(toolbar);
        //Add an OnClickListener to the navigation icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //When the icon is clicked, do go back to the MainActivity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        //Set the title of the toolbar to "Air Quality List"
        toolbar.setTitle("Find Cities");
        //Set the navigation icon to a back arrow
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        //allCities = new ArrayList<>();
        nearestCity = new ArrayList<>();

        //Initialize the recyclerView so it can display the data in the ArrayList
        recyclerView = findViewById(R.id.findRecyclerView);
        //Setting some properties of the recyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Initialize the listAdapter object so it can update the recyclerView
        //with data from the ArrayList
        findAdapter = new FindAdapter(this, nearestCity);

        //Set the recyclerView's listAdapter to the listAdapter object
        recyclerView.setAdapter(findAdapter);

        loadRecyclerViewData();

//        for (String country : countries) {
//            ArrayList<String> statesList = new ArrayList<>();
//            String statesRequestUrl = statesApiUrl + countryUrl + country + "&" + apiKeyUrl;
//            RequestQueue requestQueue = Volley.newRequestQueue(this);
//            StringRequest statesStringRequest = new StringRequest(Request.Method.GET, statesRequestUrl, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    try {
//                        JSONObject statesResponseObject = new JSONObject(response);
//                        JSONArray states = statesResponseObject.getJSONArray("data");
//
//                        for (int i = 0; i < states.length(); i++) {
//                            JSONObject stateJSONObject = states.getJSONObject(i);
//                            String state = stateJSONObject.getString("state");
//                            statesList.add(state);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e("VOLLEY ERROR", error.toString());
//                }
//            });
//            requestQueue.add(statesStringRequest);
//
//            for (String state : statesList) {
//                ArrayList<String> citiesList = new ArrayList<>();
//                String citiesRequestUrl = citiesApiUrl + stateUrl + state + "&" + countryUrl + country + "&" + apiKeyUrl;
//                StringRequest citiesStringRequest = new StringRequest(Request.Method.GET, citiesRequestUrl, new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject citiesResponseObject = new JSONObject(response);
//                            JSONArray cities = citiesResponseObject.getJSONArray("data");
//
//                            for (int i = 0; i < cities.length(); i++) {
//                                JSONObject cityJSONObject = cities.getJSONObject(i);
//                                String city = cityJSONObject.getString("city");
//                                citiesList.add(city);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("VOLLEY ERROR", error.toString());
//                    }
//                });
//                requestQueue.add(citiesStringRequest);
//
//                for (String city : citiesList) {
//                    allCities.add(city);
//                }
//            }
//        }
//        findAdapter.notifyDataSetChanged();
    }

    /*
     * This function gets data from the API and adds it to the RecyclerView list
     * @return Nothing is being returned.
     */

    private void loadRecyclerViewData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Request a string response from the provided URL, create a new StringRequest object
        /*
         * @param response - This is the response (JSON file) from the API
         */
        //This is what will happen when there is an error during the response
        /*
         * @param error - This is the error that Volley encountered
         */
        StringRequest stringRequest = new StringRequest(Request.Method.GET, nearestCityApiUrl,
                response -> {
                    //Using Gson to turn JSON to Java object of Station
                    //Create new GsonBuilder and Gson objects
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    //Create a new Station object and use Gson to deserialize JSON data
                    //into the Station object
                    Station station = gson.fromJson(response, Station.class);
                    //All file names are in the format of City.txt
                    //Add the new Station object to the ArrayList of Station objects
                    //This is to create another entry in the RecyclerView
                    //Tell the RecyclerView listAdapter that our data is updated
                    //because Station was just to the ArrayList
                    nearestCity.add(station);
                    findAdapter.notifyDataSetChanged();
                    //Write the API response data to the log console
                    Log.d("API RESPONSE", response);
                }, error -> {
            //Write the error from Volley to the log console
            Log.d("VOLLEY ERROR", error.toString());
        });
        //Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }
}