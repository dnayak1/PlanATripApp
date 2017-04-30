package dhiraj.com.chatapplication;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        trip= (Trip) getIntent().getExtras().getSerializable("trip");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /*// Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        Geocoder geocoder=new Geocoder(this);
        try {
            List<Address> addressList=geocoder.getFromLocationName("charlotte",1);
            Address address=addressList.get(0);
            double lat=address.getLatitude();
            double lng=address.getLongitude();
            LatLng latLng=new LatLng(lat,lng);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),.5f));
            //CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngZoom(latLng,15);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
