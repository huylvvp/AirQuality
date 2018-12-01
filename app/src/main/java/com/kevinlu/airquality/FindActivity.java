package com.kevinlu.airquality;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.io.IOException;

public class FindActivity extends AppCompatActivity {
    private static final String apiKeyUrl = "key=ag85mSsqaj2Y24HvQ";

    private static final String countryUrl = "country=";
    private static final String stateUrl = "state=";
    private static final String countriesApiUrl = "http://api.airvisual.com/v2/countries?" + apiKeyUrl;

    private static final String statesApiUrl = "http://api.airvisual.com/v2/states?";

    private static final String countriesUrl = "http://api.airvisual.com/v2/countries?key=ag85mSsqaj2Y24HvQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        TextView apiResponseTest = findViewById(R.id.apiResponseTest);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Request a string response from the provided URL, create a new StringRequest object
        /*
         * @param response - This is the response (JSON file) from the API
         */
        //This is what will happen when there is an error during the response
        /*
         * @param error - This is the error that Volley encountered
         */
        StringRequest stringRequest = new StringRequest(Request.Method.GET, countriesApiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        apiResponseTest.setText(response);
                        Log.d("API RESPONSE", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VOLLEY ERROR", error.toString());
                    }
                });
        //Add the request to the RequestQueue
        requestQueue.add(stringRequest);

    }

    private void getStatesInCountry() {

    }


    /*
     * This function gets data from the API and adds it to the RecyclerView list
     * @return Nothing is being returned.
     */

//    private void loadCitiesIntoRecyclerView() {
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        fileOutputStream = null;
//        //Request a string response from the provided URL, create a new StringRequest object
//        /*
//         * @param response - This is the response (JSON file) from the API
//         */
//        //This is what will happen when there is an error during the response
//        /*
//         * @param error - This is the error that Volley encountered
//         */
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                response -> {
//                    //Using Gson to turn JSON to Java object of Station
//                    //Create new GsonBuilder and Gson objects
//                    GsonBuilder gsonBuilder = new GsonBuilder();
//                    Gson gson = gsonBuilder.create();
//                    //Create a new Station object and use Gson to deserialize JSON data
//                    //into the Station object
//                    Station station = gson.fromJson(response, Station.class);
//                    //All file names are in the format of City.txt
//                    String fileName = station.getData().getCity() + ".txt";
//                    //Add the new Station object to the ArrayList of Station objects
//                    //This is to create another entry in the RecyclerView
//                    //Tell the RecyclerView adapter that our data is updated
//                    //because Station was just to the ArrayList
//                    stationList.add(station);
//                    adapter.notifyDataSetChanged();
//                    //openFileOutput will throw a FileNotFoundException so
//                    //it is surrounded in a try - catch block to handle it
//                    try {
//                        //First try to open a file output, it has parameters
//                        //of the file name and is set to MODE_PRIVATE so only
//                        //the application can access it
//                        fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
//                        //Now the response from the API is written to the file
//                        fileOutputStream.write(response.getBytes());
//                        //Send a Toast to the user informing them that the data
//                        //has been saved to a location on their device
//                        Toast.makeText(getApplicationContext(), "Station " + station.getData().getCity()
//                                + " saved to: " + getFilesDir() + "/" + fileName, Toast.LENGTH_LONG).show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } finally {
//                        //Checking if fileOutputStream is not null
//                        //then it has successfully saved the file to
//                        //the file system of the device, again it is
//                        //surrounded in a try - catch block because
//                        //close() will throw a FileNotFoundException
//                        if (fileOutputStream != null) {
//                            try {
//                                fileOutputStream.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                    //Write the API response data to the log console
//                    Log.d("API RESPONSE", response);
//                }, error -> {
//            //Write the error from Volley to the log console
//            Log.d("VOLLEY ERROR", error.toString());
//        });
//        //Add the request to the RequestQueue
//        requestQueue.add(stringRequest);
//    }
}