package com.kevinlu.airquality;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * The MainActivity class extends the AppCompatActivity.
 * It contains two Fragments that can be navigated with
 * the BottomNavigation.
 *
 * @author Kevin Lu <649859 @ pdsb.net>
 * @since JDK 1.8
 * @version 1.0
 *
 */
public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment[] fragments;

    /**
     * Called when the activity is starting.
     * @param savedInstanceState - a Bundle, if the activity is being
     *                           re-initialized after previously being
     *                           shut down then this Bundle contains
     *                           the data it most recently supplied in
     *                           onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main2);

        //Polymorphism
        fragments = new Fragment[2];
        fragments[0] = new CurrentAirQualityFragment();
        fragments[1] = new ListFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragments[0]).commit();
    }

    //Create a BottomNavigation listener for when it is selected.
    //This allows for navigation between Fragments.
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            //Switch and case statement to choose between Fragments
            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = fragments[0];
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    break;
                case R.id.nav_list:
                    selectedFragment = fragments[1];
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    break;
            }
            return true;
        }
    };
}
