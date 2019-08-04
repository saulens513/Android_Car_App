package com.example.test;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarList extends AppCompatActivity {
    final RxPermissions rxPerm = new RxPermissions(this);

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_list:
                    return true;
                case R.id.navigation_map:
                    Intent openMap = new Intent(CarList.this, MapsActivity.class);
                    CarList.this.startActivity(openMap);
                    return true;
                case R.id.navigation_filter:
                    Intent openFilter = new Intent(CarList.this, FilteredActivity.class);
                    CarList.this.startActivity(openFilter);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);

        //get list from layout
        final ListView list = (ListView) findViewById(R.id.my_list);

        //getting permission to connect to internet using rxPermissions
        rxPerm.request(Manifest.permission.INTERNET).subscribe(isGranted ->{
            if(isGranted) {
                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl("https://development.espark.lt")
                        .addConverterFactory(GsonConverterFactory.create());
                Retrofit retrofit = builder.build();

                GetData client = retrofit.create(GetData.class);

                //request car list form API
                Call<List<Car>> call = client.getAllCars();

                //get request result
                call.enqueue(new Callback<List<Car>>() {
                    @Override
                    public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                        //form result into List and display it
                        List<Car> cars = response.body();
                        listAdapter la = new listAdapter(CarList.this, cars);
                        list.setAdapter(la);
                    }

                    @Override
                    public void onFailure(Call<List<Car>> call, Throwable t) {
                        //log the error
                        Log.e("Retrofit error:", t.toString());
                    }
                });
            }else{
                    Toast.makeText(this, "Reikia leidimo prie interneto prieigos", Toast.LENGTH_LONG).show();
                }
            });
            BottomNavigationView navView = findViewById(R.id.nav_view);
            navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
