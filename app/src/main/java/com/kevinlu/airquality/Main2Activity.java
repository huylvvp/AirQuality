package com.kevinlu.airquality;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Main2Activity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private CoordinatorLayout coordinatorLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        coordinatorLayout = findViewById(R.id.airquality_view);

        //Initialize the toolbar object by referencing the toolbar layout
        toolbar = findViewById(R.id.airquality_toolbar);
        //Set the toolbar object as this activity's SupportActionBar
        setSupportActionBar(toolbar);
//        //Add an OnClickListener to the navigation icon
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //When the icon is clicked, do go back to the MainActivity
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
//            }
//        });
//        //Set the title of the toolbar to "Air Quality List"
//        toolbar.setTitle("Air Quality List");
//        //Set the navigation icon to a back arrow
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        viewPager = findViewById(R.id.view_pager);
        loadViewPager(viewPager);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void loadViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(CurrentAirQualityFragment.getInstance(), "Current");
        viewPagerAdapter.addFragment(ListFragment.getInstance(), "List");
        viewPager.setAdapter(viewPagerAdapter);
    }
}
