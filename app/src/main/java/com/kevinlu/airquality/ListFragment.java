package com.kevinlu.airquality;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {

    static ListFragment instance;
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private ArrayList<Station> stationList;

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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

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

}
