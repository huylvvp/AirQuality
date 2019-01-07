package com.kevinlu.airquality;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.kevinlu.airquality.ListActivity.EXTRA_AQI_CN;
import static com.kevinlu.airquality.ListActivity.EXTRA_AQI_US;
import static com.kevinlu.airquality.ListActivity.EXTRA_CITY_NAME;
import static com.kevinlu.airquality.ListActivity.EXTRA_COORDINATES;
import static com.kevinlu.airquality.ListActivity.EXTRA_MAIN_POLLUTANT_CN;
import static com.kevinlu.airquality.ListActivity.EXTRA_MAIN_POLLUTANT_US;
import static com.kevinlu.airquality.ListActivity.EXTRA_TIMESTAMP;
import static com.kevinlu.airquality.ListActivity.EXTRA_STATION_JSON;

public class CityActivity extends AppCompatActivity {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_token));
        setContentView(R.layout.activity_city);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String cityName = intent.getStringExtra(EXTRA_CITY_NAME);
        String coordinates = intent.getStringExtra(EXTRA_COORDINATES);
        String timestamp = intent.getStringExtra(EXTRA_TIMESTAMP);
        String aqiUS = intent.getStringExtra(EXTRA_AQI_US);
        String mainPollutantUS = intent.getStringExtra(EXTRA_MAIN_POLLUTANT_US);
        String aqiCN = intent.getStringExtra(EXTRA_AQI_CN);
        String mainPollutantCN = intent.getStringExtra(EXTRA_MAIN_POLLUTANT_CN);
        String stationJSON = intent.getStringExtra(EXTRA_STATION_JSON);
        Station station = new Gson().fromJson(stationJSON, Station.class);

        ImageView imageView = findViewById(R.id.cityImage);
//        TextView textViewDescription = findViewById(R.id.cityDescription);
        TextView textViewCoordinates = findViewById(R.id.cityCoordinates);
        TextView textViewTimestamp = findViewById(R.id.cityTimestamp);
        TextView textViewAQIUS = findViewById(R.id.cityAQIUS);
        TextView textViewMainPollutantUS = findViewById(R.id.cityMainPollutantUS);
        TextView textViewAQICN = findViewById(R.id.cityAQICN);
        TextView textViewMainPollutantCN = findViewById(R.id.cityMainPollutantCN);

        Toolbar toolbar = findViewById(R.id.cityToolbar);
        toolbar.setTitle(cityName);

//        textViewDescription.setText("Hello there, the city is... ??? hmmmmm");
        textViewCoordinates.setText(coordinates);
        textViewTimestamp.setText(timestamp);
        textViewAQIUS.setText(aqiUS);
        textViewMainPollutantUS.setText(mainPollutantUS);
        textViewAQICN.setText(aqiCN);
        textViewMainPollutantCN.setText(mainPollutantCN);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                addStationMarker(station, mapboxMap);
            }
        });

        loadHeaderImageFromUnsplash(cityName, imageView);
        //Picasso.get().load("https://dynamicmedia.zuza.com/zz/m/original_/4/c/4cbceb84-5129-4275-a4bc-7ef0507acf43/N_M_Misc_Skyline_4637_rb_8_Super_Portrait.jpg").into(imageView);
    }

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

    private void addStationMarker(Station station, MapboxMap mapboxMap) {
        double lat = station.getData().getLocation().getCoordinates().get(0);
        double lng = station.getData().getLocation().getCoordinates().get(1);
        int aqius = station.getData().getCurrent().getPollution().getAqius();
        Icon icon = getMarkerIconAQIUS(aqius);
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(station.getData().getCity()).snippet("AQI: " + aqius).setIcon(icon));
    }

    private Icon getMarkerIconAQIUS(int aqius) {
        IconFactory iconFactory = IconFactory.getInstance(CityActivity.this);
        Icon icon;
        if (aqius >= 0 && aqius <= 50) {
            icon = iconFactory.fromResource(R.drawable.ic_map_marker_good);
        } else if (aqius >= 51 && aqius <= 100) {
            icon = iconFactory.fromResource(R.drawable.ic_map_marker_moderate);
        } else if (aqius >= 101 && aqius <= 150) {
            icon = iconFactory.fromResource(R.drawable.ic_map_marker_unhealthyforsensitivegroups);
        } else if (aqius >= 151 && aqius <= 200) {
            icon = iconFactory.fromResource(R.drawable.ic_map_marker_unhealthy);
        } else if (aqius >= 201 && aqius <= 300) {
            icon = iconFactory.fromResource(R.drawable.ic_map_marker_veryunhealthy);
        } else if (aqius >= 301) {
            icon = iconFactory.fromResource(R.drawable.ic_map_marker_hazardous);
        } else {
            icon = iconFactory.fromResource(R.drawable.ic_map_marker_error);
        }
        return icon;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
