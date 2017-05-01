package dhiraj.com.chatapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Trip trip;
    ArrayList<FavoritePlace> places;
    PolylineOptions polylineOptions;
    private LocationManager mLocationManager;
    Double latitude;
    Double longitude;
    List<Address> addresses;
    Polyline polyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        trip= (Trip) getIntent().getExtras().getSerializable("trip");
        places= (ArrayList<FavoritePlace>) getIntent().getExtras().getSerializable("places");
        polylineOptions=new PolylineOptions();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
        polylineOptions.add(new LatLng(latitude, longitude));
        polyline=mMap.addPolyline(polylineOptions);
        for (FavoritePlace favoritePlace : places) {
             mMap.addMarker(new MarkerOptions().position(new LatLng(favoritePlace.getLatitude(), favoritePlace.getLongitude())));
             polylineOptions.add(new LatLng(favoritePlace.getLatitude(), favoritePlace.getLongitude())).color(Color.BLUE);
             polyline=mMap.addPolyline(polylineOptions);
        }
        Geocoder geocoder=new Geocoder(this);
        try {
            addresses= geocoder.getFromLocationName(trip.getLocation(),1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address=addresses.get(0);
        LatLng location = new LatLng(address.getLatitude(),address.getLongitude());
        mMap.addMarker(new MarkerOptions().position(location).title(address.getLocality()));
        polylineOptions.add(new LatLng(location.latitude,location.longitude)).color(Color.BLUE);
        polyline=mMap.addPolyline(polylineOptions);
        polylineOptions.add(new LatLng(latitude, longitude));
        polyline=mMap.addPolyline(polylineOptions);
        CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(latitude,longitude));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(18);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enable GPS");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            });
            builder.create().show();
        } else {
            Location location=getLastKnownLocation();
            if(location!=null){
                latitude=location.getLatitude();
                longitude=location.getLongitude();
            }
            Log.d("test", String.valueOf(location.getLatitude()));
        }
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
