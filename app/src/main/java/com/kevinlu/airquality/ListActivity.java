package com.kevinlu.airquality;

//import statements

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The MainActivity class makes use of the Station classes and
 * is ran when the app starts.
 *
 * @author Kevin Lu <649859 @ pdsb.net>
 * @since JDK 1.8
 * @version 1.0
 *
 */

public class ListActivity extends AppCompatActivity {

    private static final String url = "http://api.airvisual.com/v2/city?city=Mississauga&state=Ontario&country=Canada&key=ag85mSsqaj2Y24HvQ";
    private RecyclerView recyclerView;
    private Adapter adapter;
    private FileOutputStream fileOutputStream;

    private List<Station> stationList;

    @Override
    /*
     * Called when the activity is first created. This is where everything
     * is setup: create views, bind data to lists, etc. This method also
     * provides a Bundle containing the activity's previously frozen state,
     * if there was one.
     * @param savedInstanceState - This is a Bundle object of the activity's previously frozen state
     */
    protected void onCreate(Bundle savedInstanceState) {
        //super refers to the immediate parents' property
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //Initialize the toolbar object by referencing the toolbar layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        //Set the toolbar object as this activity's SupportActionBar
        setSupportActionBar(toolbar);
        //Add an OnClickListener to the navigation icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //When the icon is clicked, do go back to the MainActivity
                //TODO: Implement go back to MainActivity onClick
                Toast.makeText(getApplicationContext(), "Back button clicked", Toast.LENGTH_SHORT).show();
            }
        });
        //Set the title of the toolbar to "Air Quality List"
        toolbar.setTitle("Air Quality List");
        //Set the navigation icon to a back arrow
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        //Initialize the ArrayList of Station objects
        stationList = new ArrayList<>();

        //Adding a few test items to the stationList to test out the sort functions
        Pollution guangzhouPollution = new Pollution(); guangzhouPollution.setAqius(110); guangzhouPollution.setMainus("p2");
        Current guangzhouCurrent = new Current(); guangzhouCurrent.setPollution(guangzhouPollution);
        Data guangzhouData = new Data(); guangzhouData.setCurrent(guangzhouCurrent); guangzhouData.setCountry("China"); guangzhouData.setState("Guangdong"); guangzhouData.setCity("Guangzhou");
        Station guangzhou = new Station(); guangzhou.setData(guangzhouData);

        stationList.add(guangzhou);

        Pollution beijingPollution = new Pollution(); beijingPollution.setAqius(217); beijingPollution.setMainus("p2");
        Current beijingCurrent = new Current(); beijingCurrent.setPollution(beijingPollution);
        Data beijingData = new Data(); beijingData.setCurrent(beijingCurrent); beijingData.setCountry("China"); beijingData.setState("Beijing"); beijingData.setCity("Beijing");
        Station beijing = new Station(); beijing.setData(beijingData);

        stationList.add(beijing);

        //Initialize the recyclerView so it can display the data in the ArrayList
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //Setting some properties of the recyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Initialize the adapter object so it can update the recyclerView
        //with data from the ArrayList
        adapter = new Adapter(this, stationList);

        //Set the recyclerView's adapter to the adapter object
        recyclerView.setAdapter(adapter);

        //Use the function loadRecyclerViewData to get data from the API
        loadRecyclerViewData();

        //Write to the console to notify how many items are in the list
        Log.d("List Size", "" + stationList.size());
    }

    /**
     * @param menu - This is a menu object used to inflate the
     *             menu according to the menu layout list_menu
     * @return     - Set to true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu here
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    /**
     * This function sorts the AQI value in the stationList
     * in ascending order (lowest AQI to highest AQI)
     */
    private void sortAQIAscending() {
        //Call a Collections.sort function on the stationList
        //and implement a Comparator
        //@return - Positive integer if AQI of one station is greater than another
        //          Negative integer if AQI of one station is less than another
        //          Zero if both stations' AQI are the same
        //This is using the built-in sort function
        Collections.sort(stationList, (station1, station2) -> {
            if (station1.getData().getCurrent().getPollution().getAqius() > station2.getData().getCurrent().getPollution().getAqius()) {
                return 1;
            } else if (station1.getData().getCurrent().getPollution().getAqius() < station2.getData().getCurrent().getPollution().getAqius()) {
                return -1;
            } else {
                return 0;
            }
        });
    }

    /**
     * This function sorts the AQI value in the stationList
     * in descending order (highest AQI to lowest AQI)
     */
    private void sortAQIDescending() {
        //Call a Collections.sort function on the stationList
        //and implement a Comparator
        //@return - Positive integer if AQI of one station is greater than another
        //          Negative integer if AQI of one station is less than another
        //          Zero if both stations' AQI are the same
        //This is using the built-in sort function
        Collections.sort(stationList, (station1, station2) -> {
            if (station1.getData().getCurrent().getPollution().getAqius() > station2.getData().getCurrent().getPollution().getAqius()) {
                return -1;
            } else if (station1.getData().getCurrent().getPollution().getAqius() < station2.getData().getCurrent().getPollution().getAqius()) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    /**
     * This function sorts the AQI value in the stationList
     * in ascending order using the bubble sort algorithm
     */
    private void bubbleSortAQI() {
        int n = stationList.size();
        for (int i = 1; i < n; i++) {
            Station keyStation = stationList.get(i);
            int key = keyStation.getData().getCurrent().getPollution().getAqius();
            int j = i - 1;

            while (j >= 0 && stationList.get(j).getData().getCurrent().getPollution().getAqius() > key) {
                    stationList.set(j + 1, stationList.get(j));
                    j = j - 1;
            }
            stationList.set(j + 1, keyStation);
        }
    }

    /**
     * This function sorts the AQI value in the stationList
     * in ascending order using the bubble sort algorithm
     */
    private void insertionSortAQI() {
        int n = stationList.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (stationList.get(j).getData().getCurrent().getPollution().getAqius() > stationList.get(j + 1).getData().getCurrent().getPollution().getAqius()) {
                    Station temp = stationList.get(j);
                    stationList.set(j, stationList.get(j + 1));
                    stationList.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * @param item - This is a MenuItem object, each item in the menu is
     *             its own object
     * @return     - super.onOptionsItemSelected(item)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //A String for the message to be displayed in a Toast
        String msg = "";
        //Switch and case on the MenuItem object's id
        switch (item.getItemId()) {
            case R.id.sort_best_aqi:
                msg = "sorting by best AQI.";
                sortAQIAscending();
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                break;
            case R.id.sort_worst_aqi:
                msg = "sorting by worst AQI.";
                sortAQIDescending();
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_search:
                //TODO: implement a search function (as per Searching task)
                msg = "displaying search (not implemented)";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                break;
        }
        adapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    /*
     * This function gets data from the API and adds it to the RecyclerView list
     * @return Nothing is being returned.
     */

    private void loadRecyclerViewData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        fileOutputStream = null;
        //Request a string response from the provided URL, create a new StringRequest object
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    /*
                     * @param response - This is the response (JSON file) from the API
                     */
                    public void onResponse(String response) {
                        //Using Gson to turn JSON to Java object of Station
                        //Create new GsonBuilder and Gson objects
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        //Create a new Station object and use Gson to deserialize JSON data
                        //into the Station object
                        Station station = gson.fromJson(response, Station.class);
                        //All file names are in the format of City.txt
                        String fileName = station.getData().getCity() + ".txt";
                        //Add the new Station object to the ArrayList of Station objects
                        //This is to create another entry in the RecyclerView
                        //Tell the RecyclerView adapter that our data is updated
                        //because Station was just to the ArrayList
                        stationList.add(station);
                        adapter.notifyDataSetChanged();
                        //openFileOutput will throw a FileNotFoundException so
                        //it is surrounded in a try - catch block to handle it
                        try {
                            //First try to open a file output, it has parameters
                            //of the file name and is set to MODE_PRIVATE so only
                            //the application can access it
                            fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
                            //Now the response from the API is written to the file
                            fileOutputStream.write(response.getBytes());
                            //Send a Toast to the user informing them that the data
                            //has been saved to a location on their device
                            Toast.makeText(getApplicationContext(), "Station " + station.getData().getCity()
                                    + " saved to: " + getFilesDir() + "/" + fileName, Toast.LENGTH_LONG).show();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            //Checking if fileOutputStream is not null
                            //then it has successfully saved the file to
                            //the file system of the device, again it is
                            //surrounded in a try - catch block because
                            //close() will throw a FileNotFoundException
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        //Write the API response data to the log console
                        Log.d("API RESPONSE", response);
                    }
                }, new Response.ErrorListener() {
            //This is what will happen when there is an error during the response
            /*
             * @param error - This is the error that Volley encountered
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                //Write the error from Volley to the log console
                Log.d("VOLLEY ERROR", error.toString());
            }
        });
        //Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }
}