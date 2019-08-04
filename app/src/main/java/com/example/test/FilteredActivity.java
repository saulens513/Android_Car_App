package com.example.test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.patloew.rxlocation.RxLocation;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FilteredActivity extends AppCompatActivity {

    RxPermissions rxPerm = new RxPermissions(this);
    ListView list;  //here list is global so that it can be seen by listeners
    String plateSelected="Visi";    //sorter will use this to sort number plate-wise
    String distanceSelected="Visi"; //sorter will use this to sort battery distance-wise

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_list:
                    //go to list activity
                    Intent openList = new Intent(FilteredActivity.this, CarList.class);
                    FilteredActivity.this.startActivity(openList);
                    return true;
                case R.id.navigation_map:
                    //go to map activity
                    Intent openMap = new Intent(FilteredActivity.this, MapsActivity.class);
                    FilteredActivity.this.startActivity(openMap);
                    return true;
                case R.id.navigation_filter:
                    //do nothing
                    return true;
            }
            return false;
        }
    };

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered);

        //get list from layout
        list= (ListView) findViewById(R.id.filteredList) ;

        //get drop down lists from layout
        Spinner batteryFilterBox = findViewById(R.id.filterBattery);
        ArrayList<String> batChoice = new ArrayList<String>();

        Spinner numPlateBox = findViewById(R.id.plate);
        ArrayList<String> plateChoice = new ArrayList<String>();

        //add content to drop down list
        batChoice.add("Visi");
        batChoice.add("Iki 20km");
        batChoice.add("Iki 50km");
        batChoice.add("Iki 100km");

        //add content to drop down list
        plateChoice.add("Visi");

        //getting permission to connect to internet using rxPermissions
        rxPerm.request(Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION).subscribe(isGranted ->{
            if(isGranted) {
                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl("https://development.espark.lt")
                        .addConverterFactory(GsonConverterFactory.create());
                Retrofit retrofit = builder.build();

                GetData client = retrofit.create(GetData.class);

                //request list
                Call<List<Car>> call = client.getAllCars();

                //get results from request
                call.enqueue(new Callback<List<Car>>() {
                    @Override
                    public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                        //car list that i got from API
                        List<Car> cars = response.body();

                        //check location permission
                        if(ContextCompat.checkSelfPermission(FilteredActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                            //location manager
                            LocationManager locManager = (LocationManager) FilteredActivity.this.getSystemService(Context.LOCATION_SERVICE);
                            Criteria criteria = new Criteria();
                            String provider = String.valueOf(locManager.getBestProvider(criteria, true));

                            //get location update
                            locManager.requestLocationUpdates(provider, 1000, 0, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    for(Car car: cars){
                                        //get location
                                        Location loc = new Location("");
                                        loc.setLatitude(car.getLocation().getLatitude());
                                        loc.setLongitude(car.getLocation().getLongitude());

                                        //get distance
                                        String formatDistance = new DecimalFormat("##.##").format((location.distanceTo(loc)/1000));

                                        //save distance
                                        car.setDistance(formatDistance);

                                        //put it next to title
                                        String title = car.getModel().getTitle() + ", "+car.getDistance()+"km";
                                        car.getModel().setTitle(title);
                                    }
                                    //sort list
                                    Collections.sort(cars, new Comparator<Car>() {
                                        @Override
                                        public int compare(Car car, Car t1) {
                                            return Float.parseFloat(car.getDistance())<Float.parseFloat(t1.getDistance()) ? -1
                                                    :Float.parseFloat(car.getDistance())>Float.parseFloat(t1.getDistance()) ? 1
                                                    : 0;
                                        }
                                    });
                                    updateList(list, cars);

                                    //stop getting location
                                    locManager.removeUpdates(this);
                                }

                                @Override
                                public void onStatusChanged(String s, int i, Bundle bundle) {

                                }

                                @Override
                                public void onProviderEnabled(String s) {

                                }

                                @Override
                                public void onProviderDisabled(String s) {

                                }
                            });
                        }

                        //get content for number plate drow down list from our car list
                        for(int i=0; i<cars.size();i++){
                            plateChoice.add(cars.get(i).getPlateNumber());
                        }

                        //fill number plate drop down list
                        ArrayAdapter<String> plateAdapter = new ArrayAdapter<String>(FilteredActivity.this,R.layout.support_simple_spinner_dropdown_item,plateChoice);
                        numPlateBox.setAdapter(plateAdapter);

                        //fill battery life drop down lis
                        ArrayAdapter<String> batAdapter = new ArrayAdapter<String>(FilteredActivity.this,R.layout.support_simple_spinner_dropdown_item,batChoice);
                        batteryFilterBox.setAdapter(batAdapter);

                        //listen to selections battery life selection
                        batteryFilterBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                List<Car> filteredCars = new ArrayList<Car>();
                                String itemStr = (String) adapterView.getItemAtPosition(i);
                                distanceSelected = itemStr;
                                filteredCars = filterList(cars);
                                updateList(list, filteredCars);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                //nothing happens if you dont choose anything
                            }
                        });

                        //listen to number plate selection
                        numPlateBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                List<Car> filteredCars = new ArrayList<Car>();
                                String itemStr = (String) adapterView.getItemAtPosition(i);
                                plateSelected = itemStr;
                                filteredCars = filterList(cars);
                                updateList(list, filteredCars);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                //nothing happens if you dont choose anything
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<List<Car>> call, Throwable t) {
                        Log.e("Retrofit error", t.toString());
                    }
                });
            }else{
                Toast.makeText(this, "Reikia leidimo prie interneto prieigos", Toast.LENGTH_LONG).show();
            }
        });
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void updateList(ListView view, List<Car> list){
        listAdapter la = new listAdapter(FilteredActivity.this, list);
        view.setAdapter(la);
    }

    public List<Car> filterList(List<Car> fullList){
        List<Car> filteredCarList = new ArrayList<Car>();
        String numberStr;
        Integer itemValue;

        //Toast.makeText(FilteredActivity.this, "Filtruojama...", Toast.LENGTH_LONG).show();

        //if no matter both just show all cars
        if(distanceSelected.equals("Visi")&&plateSelected.equals("Visi")){
            filteredCarList=fullList;
        }else{
            //if no matter distance, only filter by plate number
            if(distanceSelected.equals("Visi")){
                for(Car car:fullList){
                    if(car.getPlateNumber().equals(plateSelected)){
                        filteredCarList.add(car);
                    }
                }
            }else{
                //if plate no matter, only filter by distance
                if(plateSelected.equals("Visi")){
                    numberStr = distanceSelected.replaceAll("\\D+","");
                    itemValue = Integer.parseInt(numberStr);
                    for(Car car: fullList){
                        if(car.getBatteryEstimatedDistance()<itemValue){
                            filteredCarList.add(car);
                        }
                    }
                }else{
                    //if both matter, sort by both
                    numberStr = distanceSelected.replaceAll("\\D+","");
                    itemValue = Integer.parseInt(numberStr);
                    for(Car car:fullList){
                        if(car.getPlateNumber().equals(plateSelected)&&car.getBatteryEstimatedDistance()<itemValue){
                            filteredCarList.add(car);
                        }
                    }
                }
            }
        }
        return filteredCarList;
    }
    
}
