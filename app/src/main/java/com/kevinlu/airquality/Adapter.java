package com.kevinlu.airquality;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Adapter class extends the RecyclerView.Adapter class.
 * It adds and updates the RecyclerView with data from a ArrayList
 *
 * @author Kevin Lu <649859 @ pdsb.net>
 * @since JDK 1.8
 * @version 1.0
 *
 */

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private Context context;
    private List<Station> stationList;

    /**
     * This is the constructor for the Adapter class.
     * @param context - This is a Context object
     * @param stationList - This is an ArrayList of Station objects
     */
    public Adapter(Context context, List<Station> stationList) {
        this.context = context;
        this.stationList = stationList;
    }

    /**
     * @param parent
     * @param viewType
     * @return the ViewHolder object
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_listitem, null);
        return new ViewHolder(view);
    }

    /**
     * This function sets the information from the ArrayList of Station objects
     * @param holder - This is a ViewHolder object
     * @param position - This is the index of the Station in the ArrayList
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Grab the station from the station list
        Station station = stationList.get(position);

        //Create a String for the location of the station (City, Province, Country)
        String stationLocation = station.getData().getCity() + ", " + station.getData().getState() + ", " + station.getData().getCountry();
        holder.textViewTitle.setText(stationLocation);
//        List<Double> coordinates = station.getData().getLocation().getCoordinates();
//        String listString = coordinates.stream().map(Object::toString)
//                .collect(Collectors.joining(", "));
//        holder.textViewShortDesc.setText(listString);
        holder.textViewShortDesc.setText(decodePollutant(station.getData().getCurrent().getPollution().getMainus()));
        int aqius = station.getData().getCurrent().getPollution().getAqius();
        holder.textViewRating.setText(aqius + "");
        setColorAQIUS(holder, aqius);
        holder.textViewPrice.setText(rankAQIUS(aqius));
//        holder.imageView.setImageDrawable(context.getResources().getDrawable());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, CityActivity.class);
            context.startActivity(intent);
        });
    }

    /**
     * This method converts short form of pollutant names to its full form
     * @param mainPollutant - This is a String of the short form of the pollutant name
     * @return Full form of the short form pollutant
     */
    public String decodePollutant(String mainPollutant) {
        if (mainPollutant.equals("p2")) {
            return "PM 2.5";
        } else if (mainPollutant.equals("p1")) {
            return "PM 10";
        } else if (mainPollutant.equals("o3")) {
            return "Ozone";
        } else if (mainPollutant.equals("n2")) {
            return "Nitrogen Dioxide";
        } else if (mainPollutant.equals("s2")) {
            return "Sulfur Dioxide";
        } else if (mainPollutant.equals("co")) {
            return "Carbon Monoxide";
        } else {
            return "Unknown Pollutant";
        }
    }

    /**
     * This method converts a numerical AQI value to its severity ranking in words
     * @param aqius - This is the air quality index by U.S. EPA standards
     * @return the rank of the air quality index, a String
     */
    public String rankAQIUS(int aqius) {
        if (aqius >= 0 && aqius <= 50) {
            return "Good";
        } else if (aqius >= 51 && aqius <= 100) {
            return "Moderate";
        } else if (aqius >= 101 && aqius <= 150) {
            return "Unhealthy for Sensitive Groups";
        } else if (aqius >= 151 && aqius <= 200) {
            return "Unhealthy";
        } else if (aqius >= 201 && aqius <= 300) {
            return "Very Unhealthy";
        } else if (aqius >= 301) {
            return "Hazardous";
        } else {
            return "ERROR";
        }
    }

    /**
     * This method converts a numerical AQI value to its severity ranking in words
     * @param aqius - This is the air quality index by U.S. EPA standards
     * @return the rank of the air quality index, a String
     */
    public void setColorAQIUS(ViewHolder holder, int aqius) {
        if (aqius >= 0 && aqius <= 50) {
            holder.textViewRating.setBackgroundColor(ContextCompat.getColor(context, R.color.aqius_good));
        } else if (aqius >= 51 && aqius <= 100) {
            holder.textViewRating.setBackgroundColor(ContextCompat.getColor(context, R.color.aqius_moderate));
        } else if (aqius >= 101 && aqius <= 150) {
            holder.textViewRating.setBackgroundColor(ContextCompat.getColor(context, R.color.aqius_unhealthyforsensitivegroups));
        } else if (aqius >= 151 && aqius <= 200) {
            holder.textViewRating.setBackgroundColor(ContextCompat.getColor(context, R.color.aqius_unhealthy));
        } else if (aqius >= 201 && aqius <= 300) {
            holder.textViewRating.setBackgroundColor(ContextCompat.getColor(context, R.color.aqius_veryunhealthy));
        } else if (aqius >= 301) {
            holder.textViewRating.setBackgroundColor(ContextCompat.getColor(context, R.color.aqius_hazardous));
        } else {
            holder.textViewRating.setBackgroundColor(ContextCompat.getColor(context, R.color.colorError));
        }
    }

    /**
     * This method returns the size of the ArrayList
     * @return size of ArrayList, an Integer
     */
    @Override
    public int getItemCount() {
        return stationList.size();
    }

    /**
     * This ViewHolder class finds all the fields used in the RecyclerView
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
        }
    }

    public void filterList(ArrayList<Station> filteredList) {
        stationList = filteredList;
    }
}
