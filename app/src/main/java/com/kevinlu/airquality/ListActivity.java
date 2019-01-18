package com.kevinlu.airquality;

//import statements

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

/**
 * The MainActivity class makes use of the Station classes and
 * is ran when the app starts.
 *
 * @author Kevin Lu <649859 @ pdsb.net>
 * @version 1.0
 * @since JDK 1.8
 */

public class ListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, ListAdapter.OnItemClickListener, RecyclerItemTouchHelperListener {
    public static final String EXTRA_CITY_NAME = "cityName";
    public static final String EXTRA_COORDINATES = "coordinates";
    public static final String EXTRA_TIMESTAMP = "timestamp";
    public static final String EXTRA_AQI_US = "aqiUS";
    public static final String EXTRA_MAIN_POLLUTANT_US = "mainPollutantUS";
    public static final String EXTRA_AQI_CN = "aqiCN";
    public static final String EXTRA_MAIN_POLLUTANT_CN = "mainPollutantCN";
    public static final String EXTRA_STATION_JSON = "stationJSON";

    private final String urlMississauga = "http://api.airvisual.com/v2/city?city=Mississauga&state=Ontario&country=Canada&key=ag85mSsqaj2Y24HvQ";
    private final String url = "http://api.airvisual.com/v2/city?city={{YOUR_CITY}}&state={{YOUR_STATE}}&country={{YOUR_COUNTRY}}&key=5zbAzdPBu2RftKbus";
    //private final String url = "http://api.airvisual.com/v2/city?city={{YOUR_CITY}}&state={{YOUR_STATE}}&country={{YOUR_COUNTRY}}&key=ag85mSsqaj2Y24HvQ";

    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private FileOutputStream fileOutputStream;

    private ArrayList<Station> stationList;
    private ArrayList<String> spinnerList;

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;

    private FloatingActionButton floatingActionButton;
    private SpinnerDialog spinnerDialog;

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
        coordinatorLayout = findViewById(R.id.listCoordinatorLayout);

