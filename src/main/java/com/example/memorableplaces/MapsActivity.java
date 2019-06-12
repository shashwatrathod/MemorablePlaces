package com.example.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    public static ArrayList<LatLng> latLng = new ArrayList<LatLng>();
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                showLocation(location, "Your Location");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        Intent intent = getIntent();
        int positionClicked = intent.getIntExtra("Position",0);
        if(positionClicked == 0 && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null ){
                Location lastLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                showLocation(lastLoc,"Your Location");
            }else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }else if(positionClicked!=0){
            placeMarker(positionClicked -1);
        }

        mMap.setOnMapLongClickListener(this);
    }

    private void showLocation(Location location, String title){
        if(location!=null){
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(ll).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll,13));
        }
    }

    private void placeMarker(int positionClicked) {
        mMap.clear();
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.get(positionClicked).latitude, latLng.get(positionClicked).longitude, 1);
            String title = addressList.get(0).getAddressLine(0);
            if (title.length() < 2) {
                title = "Nothing";
            }
            mMap.addMarker(new MarkerOptions().position(latLng.get(positionClicked)).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng.get(positionClicked), 13));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapLongClick(LatLng ll) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(),Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(ll.latitude, ll.longitude, 1);
            String title = addressList.get(0).getAddressLine(0);
            if (title.length() < 2) {
                title = "Nothing";
            }
            latLng.add(ll);
            MainActivity.arrayList.add(title);
            MainActivity.arrayAdapter.notifyDataSetChanged();
            SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.memorableplaces",Context.MODE_PRIVATE);
            try{
                sharedPreferences.edit().putString("arrayList",ObjectSerializer.serialize(MainActivity.arrayList)).apply();
                ArrayList<String> latitudes = new ArrayList<>();
                ArrayList<String> longitudes = new ArrayList<>();

                for(LatLng l : latLng){
                    latitudes.add(Double.toString(l.latitude));
                    longitudes.add(Double.toString(l.longitude));
                }
                sharedPreferences.edit().putString("latitudes",ObjectSerializer.serialize(latitudes)).apply();
                sharedPreferences.edit().putString("longitudes",ObjectSerializer.serialize(longitudes)).apply();
                Log.i("Lat",latitudes.toString());
            }catch(Exception e){
                e.printStackTrace();
            }

            placeMarker(latLng.indexOf(ll));

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
