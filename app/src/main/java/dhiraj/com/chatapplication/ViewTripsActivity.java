package dhiraj.com.chatapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ViewTripsActivity extends AppCompatActivity implements ViewTripsRecyclerAdapter.ITripsListener {
    RecyclerView recyclerViewViewTrips;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    ArrayList<Trip> tripArrayList;
    ViewTripsRecyclerAdapter viewTripsRecyclerAdapter;
    LinearLayoutManager layoutManager;
    Button buttonViewTripsDone;
    ProgressBar progressBarViewTrips;
    TextView textViewNoTripsAvailable;
    User loggedUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trips);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setTitle("Trips");
        recyclerViewViewTrips= (RecyclerView) findViewById(R.id.recyclerViewViewTrips);
        buttonViewTripsDone= (Button) findViewById(R.id.buttonViewTripDone);
        progressBarViewTrips= (ProgressBar) findViewById(R.id.progressBarViewTrip);
        textViewNoTripsAvailable= (TextView) findViewById(R.id.textViewNoTripsAvailable);
        textViewNoTripsAvailable.setVisibility(View.INVISIBLE);
        progressBarViewTrips.setVisibility(View.VISIBLE);
        loggedUser= (User) getIntent().getExtras().getSerializable("loggedUser");
        mAuth=FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=mDatabase.getReference("trips");
        Query query=databaseReference.orderByChild("trips");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tripArrayList=new ArrayList<Trip>();
                for(DataSnapshot tripSnapshot:dataSnapshot.getChildren()){
                    for(DataSnapshot userSnapshot:tripSnapshot.child("tripUsers").getChildren()){
                        if (userSnapshot.child("userId").getValue()!=null){
                            if(userSnapshot.child("userId").getValue().toString().equals(mAuth.getCurrentUser().getUid())){
                                Trip trip;
                                if(userSnapshot.child("joined").getValue().toString().equals("true")){
                                    trip=new Trip();
                                    trip.setCreatedBy(tripSnapshot.child("createdBy").getValue().toString());
                                    trip.setImage(tripSnapshot.child("image").getValue().toString());
                                    trip.setLocation(tripSnapshot.child("location").getValue().toString());
                                    trip.setTitle(tripSnapshot.child("title").getValue().toString());
                                    trip.setId(tripSnapshot.child("id").getValue().toString());
                                    trip.setJoined(true);
                                }
                                else{
                                    trip=new Trip();
                                    trip.setCreatedBy(tripSnapshot.child("createdBy").getValue().toString());
                                    trip.setImage(tripSnapshot.child("image").getValue().toString());
                                    trip.setLocation(tripSnapshot.child("location").getValue().toString());
                                    trip.setTitle(tripSnapshot.child("title").getValue().toString());
                                    trip.setId(tripSnapshot.child("id").getValue().toString());
                                    trip.setJoined(false);
                                }
                                if(trip!=null && trip.getId()!=null){
                                    tripArrayList.add(trip);
                                }
                            }
                        }
                        }
                }
                if ((tripArrayList!=null && tripArrayList.size()>0)){
                    progressBarViewTrips.setVisibility(View.INVISIBLE);
                    viewTripsRecyclerAdapter=new ViewTripsRecyclerAdapter(ViewTripsActivity.this,tripArrayList,ViewTripsActivity.this);
                    recyclerViewViewTrips.setAdapter(viewTripsRecyclerAdapter);
                    layoutManager=new LinearLayoutManager(ViewTripsActivity.this);
                    recyclerViewViewTrips.setLayoutManager(layoutManager);
                    viewTripsRecyclerAdapter.notifyDataSetChanged();
                }
                else{
                    progressBarViewTrips.setVisibility(View.INVISIBLE);
                    textViewNoTripsAvailable.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        buttonViewTripsDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void joinTrip(Trip trip) {
        DatabaseReference databaseReference=mDatabase.getReference("trips").child(trip.getId()).child("tripUsers").child(loggedUser.getUserId());
        loggedUser.setJoined(true);
        databaseReference.setValue(loggedUser);
        for(Trip trips:tripArrayList){
            if(trips.getId().equals(trip.getId())){
                trips.setJoined(true);
            }
        }
        Toast.makeText(this, "Trip joined!!!", Toast.LENGTH_SHORT).show();
        viewTripsRecyclerAdapter=new ViewTripsRecyclerAdapter(ViewTripsActivity.this,tripArrayList,ViewTripsActivity.this);
        recyclerViewViewTrips.setAdapter(viewTripsRecyclerAdapter);
        layoutManager=new LinearLayoutManager(ViewTripsActivity.this);
        recyclerViewViewTrips.setLayoutManager(layoutManager);
        viewTripsRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void chatRoom(Trip trip) {
        Intent intent=new Intent(ViewTripsActivity.this,ChatRoomActivity.class);
        intent.putExtra("selectedTrip",trip);
        intent.putExtra("loggedUser",loggedUser);
        startActivity(intent);
    }
}
