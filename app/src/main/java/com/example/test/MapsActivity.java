package com.example.test;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    final RxPermissions rxPerm = new RxPermissions(this);
    private GoogleMap mMap;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_list:
                    Intent openList = new Intent(MapsActivity.this, CarList.class);
                    MapsActivity.this.startActivity(openList);
                    return true;
                case R.id.navigation_map:
                    return true;
                case R.id.navigation_filter:
                    Intent openFilter = new Intent(MapsActivity.this, FilteredActivity.class);
                    MapsActivity.this.startActivity(openFilter);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        rxPerm.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe(isGranted ->{
            if(isGranted) {
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);

            }else{
                Toast.makeText(this, "Reikia vietoves prieigos", Toast.LENGTH_LONG).show();
            }
        });
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://development.espark.lt")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        GetData client = retrofit.create(GetData.class);

        //request car list from API
        Call<List<Car>> call = client.getAllCars();

        //get request results
        call.enqueue(new Callback<List<Car>>() {
                         @Override
                         public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                             List<Car> cars = response.body();
                             FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);

                             if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                                 //location manager
                                 LocationManager locManager = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
                                 Criteria criteria = new Criteria();
                                 String provider = String.valueOf(locManager.getBestProvider(criteria, true));

                                 //request location
                                 locManager.requestLocationUpdates(provider, 1000, 0, new LocationListener() {
                                     @Override
                                     public void onLocationChanged(Location location) {
                                         //once we get location, put the marker on the map
                                         LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                                         mMap.addMarker(new MarkerOptions().position(position).title("Jus esate cia").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                         mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, (float)10.0));

                                         //stop receiving updates
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
                             //put car markes on the map
                             for (int i = 0; i < cars.size(); i++) {
                                 LatLng carLoc = new LatLng(cars.get(i).getLocation().getLatitude(), cars.get(i).getLocation().getLongitude());
                                 Location carLocation = new Location("");
                                 carLocation.setLongitude(carLoc.longitude);
                                 carLocation.setLatitude(carLoc.latitude);
                                 mMap.addMarker(new MarkerOptions().position(carLoc).title(cars.get(i).getModel().getTitle() + ", " + cars.get(i).getPlateNumber()));
                             }
                         }

                         @Override
                         public void onFailure(Call<List<Car>> call, Throwable t) {
                             Log.e("Retrofit error:", t.toString());
                         }
                     });
    }
}
