package com.kevinlu.airquality;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kc.unsplash.Unsplash;
import com.kc.unsplash.models.Download;
import com.kc.unsplash.models.Photo;
import com.kc.unsplash.models.SearchResults;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import static com.kevinlu.airquality.ListActivity.EXTRA_AQI_CN;
import static com.kevinlu.airquality.ListActivity.EXTRA_AQI_US;
import static com.kevinlu.airquality.ListActivity.EXTRA_CITY_NAME;
import static com.kevinlu.airquality.ListActivity.EXTRA_COORDINATES;
import static com.kevinlu.airquality.ListActivity.EXTRA_MAIN_POLLUTANT_CN;
import static com.kevinlu.airquality.ListActivity.EXTRA_MAIN_POLLUTANT_US;
import static com.kevinlu.airquality.ListActivity.EXTRA_TIMESTAMP;

public class CityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        Intent intent = getIntent();
        String cityName = intent.getStringExtra(EXTRA_CITY_NAME);
        String coordinates = intent.getStringExtra(EXTRA_COORDINATES);
        String timestamp = intent.getStringExtra(EXTRA_TIMESTAMP);
        String aqiUS = intent.getStringExtra(EXTRA_AQI_US);
        String mainPollutantUS = intent.getStringExtra(EXTRA_MAIN_POLLUTANT_US);
        String aqiCN = intent.getStringExtra(EXTRA_AQI_CN);
        String mainPollutantCN = intent.getStringExtra(EXTRA_MAIN_POLLUTANT_CN);

        ImageView imageView = findViewById(R.id.cityImage);
        TextView textViewDescription = findViewById(R.id.cityDescription);
        TextView textViewCoordinates = findViewById(R.id.cityCoordinates);
        TextView textViewTimestamp = findViewById(R.id.cityTimestamp);
        TextView textViewAQIUS = findViewById(R.id.cityAQIUS);
        TextView textViewMainPollutantUS = findViewById(R.id.cityMainPollutantUS);
        TextView textViewAQICN = findViewById(R.id.cityAQICN);
        TextView textViewMainPollutantCN = findViewById(R.id.cityMainPollutantCN);

        Toolbar toolbar = findViewById(R.id.cityToolbar);
        toolbar.setTitle(cityName);

        textViewDescription.setText("Hello there, the city is... ??? hmmmmm");
        textViewCoordinates.setText(coordinates);
        textViewTimestamp.setText(timestamp);
        textViewAQIUS.setText(aqiUS);
        textViewMainPollutantUS.setText(mainPollutantUS);
        textViewAQICN.setText(aqiCN);
        textViewMainPollutantCN.setText(mainPollutantCN);

        Unsplash unsplash = new Unsplash("2ef4adbbc6aa68fb62acbf7170933cbc25526420aa6da426c16cb0c08ce5cffd");
        unsplash.searchPhotos(cityName, new Unsplash.OnSearchCompleteListener() {
            @Override
            public void onComplete(SearchResults results) {
                Log.d("Photos", "Total Results Found " + results.getTotal());
                List<Photo> photos = results.getResults();
                unsplash.getPhotoDownloadLink(photos.get(0).getId(), new Unsplash.OnLinkLoadedListener() {
                    @Override
                    public void onComplete(Download downloadLink) {
                        String imageLink = downloadLink.getUrl();
                        Picasso.get().load(imageLink).into(imageView);
                    }

                    @Override
                    public void onError(String error) {
                        Log.d("Unsplash", error);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.d("Unsplash", error);
            }
        });
//        Picasso.get().load("https://dynamicmedia.zuza.com/zz/m/original_/4/c/4cbceb84-5129-4275-a4bc-7ef0507acf43/N_M_Misc_Skyline_4637_rb_8_Super_Portrait.jpg").into(imageView);
    }
}
