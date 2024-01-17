package com.example.quizgeneration;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class SanPabloActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap map;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean markerClicked = false;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_san_pablo);

        // Check for location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, proceed with map initialization
            initializeMap();
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with map initialization
                initializeMap();
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeMap() {
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(null);
        mapView.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Load custom map style if mapId is not empty
        String mapId = "a4b97bf0d5b15220";
        if (!TextUtils.isEmpty(mapId)) {
            MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.custom_map_style);
            map.setMapStyle(style);
        }

        // Set a custom map click listener to handle clicks on the map
        map.setOnMapClickListener(latLng -> {
            // Check if a marker was clicked, if yes, ignore the map click
            if (!markerClicked) {
                // Handle the map click event here
                // You can leave it empty or show a message to the user
            }
            markerClicked = false; // Reset the markerClicked flag
        });

        // Clear all markers to remove default markers
        map.clear();

        // Add personalized markers
        addCustomMarker(new LatLng(14.078793700767148, 121.32565090345645), "Sampalok Lake","Sampalok Lake","https://firebasestorage.googleapis.com/v0/b/qr-code-system-lasbsp.appspot.com/o/sanPablovids%2FSampalokJan8.mp4?alt=media&token=56d0155c-c5ef-4cc6-b6f1-c60c8809ef37");
        addCustomMarker(new LatLng(14.124175078568129, 121.36879092371947), "Yambo Lake","Yambo Lake","https://firebasestorage.googleapis.com/v0/b/qr-code-system-lasbsp.appspot.com/o/sanPablovids%2FPandin%26YamboJan8.mp4?alt=media&token=429c5d5d-28d3-499c-b7c7-f915de9e0b22");
        addCustomMarker(new LatLng(14.115956485705013, 121.3655637854843), "Pandin Lake","Pandin Lake","https://firebasestorage.googleapis.com/v0/b/qr-code-system-lasbsp.appspot.com/o/sanPablovids%2FPandin%26YamboJan8.mp4?alt=media&token=429c5d5d-28d3-499c-b7c7-f915de9e0b22");
        addCustomMarker(new LatLng(14.103509376824633, 121.3784443330367), "Kalibato Lake","Calibato Lake","https://firebasestorage.googleapis.com/v0/b/qr-code-system-lasbsp.appspot.com/o/sanPablovids%2FCalibatoJan8.mp4?alt=media&token=fa9d017a-2b0d-4a39-9795-a3ef1eadb947");
        addCustomMarker(new LatLng(14.111645878010773, 121.3398106094073), "Palakpakin Lake","Palakpakin Lake","https://firebasestorage.googleapis.com/v0/b/qr-code-system-lasbsp.appspot.com/o/sanPablovids%2Fpalakpakin%20lake%20final.mp4?alt=media&token=0229b156-b8bd-4df9-b3ab-7b64b159cf60");
        addCustomMarker(new LatLng(14.12472461151796, 121.33595778296849), "Mojicap Lake","Mojicap Lake","https://firebasestorage.googleapis.com/v0/b/qr-code-system-lasbsp.appspot.com/o/sanPablovids%2Fmuhikap%20lake%20final.mp4?alt=media&token=733f2b17-72ec-42b6-93bb-153450f07002&_gl=1*k7s9ot*_ga*MjEyMzk1ODE1LjE2OTUzODYyMTg.*_ga_CW55HF8NVT*MTY5OTI3MTExMy4yNTkuMS4xNjk5Mjc2OTM3LjQyLjAuMA..");
        addCustomMarker(new LatLng(14.081460149528116, 121.34313853679295), "Bunot Lake","Bunot Lake","https://firebasestorage.googleapis.com/v0/b/qr-code-system-lasbsp.appspot.com/o/sanPablovids%2FBunotJan8.mp4?alt=media&token=bd62ea05-25bb-4f3f-8d13-1e9527b99ba8");
        addCustomMarker(new LatLng(14.070211032928636, 121.32587885916809), "Library Hub, San Pablo City","Library Hub, San Pablo City","https://firebasestorage.googleapis.com/v0/b/qr-code-system-lasbsp.appspot.com/o/sanPablovids%2FLibraryJan8.mp4?alt=media&token=7710a3e0-52ce-4b88-8324-de6f2e2f6c1a");
        addCustomMarker(new LatLng(14.069800702871225, 121.3257689702928), "Mangga tree, San Pablo City","Mangga tree, San Pablo City","https://firebasestorage.googleapis.com/v0/b/qr-code-system-lasbsp.appspot.com/o/sanPablovids%2Fmangga%20tree.mp4?alt=media&token=19e84b2f-1742-4af0-a8c9-d0bfe1b09a86&_gl=1*y3zjcv*_ga*MjEyMzk1ODE1LjE2OTUzODYyMTg.*_ga_CW55HF8NVT*MTY5OTI3MTExMy4yNTkuMS4xNjk5Mjc3MDU5LjkuMC4w");
        addCustomMarker(new LatLng(14.069810655093532, 121.32669459265487), "Cathedral Parish of Saint Paul the First Hermit","Cathedral Parish of Saint Paul the First Hermit","https://firebasestorage.googleapis.com/v0/b/qr-code-system-lasbsp.appspot.com/o/sanPablovids%2FCathedral.mp4?alt=media&token=b5036e2e-04c0-4a90-84b8-b583d6f25bc2");
        addCustomMarker(new LatLng(14.067693642089822, 121.29541317485223),"PRUDENCIA D. FULE MEMORIAL NATIONAL HIGH SCHOOL","PRUDENCIA D. FULE MEMORIAL NATIONAL HIGH SCHOOL","https://firebasestorage.googleapis.com/v0/b/qr-code-system-lasbsp.appspot.com/o/sanPablovids%2FPrudenciaJan8.mp4?alt=media&token=d9b4768f-5119-4517-976d-0de3e71148d3");
        addCustomMarker(new LatLng(14.074224786304171, 121.32645479564549),"Andres Bonifacio Monument, San Pablo City","Andres Bonifacio Monument, San Pablo City","https://firebasestorage.googleapis.com/v0/b/qr-code-system-lasbsp.appspot.com/o/sanPablovids%2FBonifacioJan8.mp4?alt=media&token=66fd69af-41da-434f-81a7-bf039e4244c5");
        addCustomMarker(new LatLng(14.071803299274222, 121.32259413624742),"Fule Malvar Ancestral Mansion","Fule Malvar Ancestral Mansion","https://firebasestorage.googleapis.com/v0/b/qr-code-system-lasbsp.appspot.com/o/sanPablovids%2Fwhite%20house%20final.mp4?alt=media&token=66a12cdf-d4bb-4c14-af21-fcf6cfd975d8&_gl=1*1mznxps*_ga*MjEyMzk1ODE1LjE2OTUzODYyMTg.*_ga_CW55HF8NVT*MTY5OTI3MTExMy4yNTkuMS4xNjk5Mjc3MTU2LjYwLjAuMA..");

        setMarkerClickListener();

        // Enable zoom controls
        map.getUiSettings().setZoomControlsEnabled(true);

        // Enable zoom gestures
        map.getUiSettings().setZoomGesturesEnabled(true);

        // Enable compass
        map.getUiSettings().setCompassEnabled(true);

        // Enable rotate gestures
        map.getUiSettings().setRotateGesturesEnabled(true);

        // Enable scroll gestures
        map.getUiSettings().setScrollGesturesEnabled(true);
    }

    private void addCustomMarker(LatLng position, String title, String locationName, String videoUrl) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(title)
                .snippet(locationName) // Set the location name as the snippet
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        Marker marker = map.addMarker(markerOptions);
        marker.setTag(videoUrl);
    }
    private void setMarkerClickListener() {
        map.setOnMarkerClickListener(marker -> {
            markerClicked = true;

            String markerTitle = marker.getTitle();
            String videoUrl = (String) marker.getTag();

            if (videoUrl != null) {
                Intent intent = new Intent(SanPabloActivity.this, VideoInfoActivity.class);
                intent.putExtra("markerTitle", markerTitle);
                intent.putExtra("videoUrl", videoUrl);
                startActivity(intent);
            }

            return true;
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}