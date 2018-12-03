package com.kevinlu.airquality;

//import statements

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

/**
 * The MainActivity class makes use of the Station classes and
 * is ran when the app starts.
 *
 * @author Kevin Lu <649859 @ pdsb.net>
 * @since JDK 1.8
 * @version 1.0
 *
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private CardView findCard;
    private CardView exploreCard;
    private CardView listCard;
    private CardView settingsCard;
    private CardView exitCard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting the MainActivity to use the layout "activity_home"
        setContentView(R.layout.activity_home);

        //  Declare a new thread to do a preference check
        Thread t = new Thread(() -> {
            //  Initialize SharedPreferences
            SharedPreferences getPrefs = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());

            //  Create a new boolean and preference and set it to true
            boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

            //  If the activity has never started before...
            if (isFirstStart) {

                //  Launch app intro
                final Intent i = new Intent(MainActivity.this, OnboardingActivity.class);

                runOnUiThread(() -> startActivity(i));

                //  Make a new preferences editor
                SharedPreferences.Editor e = getPrefs.edit();

                //  Edit preference to make it false because we don't want this to run again
                e.putBoolean("firstStart", false);

                //  Apply changes
                e.apply();
            }
        });

        // Start the thread
        t.start();

        //Defining CardView objects to match the id specified in layout
        exploreCard = (CardView) findViewById(R.id.explore_card);
        listCard = (CardView) findViewById(R.id.list_card);
        settingsCard = (CardView) findViewById(R.id.settings_card);
        exitCard = (CardView) findViewById(R.id.exit_card);

        //Adding a onClickListener to each of the CardViews
        exploreCard.setOnClickListener(this);
        listCard.setOnClickListener(this);
        settingsCard.setOnClickListener(this);
        exitCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;

        switch(view.getId()) {
            case R.id.explore_card:
                intent = new Intent(this, ExploreActivity.class);
                startActivity(intent);
                break;
            case R.id.list_card:
                //intent = new Intent(this, ListActivity.class);
                intent = new Intent(this, FindActivity.class);
                startActivity(intent);
                break;
            case R.id.settings_card:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.exit_card:
                intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory( Intent.CATEGORY_HOME );
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            default: break;
        }
    }
}