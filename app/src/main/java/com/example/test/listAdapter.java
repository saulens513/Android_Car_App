package com.example.test;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class listAdapter extends ArrayAdapter {
    private final Activity context;
    private List<Car> values;
    String imageURL;
    String isCharging;

    //constructor
    public listAdapter(Activity context, List<Car> values){
        super(context, R.layout.car_list, values);

        this.context=context;
        this.values=values;
    }

    //fill the list
    public View getView(int position, View view, ViewGroup parent){
        //list row
        View listEntry = view;

        //add layout i made for my list entry
        if(listEntry == null){
            LayoutInflater inflater = (LayoutInflater) context.getLayoutInflater();
            listEntry = inflater.inflate(R.layout.car_list, null, true);
        }

        //get list entry fields - image and three rows
        ImageView carImage = (ImageView) listEntry.findViewById(R.id.car_image);
        TextView carNameField = (TextView) listEntry.findViewById(R.id.car_name);
        TextView carBatteryField = (TextView) listEntry.findViewById(R.id.car_battery);
        TextView carLocationField = (TextView) listEntry.findViewById(R.id.car_location);

        //get car
        Car item = values.get(position);

        //getting info if its being charged
        if(item.getIsCharging()){
            isCharging="Ikraunamas";
        }else isCharging="Neikraunamas";

        //get image link
        imageURL = item.getModel().getPhotoUrl();

        //download image - async
        ImageDownloader imgDownload = new ImageDownloader(carImage);
        imgDownload.execute(imageURL);

        //fill info
        carNameField.setText(item.getModel().getTitle()+", "+item.getPlateNumber());
        carBatteryField.setText(item.getBatteryPercentage()+"%, "+item.getBatteryEstimatedDistance()+"km, "+isCharging);
        carLocationField.setText(item.getLocation().getAddress());

        //add list entry
        return listEntry;
    }
}
