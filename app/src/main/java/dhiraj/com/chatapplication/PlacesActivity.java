package dhiraj.com.chatapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlacesActivity extends AppCompatActivity implements PlacesAdapter.IPlacesListener{
    RecyclerView recyclerViewPlaces;
    Button buttonPickPlaces,buttonRoundTrip, buttonPlacesDone;
    static  final int PLACE_PICKER_REQUEST = 1;
    ArrayList<FavoritePlace> favoritePlaces;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    PlacesAdapter placesAdapter;
    LinearLayoutManager layoutManager;
    Trip trip;
    TextView textViewNoFavoritePlace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setTitle("Places");
        recyclerViewPlaces= (RecyclerView) findViewById(R.id.recyclerViewPlaces);
        buttonPickPlaces= (Button) findViewById(R.id.buttonPickAPlace);
        buttonRoundTrip= (Button) findViewById(R.id.buttonRoundTrip);
        buttonPlacesDone= (Button) findViewById(R.id.buttonPlacesDone);
        textViewNoFavoritePlace= (TextView) findViewById(R.id.textViewNoFavoritePlaces);
        mDatabase=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();
        favoritePlaces=new ArrayList<>();
        trip= (Trip) getIntent().getExtras().getSerializable("trip");
        getPlaces();
        buttonPickPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(PlacesActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        buttonPlacesDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonRoundTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PlacesActivity.this,MapsActivity.class);
                intent.putExtra("places",favoritePlaces);
                intent.putExtra("trip",trip);
                startActivity(intent);
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this,data);
                String name=place.getName().toString();
                LatLng latLng=place.getLatLng();
                double latitude=latLng.latitude;
                double longitude=latLng.longitude;
                final FavoritePlace favoritePlace=new FavoritePlace();
                favoritePlace.setPlaceName(name);
                favoritePlace.setLatitude(latitude);
                favoritePlace.setLongitude(longitude);
                setPlaces(favoritePlace);
            }
        }
    }
    public void setPlaces(FavoritePlace favPlace){
        DatabaseReference databaseReference=mDatabase.getReference("trips").child(trip.getId()).child("places").push();
        favPlace.setPlaceId(databaseReference.getKey());
        databaseReference.setValue(favPlace);
    }

    public void getPlaces(){
//        favoritePlaces=new ArrayList<>();
        DatabaseReference placeReference=mDatabase.getReference("trips").child(trip.getId()).child("places");
        placeReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FavoritePlace favoritePlace=dataSnapshot.getValue(FavoritePlace.class);
                favoritePlaces.add(favoritePlace);
                placesAdapter=new PlacesAdapter(PlacesActivity.this,favoritePlaces,PlacesActivity.this);
                recyclerViewPlaces.setAdapter(placesAdapter);
                layoutManager=new LinearLayoutManager(PlacesActivity.this);
                recyclerViewPlaces.setLayoutManager(layoutManager);
                placesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                    placesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void deletePlaces(final FavoritePlace favoritePlace) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Do you want to remove this place from your favorites");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference reference=mDatabase.getReference("trips").child(trip.getId()).child("places");
                reference.child(favoritePlace.getPlaceId()).setValue(null);
                favoritePlaces.remove(favoritePlace);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }
}
