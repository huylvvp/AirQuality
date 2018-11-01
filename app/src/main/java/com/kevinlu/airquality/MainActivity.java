package com.kevinlu.airquality;

//import statements

import android.content.Intent;
import android.os.Bundle;
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
    private CardView exploreCard, listCard, settingsCard, exitCard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting the MainActivity to use the layout "activity_home"
        setContentView(R.layout.activity_home);

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
                intent = new Intent(this, ListActivity.class);
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