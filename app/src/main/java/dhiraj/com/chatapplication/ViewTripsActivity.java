package dhiraj.com.chatapplication;

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
        mAuth=FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=mDatabase.getReference("trips");
        Query query=databaseReference.orderByChild("trips");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tripArrayList=new ArrayList<Trip>();
                for(DataSnapshot tripSnapshot:dataSnapshot.getChildren()){
                    Trip trip=new Trip();
                    HashMap<String,User> userHashMap= (HashMap<String, User>) tripSnapshot.child("viewTripUsers").getValue();
                    Iterator it = userHashMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        if(pair.getKey().toString().equals(mAuth.getCurrentUser().getUid())){
                            trip=new Trip();
                            trip.setTitle(tripSnapshot.child("title").getValue().toString());
                            trip.setLocation(tripSnapshot.child("location").getValue().toString());
                            trip.setImage(tripSnapshot.child("image").getValue().toString());
                            trip.setCreatedBy(tripSnapshot.child("createdBy").getValue().toString());
                            trip.setId(tripSnapshot.getKey());
                            HashMap<String,User> joinedUserHashMap= (HashMap<String, User>) tripSnapshot.child("joinedUsers").getValue();
                            Iterator iterator=joinedUserHashMap.entrySet().iterator();
                            while (iterator.hasNext()){
                                Map.Entry entry= (Map.Entry) iterator.next();
                                if(entry.getKey().toString().equals(pair.getKey())){
                                    trip.setJoined(true);
                                }
                            }
                        }
                    }

                    if(trip!=null && trip.getId()!=null){
                        tripArrayList.add(trip);
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

    }

    @Override
    public void chatRoom(Trip trip) {

    }
}
