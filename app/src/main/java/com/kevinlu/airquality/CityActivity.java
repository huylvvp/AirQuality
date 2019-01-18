package com.kevinlu.airquality;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kc.unsplash.Unsplash;
import com.kc.unsplash.models.Download;
import com.kc.unsplash.models.Photo;
import com.kc.unsplash.models.SearchResults;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.kevinlu.airquality.ListFragment.EXTRA_AQI_CN;
import static com.kevinlu.airquality.ListFragment.EXTRA_AQI_US;
import static com.kevinlu.airquality.ListFragment.EXTRA_CITY_NAME;
import static com.kevinlu.airquality.ListFragment.EXTRA_COORDINATES;
import static com.kevinlu.airquality.ListFragment.EXTRA_MAIN_POLLUTANT_CN;
import static com.kevinlu.airquality.ListFragment.EXTRA_MAIN_POLLUTANT_US;
import static com.kevinlu.airquality.ListFragment.EXTRA_TIMESTAMP;
import static com.kevinlu.airquality.ListFragment.EXTRA_STATION_JSON;

/**
 * The CityActivity class extends the AppCompatActivity.
 * It shows crucial air quality information to the user based
 * on the selected Station object.
 *
 * @author Kevin Lu <649859 @ pdsb.net>
 * @since JDK 1.8
 * @version 1.0
 *
 */
public class CityActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_city);

        Gson gson = new Gson();

        Intent intent = getIntent();
        String cityName = intent.getStringExtra(EXTRA_CITY_NAME);
        String coordinates = intent.getStringExtra(EXTRA_COORDINATES);
        String timestamp = intent.getStringExtra(EXTRA_TIMESTAMP);
        String aqiUS = intent.getStringExtra(EXTRA_AQI_US);
        String mainPollutantUS = intent.getStringExtra(EXTRA_MAIN_POLLUTANT_US);
        String aqiCN = intent.getStringExtra(EXTRA_AQI_CN);
        String mainPollutantCN = intent.getStringExtra(EXTRA_MAIN_POLLUTANT_CN);
        String stationJSON = intent.getStringExtra(EXTRA_STATION_JSON);

        Station station = gson.fromJson(stationJSON, Station.class);

        String countryName = station.getData().getCountry();

        Log.d("JSON test", station.getStatus());

        ImageView imageView = findViewById(R.id.cityImage);
        TextView textViewCoordinates = findViewById(R.id.cityCoordinates);
        TextView textViewTimestamp = findViewById(R.id.cityTimestamp);
        TextView textViewAQIUS = findViewById(R.id.cityAQIUS);
        TextView textViewMainPollutantUS = findViewById(R.id.cityMainPollutantUS);
        TextView textViewAQICN = findViewById(R.id.cityAQICN);
        TextView textViewMainPollutantCN = findViewById(R.id.cityMainPollutantCN);

        Toolbar toolbar = findViewById(R.id.cityToolbar);
        toolbar.setTitle(cityName);

        textViewCoordinates.setText("Coordinates: " + coordinates);
        textViewTimestamp.setText("ISO-8601 Timestamp: " + timestamp);
        textViewAQIUS.setText("U.S. AQI: " + aqiUS);
        textViewMainPollutantUS.setText("U.S. Main Pollutant: " + decodePollutant(mainPollutantUS));
        textViewAQICN.setText("China AQI: " + aqiCN);
        textViewMainPollutantCN.setText("China Main Pollutant: " + decodePollutant(mainPollutantCN));

        loadHeaderImageFromUnsplash(countryName, imageView);
    }

    /**
     * This function uses an Unsplash library to load a header image
     * corresponding to the city of interest.
     * @param cityName - a String, the name of the city.
     * @param imageView - the ImageView that should hold this image.
     */
    private void loadHeaderImageFromUnsplash(String cityName, ImageView imageView) {
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
    }

    /**
     * This method converts short form of pollutant names to its full form
     * @param mainPollutant - This is a String of the short form of the pollutant name
     * @return Full form of the short form pollutant
     */
    private String decodePollutant(String mainPollutant) {
        switch (mainPollutant) {
            case "p2": return "PM 2.5";
            case "p1": return "PM 10";
            case "o3": return "Ozone";
            case "n2": return "Nitrogen Dioxide";
            case "s2": return "Sulfur Dioxide";
            case "co": return "Carbon Monoxide";
            default: return "Unknown Pollutant";
        }
    }
}
