package com.kevinlu.airquality;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment implements RecyclerItemTouchHelperListener {

    static ListFragment instance;
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private ArrayList<Station> stationList;
    private ArrayList<String> spinnerList;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private SpinnerDialog spinnerDialog;

    private final String url = "http://api.airvisual.com/v2/city?city={{YOUR_CITY}}&state={{YOUR_STATE}}&country={{YOUR_COUNTRY}}&key=ag85mSsqaj2Y24HvQ";

    public static ListFragment getInstance() {
        if (instance == null) {
            instance = new ListFragment();
        }
        return instance;
    }

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_list, container, false);

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

        //Initialize the toolbar object by referencing the toolbar layout
        toolbar = itemView.findViewById(R.id.toolbar);
        //Set the toolbar object as this activity's SupportActionBar
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        //Initialize the Spinner
        spinnerList = new ArrayList<>();
        loadSpinnerListItems();
        spinnerDialog = new SpinnerDialog(getActivity(), spinnerList, "Select city");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String s, int i) {
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                loadSelectedDataToRecyclerView(s);
                listAdapter.notifyDataSetChanged();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        ItemTouchHelper.SimpleCallback ItemTouchHelperCallbackLeft = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(ItemTouchHelperCallbackLeft).attachToRecyclerView(recyclerView);

        //Initialize the recyclerView so it can display the data in the ArrayList
        recyclerView = itemView.findViewById(R.id.recyclerView);
        //Setting some properties of the recyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        //Initialize the listAdapter object so it can update the recyclerView
        //with data from the ArrayList
        listAdapter = new ListAdapter(getActivity(), stationList);

        //Set the recyclerView's listAdapter to the listAdapter object
        recyclerView.setAdapter(listAdapter);

        return itemView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.item_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
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
                public boolean onQueryTextChange(String query) {
                    filter(query.toLowerCase());
                    return true;
                }
                /**
                 * Called when the query text is changed by the user.
                 *
                 * @param query the new content of the query text field.
                 * @return false if the SearchView should perform the default action of showing any
                 * suggestions if available, true if the action was handled by the listener.
                 */
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
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
        Log.d("Binary Search time", (endTime - startTime) + "");
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
     * @param item - This is a MenuItem object, each item in the menu is
     *             its own object
     * @return - super.onOptionsItemSelected(item)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Switch and case on the MenuItem object's id
        switch (item.getItemId()) {
            case R.id.sort_best_aqi:
                sortAQIAscending();
                break;
            case R.id.sort_worst_aqi:
                sortAQIDescending();
                break;
            case R.id.item_add:
                spinnerDialog.showSpinerDialog();
        }
        listAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    /*
     * This function gets data for the selected city from the API
     * and adds it to the RecyclerView list
     */
    private void loadSelectedDataToRecyclerView(String selectedStation) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        //fileOutputStream = null;
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
//                    try {
//                        //First try to open a file output, it has parameters
//                        //of the file name and is set to MODE_PRIVATE so only
//                        //the application can access itz
//                        //fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
//                        //Now the response from the API is written to the file
//                        //fileOutputStream.write(response.getBytes());
//                        //gson.toJson(station, new FileWriter(fileName));
//                        //Send a Toast to the user informing them that the data
//                        //has been saved to a location on their device
////                        Toast.makeText(getActivity(), "Station " + station.getData().getCity()
////                                + " saved to: " + getFilesDir() + "/" + fileName, Toast.LENGTH_LONG).show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } finally {
//                        //Checking if fileOutputStream is not null
//                        //then it has successfully saved the file to
//                        //the file system of the device, again it is
//                        //surrounded in a try - catch block because
//                        //close() will throw a FileNotFoundException
////                        if (fileOutputStream != null) {
////                            try {
////                                fileOutputStream.close();
////                            } catch (IOException e) {
////                                e.printStackTrace();
////                            }
////                        }
//                    }
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
}