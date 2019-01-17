package com.kevinlu.airquality;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class Main2Activity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        fragments = new Fragment[3];
        fragments[0] = new CurrentAirQualityFragment();
        fragments[1] = new ListFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragments[0]).commit();

//        //Initialize the toolbar object by referencing the toolbar layout
//        toolbar = findViewById(R.id.airquality_toolbar);
//        //Set the toolbar object as this activity's SupportActionBar
//        setSupportActionBar(toolbar);
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
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = fragments[0];
                    break;
                case R.id.nav_list:
                    selectedFragment = fragments[1];
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        }
    };
}