        //Initialize the toolbar object by referencing the toolbar layout
        toolbar = findViewById(R.id.toolbar);
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
        toolbar.setTitle("Air Quality List");
        //Set the navigation icon to a back arrow
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        //Initialize the Spinner
        spinnerList = new ArrayList<>();
        loadSpinnerListItems();
        spinnerDialog = new SpinnerDialog(ListActivity.this, spinnerList, "Select city");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String s, int i) {
                Toast.makeText(ListActivity.this, s, Toast.LENGTH_SHORT).show();
                loadSelectedDataToRecyclerView(s);
                listAdapter.notifyDataSetChanged();
            }
        });

        //Initialize the FloatingActionButton
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerDialog.showSpinerDialog();
            }
        });

        //Initialize the ArrayList of Station objects
        stationList = new ArrayList<>();

        //Adding a few test items to the stationList to test out the sort functions
        Pollution guangzhouPollution = new Pollution();
        guangzhouPollution.setAqius(110);
        guangzhouPollution.setMainus("p2");
        Current guangzhouCurrent = new Current();
        guangzhouCurrent.setPollution(guangzhouPollution);
        Data guangzhouData = new Data();
        guangzhouData.setCurrent(guangzhouCurrent);
        guangzhouData.setCountry("China");
        guangzhouData.setState("Guangdong");
        guangzhouData.setCity("Guangzhou");
        Station guangzhou = new Station();
        guangzhou.setData(guangzhouData);

        stationList.add(guangzhou);

        Pollution beijingPollution = new Pollution();
        beijingPollution.setAqius(217);
        beijingPollution.setMainus("p2");
        Current beijingCurrent = new Current();
        beijingCurrent.setPollution(beijingPollution);
        Data beijingData = new Data();
        beijingData.setCurrent(beijingCurrent);
        beijingData.setCountry("China");
        beijingData.setState("Beijing");
        beijingData.setCity("Beijing");
        Station beijing = new Station();
        beijing.setData(beijingData);

        stationList.add(beijing);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        //Initialize the recyclerView so it can display the data in the ArrayList
        recyclerView = findViewById(R.id.recyclerView);
        //Setting some properties of the recyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //Initialize the listAdapter object so it can update the recyclerView
        //with data from the ArrayList
        listAdapter = new ListAdapter(this, stationList);

        //Set the recyclerView's listAdapter to the listAdapter object
        recyclerView.setAdapter(listAdapter);
        listAdapter.setOnItemClickListener(ListActivity.this);

        ItemTouchHelper.SimpleCallback ItemTouchHelperCallbackLeft = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        //ItemTouchHelper.SimpleCallback ItemTouchHelperCallbackRight = new RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(ItemTouchHelperCallbackLeft).attachToRecyclerView(recyclerView);
        //new ItemTouchHelper(ItemTouchHelperCallbackRight).attachToRecyclerView(recyclerView);

        //Use the function loadRecyclerViewData to get data from the API
        //loadRecyclerViewData();

        //Write to the console to notify how many items are in the list
        Log.d("List Size", "" + stationList.size());
    }

    /**
     * @param menu - This is a menu object used to inflate the
     *             menu according to the menu layout list_menu
     * @return - Set to true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu here
        getMenuInflater().inflate(R.menu.list_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
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
     * @return - super.onOptionsItemSelected(item)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //A String for the message to be displayed in a Toast
        String msg = "";
        //Switch and case on the MenuItem object's id
        switch (item.getItemId()) {
            case R.id.sort_best_aqi:
                msg = "Sorting by best AQI";
                sortAQIAscending();
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                break;
            case R.id.sort_worst_aqi:
                msg = "Sorting by worst AQI";
                sortAQIDescending();
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                break;
        }
        listAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    /**
     * This function takes the selected Station and turns it
     * into a request URL. This URL will be used to load data
     * into the RecyclerView list.
     * @param station - a String that the user selected from the Spinner
     * @return - a String that contains the request URL
     */
    private String generateRequestURL(String station) {
        String city = station.substring(0, station.indexOf(','));
        String state = station.substring(station.indexOf(',')+2, station.lastIndexOf(','));
        String country = station.substring(station.lastIndexOf(',')+2, station.length());

        String newURL = url.replace("{{YOUR_CITY}}", city);
        newURL = newURL.replace("{{YOUR_STATE}}", state);
        newURL = newURL.replace("{{YOUR_COUNTRY}}", country);

        return newURL;
    }

    /*
     * This function gets data for the selected city from the API
     * and adds it to the RecyclerView list
     */
    private void loadSelectedDataToRecyclerView(String selectedStation) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        fileOutputStream = null;
        //Request a string response from the provided URL, create a new StringRequest object
        /*
         * @param response - This is the response (JSON file) from the API
         */
        //This is what will happen when there is an error during the response
        /*
         * @param error - This is the error that Volley encountered
         */
        StringRequest stringRequest = new StringRequest(Request.Method.GET, generateRequestURL(selectedStation),
                response -> {
                    //Using Gson to turn JSON to Java object of Station
                    //Create new GsonBuilder and Gson objects
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    //Create a new Station object and use Gson to deserialize JSON data
                    //into the Station object
                    Station station = gson.fromJson(response, Station.class);
                    //All file names are in the format of City.json
                    String fileName = station.getData().getCity() + ".json";
                    //Add the new Station object to the ArrayList of Station objects
                    //This is to create another entry in the RecyclerView
                    //Tell the RecyclerView listAdapter that our data is updated
                    //because Station was just to the ArrayList
                    stationList.add(station);
                    listAdapter.notifyDataSetChanged();
                    //openFileOutput will throw a FileNotFoundException so
                    //it is surrounded in a try - catch block to handle it
                    try {
                        //First try to open a file output, it has parameters
                        //of the file name and is set to MODE_PRIVATE so only
                        //the application can access itz
                        fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
                        //Now the response from the API is written to the file
                        fileOutputStream.write(response.getBytes());
                        //gson.toJson(station, new FileWriter(fileName));
                        //Send a Toast to the user informing them that the data
                        //has been saved to a location on their device
                        Toast.makeText(getApplicationContext(), "Station " + station.getData().getCity()
                                + " saved to: " + getFilesDir() + "/" + fileName, Toast.LENGTH_LONG).show();
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
                }, error -> {
            //Write the error from Volley to the log console
            Log.d("VOLLEY ERROR", error.toString());
        });
        //Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }

    /*
     * This function gets data from the API and adds it to the RecyclerView list
     */
    private void loadRecyclerViewData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        fileOutputStream = null;
        //Request a string response from the provided URL, create a new StringRequest object
        /*
         * @param response - This is the response (JSON file) from the API
         */
        //This is what will happen when there is an error during the response
        /*
         * @param error - This is the error that Volley encountered
         */
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlMississauga,
                response -> {
                    //Using Gson to turn JSON to Java object of Station
                    //Create new GsonBuilder and Gson objects
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    //Create a new Station object and use Gson to deserialize JSON data
                    //into the Station object
                    Station station = gson.fromJson(response, Station.class);
                    //All file names are in the format of City.json
                    String fileName = station.getData().getCity() + ".json";
                    //Add the new Station object to the ArrayList of Station objects
                    //This is to create another entry in the RecyclerView
                    //Tell the RecyclerView listAdapter that our data is updated
                    //because Station was just to the ArrayList
                    stationList.add(station);
                    listAdapter.notifyDataSetChanged();
                    //openFileOutput will throw a FileNotFoundException so
                    //it is surrounded in a try - catch block to handle it
                    try {
                        //First try to open a file output, it has parameters
                        //of the file name and is set to MODE_PRIVATE so only
                        //the application can access it
                        fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
                        //Now the response from the API is written to the file
                        fileOutputStream.write(response.getBytes());
                        //gson.toJson(station, new FileWriter(fileName));
                        //Send a Toast to the user informing them that the data
                        //has been saved to a location on their device
                        Toast.makeText(getApplicationContext(), "Station " + station.getData().getCity()
                                + " saved to: " + getFilesDir() + "/" + fileName, Toast.LENGTH_LONG).show();
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
                }, error -> {
            //Write the error from Volley to the log console
            Log.d("VOLLEY ERROR", error.toString());
        });
        //Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }

    /**
     * Called when the user submits the query. This could be due to a key press on the
     * keyboard or due to pressing a submit button.
     * The listener can override the standard behavior by returning true
     * to indicate that it has handled the submit request. Otherwise return false to
     * let the SearchView handle the submission by launching any associated intent.
     *
     * @param query the query text that is to be submitted
     * @return true if the query has been handled by the listener, false to let the
     * SearchView perform the default action.
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        //The RecyclerView updates as the user types in the SearchView
        //thus eliminating the need to submit the search query text
        return false;
    }

    /**
     * Called when the query text is changed by the user.
     *
     * @param newText the new content of the query text field.
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        //Use the filter function to filter results based on
        //the user's input in the SearchView text field
        filter(newText.toLowerCase());
        return true;
    }

    /**
     * Called when the query text is changed by the user.
     * This function uses a Linear Search algorithm to
     * only display items that contains the String user inputted.
     *
     * @param text the new content of the query text field.
     */
    private void filter(@NotNull String text) {
        long startTime = System.currentTimeMillis();
        if (text.length() == 0) {
            listAdapter.filterList(stationList);
        } else {
            ArrayList<Station> filteredList = new ArrayList<>();

            for (Station item : stationList) {
                if (item.getData().getCity().toLowerCase().contains(text)) {
                    filteredList.add(item);
                }
            }
            listAdapter.filterList(filteredList);
        }
        long endTime = System.currentTimeMillis();
        Log.d("Linear Search time", (endTime - startTime) + "");
    }

    /**
     * Called when the query text is changed by the user.
     * This function uses a Binary Search algorithm to
     * only display items that contains the String user inputted.
     *
     * @param aqi integer value of AQI to search city with AQI value
     */
    private void filterBinarySearch(int aqi) {
        long startTime = System.currentTimeMillis();
        sortAQIAscending();
        int left = 0;
        int right = stationList.size() - 1;
        ArrayList<Station> filteredList = new ArrayList<>();

        while (left <= right) {
            int midPoint = left + (right - 1) / 2;

            if (stationList.get(midPoint).getData().getCurrent().getPollution().getAqius() == aqi) {
                filteredList.add(stationList.get(midPoint));
            }

            if (stationList.get(midPoint).getData().getCurrent().getPollution().getAqius() < aqi) {
                left = midPoint + 1;
            } else {
                right = midPoint - 1;
            }
        }
        long endTime = System.currentTimeMillis();
        Log.d("Binary Search time", (endTime - startTime) + "");
        listAdapter.filterList(filteredList);
    }

    /**
     * Overrides the onItemClick method specified in the
     * setOnItemClickListener interface. This method grabs information
     * from the RecyclerView item and gives it to the CityActivity which launches
     * through the cityIntent object.
     *
     * @param position - an integer which defines which item
     *                 in the RecyclerView has been clicked on
     */
    @Override
    public void onItemClick(int position) {
        Intent cityIntent = new Intent(this, CityActivity.class);
        Station clickedStation = stationList.get(position);

        cityIntent.putExtra(EXTRA_CITY_NAME, clickedStation.getData().getCity());
        cityIntent.putExtra(EXTRA_COORDINATES, clickedStation.getData().getLocation().getCoordinates().toString());
        cityIntent.putExtra(EXTRA_TIMESTAMP, clickedStation.getData().getCurrent().getPollution().getTs());
        cityIntent.putExtra(EXTRA_AQI_US, clickedStation.getData().getCurrent().getPollution().getAqius().toString());
        cityIntent.putExtra(EXTRA_MAIN_POLLUTANT_US, clickedStation.getData().getCurrent().getPollution().getMainus());
        cityIntent.putExtra(EXTRA_AQI_CN, clickedStation.getData().getCurrent().getPollution().getAqicn().toString());
        cityIntent.putExtra(EXTRA_MAIN_POLLUTANT_CN, clickedStation.getData().getCurrent().getPollution().getMaincn());
        cityIntent.putExtra(EXTRA_STATION_JSON, new Gson().toJson(clickedStation));
        startActivity(cityIntent);
    }

    /**
     * This onSwiped function overrides the method defined in the
     * RecyclerItemTouchHelperListener interface. It removes a Station
     * from the RecyclerView if it was swiped left/right. A Snackbar is
     * also shown to notify the user of the change and allows the
     * user to undo the removal if desired.
     *
     * @param viewHolder
     * @param direction
     * @param position
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ListAdapter.ViewHolder) {
            if (direction == ItemTouchHelper.LEFT) {
                String cityName = stationList.get(viewHolder.getAdapterPosition()).getData().getCity();

                //Create a backup of the deleted item in case user wants to undo delete
                Station deletedStation = stationList.get(viewHolder.getAdapterPosition());
                int deletedStationIndex = viewHolder.getAdapterPosition();

                //Remove the item from RecyclerView
                listAdapter.removeItem(deletedStationIndex);

                Snackbar snackbar = Snackbar.make(coordinatorLayout, cityName + " removed from list!", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // undo is selected, restore the deleted item
                        listAdapter.restoreItem(deletedStation, position);
                    }
                });
                snackbar.show();
            }
            if (direction == ItemTouchHelper.RIGHT) {
                String cityName = stationList.get(viewHolder.getAdapterPosition()).getData().getCity();
                Snackbar snackbar = Snackbar.make(coordinatorLayout, cityName + " added to favourites!", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("yeet", "big oof");
                    }
                });
                snackbar.show();
            }
        }
    }

    private void loadSpinnerListItems() {
        spinnerList.add("Athabasca Valley, Alberta, Canada");
        spinnerList.add("Beaverlodge, Alberta, Canada");
        spinnerList.add("Bruderheim, Alberta, Canada");
        spinnerList.add("Calgary, Alberta, Canada");
        spinnerList.add("Calmar, Alberta, Canada");
        spinnerList.add("Caroline, Alberta, Canada");
        spinnerList.add("Edmonton, Alberta, Canada");
        spinnerList.add("Elk Island, Alberta, Canada");
        spinnerList.add("Fort Chipewyan, Alberta, Canada");
        spinnerList.add("Fort Mckay, Alberta, Canada");
        spinnerList.add("Improvement District No. 24, Alberta, Canada");
        spinnerList.add("Lethbridge, Alberta, Canada");
        spinnerList.add("Red Deer, Alberta, Canada");

        spinnerList.add("Abbotsford, British Columbia, Canada");
        spinnerList.add("Burnaby, British Columbia, Canada");
        spinnerList.add("Burns Lake, British Columbia, Canada");
        spinnerList.add("Campbell River, British Columbia, Canada");
        spinnerList.add("Chilliwack, British Columbia, Canada");
        spinnerList.add("Coldstream, British Columbia, Canada");
        spinnerList.add("Coquitlam, British Columbia, Canada");
        spinnerList.add("Courtenay, British Columbia, Canada");
        spinnerList.add("Crofton, British Columbia, Canada");
        spinnerList.add("Duncan, British Columbia, Canada");
        spinnerList.add("Fort St John, British Columbia, Canada");
        spinnerList.add("Gibsons, British Columbia, Canada");
        spinnerList.add("Golden, British Columbia, Canada");
        spinnerList.add("Grand Forks, British Columbia, Canada");
        spinnerList.add("Hope, British Columbia, Canada");
        spinnerList.add("Houston, British Columbia, Canada");
        spinnerList.add("Kamloops, British Columbia, Canada");
        spinnerList.add("Langley, British Columbia, Canada");
        spinnerList.add("Maple Ridge, British Columbia, Canada");
        spinnerList.add("Nanaimo, British Columbia, Canada");
        spinnerList.add("New Westminster, British Columbia, Canada");
        spinnerList.add("North Vancouver, British Columbia, Canada");
        spinnerList.add("Port Alberni, British Columbia, Canada");
        spinnerList.add("Powell River, British Columbia, Canada");
        spinnerList.add("Richmond, British Columbia, Canada");
        spinnerList.add("Squamish, British Columbia, Canada");
        spinnerList.add("Surrey East, British Columbia, Canada");
        spinnerList.add("West Vancouver, British Columbia, Canada");

        spinnerList.add("Brandon, Manitoba, Canada");
        spinnerList.add("Flin Flon, Manitoba, Canada");

        spinnerList.add("Bathurst, New Brunswick, Canada");
        spinnerList.add("Forest Hills, New Brunswick, Canada");
        spinnerList.add("Moncton, New Brunswick, Canada");
        spinnerList.add("Saint John West, New Brunswick, Canada");
        spinnerList.add("St Andrews, New Brunswick, Canada");

        spinnerList.add("Corner Brook, Newfoundland and Labrador, Canada");
        spinnerList.add("Goose Bay, Newfoundland and Labrador, Canada");
        spinnerList.add("Grand Falls Windsor, Newfoundland and Labrador, Canada");
        spinnerList.add("Labrador City, Newfoundland and Labrador, Canada");
        spinnerList.add("Marystown, Newfoundland and Labrador, Canada");
        spinnerList.add("Mount Pearl, Newfoundland and Labrador, Canada");
        spinnerList.add("St. John's, Newfoundland and Labrador, Canada");

        spinnerList.add("Snare Rapids, Northwest Territories, Canada");

        spinnerList.add("Aylesford Mountain, Nova Scotia, Canada");
        spinnerList.add("Kentville, Nova Scotia, Canada");
        spinnerList.add("Lake Major, Nova Scotia, Canada");
        spinnerList.add("Pictou, Nova Scotia, Canada");
        spinnerList.add("Port Hawkesbury, Nova Scotia, Canada");
        spinnerList.add("Sydney, Nova Scotia, Canada");

        spinnerList.add("Algoma, Ontario, Canada");
        spinnerList.add("Barrie, Ontario, Canada");
        spinnerList.add("Belleville, Ontario, Canada");
        spinnerList.add("Bonner Lake, Ontario, Canada");
        spinnerList.add("Brampton, Ontario, Canada");
        spinnerList.add("Brantford, Ontario, Canada");
        spinnerList.add("Burlington, Ontario, Canada");
        spinnerList.add("Chatham, Ontario, Canada");
        spinnerList.add("Cornwall, Ontario, Canada");
        spinnerList.add("Egbert, Ontario, Canada");
        spinnerList.add("Guelph, Ontario, Canada");
        spinnerList.add("Hamilton, Ontario, Canada");
        spinnerList.add("Kingston, Ontario, Canada");
        spinnerList.add("Kitchener, Ontario, Canada");
        spinnerList.add("London, Ontario, Canada");
        spinnerList.add("Mississauga, Ontario, Canada");
        spinnerList.add("Newmarket, Ontario, Canada");
        spinnerList.add("North Bay, Ontario, Canada");
        spinnerList.add("Oakville, Ontario, Canada");
        spinnerList.add("Oshawa, Ontario, Canada");
        spinnerList.add("Ottawa, Ontario, Canada");
        spinnerList.add("Parry Sound, Ontario, Canada");
        spinnerList.add("Peterborough, Ontario, Canada");
        spinnerList.add("Sarnia, Ontario, Canada");
        spinnerList.add("Sault Ste Marie, Ontario, Canada");
        spinnerList.add("St. Catharines, Ontario, Canada");
        spinnerList.add("Sudbury, Ontario, Canada");
        spinnerList.add("Thunder Bay, Ontario, Canada");
        spinnerList.add("Tiverton, Ontario, Canada");
        spinnerList.add("Toronto, Ontario, Canada");
        spinnerList.add("Windsor, Ontario, Canada");

        //TODO: add more stations.. maybe try to do this with a script?
    }

    /*
     * Time it took for Linear search vs. Binary search
     * Linear: 102 ms
     * Binary: 127 ms
     * LINEAR SEARCH WINS!!!
     */
}