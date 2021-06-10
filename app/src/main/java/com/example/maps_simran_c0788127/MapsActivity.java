package com.example.maps_simran_c0788127;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final int REQUEST_CODE = 1;
    private Marker homeMarker;
    private Marker destMarker;
    public static Integer touchCount = 0;

    public static Double homeLat = 0.0;
    public static Double homeLong = 0.0;

    Polygon shape;
    private static final int POLYGON_SIDES = 4;
    List<Marker> markers = new ArrayList();
    public static Double destLat = 0.0;
    public static Double destLong = 0.0;

    // location with location manager and listener
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setHomeMarker(location);

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

        if (!hasLocationPermission())
            requestLocationPermission();
        else
            startUpdateLocation();


        // apply long press gesture
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // set marker
                touchCount += 1;
                setMarker(latLng);

            }

            private void setMarker(LatLng latLng) {

                MarkerOptions options = null;


                if (touchCount == 1) {

                    float dist[] = new float[1];
                    Location.distanceBetween(homeLat, homeLong, latLng.latitude, latLng.longitude, dist);

                    options = new MarkerOptions().position(latLng)
                            .title("A").draggable(true).snippet("Distance is " + String.valueOf(dist[0]));
                    markers.add(mMap.addMarker(options));

                } else if (touchCount == 2) {
                    float dist[] = new float[1];
                    Location.distanceBetween(homeLat, homeLong, latLng.latitude, latLng.longitude, dist);

                    options = new MarkerOptions().position(latLng)
                            .title("B").draggable(true).snippet("Distance is " + String.valueOf(dist[0]));
                    markers.add(mMap.addMarker(options));
                } else if (touchCount == 3) {

                    float dist[] = new float[1];
                    Location.distanceBetween(homeLat, homeLong, latLng.latitude, latLng.longitude, dist);

                    options = new MarkerOptions().position(latLng)
                            .title("C").draggable(true).snippet("Distance is " + String.valueOf(dist[0]));
                    markers.add(mMap.addMarker(options));
                } else if (touchCount == 4) {
                    float dist[] = new float[1];
                    Location.distanceBetween(homeLat, homeLong, latLng.latitude, latLng.longitude, dist);

                    options = new MarkerOptions().position(latLng)
                            .title("D").draggable(true).snippet("Distance is " + String.valueOf(dist[0]));
                    markers.add(mMap.addMarker(options));
                }


                if (markers.size() == POLYGON_SIDES)
                    drawShape();

                markers.get(0).getPosition();

            }

            private void drawShape() {
                PolygonOptions options = new PolygonOptions()
                        .fillColor(0x5F00FF00)
                        .strokeColor(Color.RED)
                        .strokeWidth(5);


                for (int i=0; i<POLYGON_SIDES; i++) {
                    options.add(markers.get(i).getPosition());
                }

                shape = mMap.addPolygon(options);
                shape.setClickable(true);

            }


        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                showAddress(marker.getPosition());
                return false;
            }
        });


    }

    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);

    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void setHomeMarker(Location location) {
        homeLat = location.getLatitude();
        homeLong = location.getLongitude();

        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userLocation)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("Your Location");
        homeMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        }
    }
    public void showAddress(LatLng latLng){

        destLat = latLng.latitude;
        destLong = latLng.longitude;

        String address = "Could not find the address";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(destLat, destLong, 1);
            if (addressList != null && addressList.size() > 0) {
                address = "\n";

                // street name
                if (addressList.get(0).getThoroughfare() != null)
                    address += addressList.get(0).getThoroughfare() + "\n";
                if (addressList.get(0).getLocality() != null)
                    address += addressList.get(0).getLocality() + " ";
                if (addressList.get(0).getPostalCode() != null)
                    address += addressList.get(0).getPostalCode() + " ";
                if (addressList.get(0).getAdminArea() != null)
                    address += addressList.get(0).getAdminArea();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(),"Address : "+address,Toast.LENGTH_LONG).show();

    }


}