package dhiraj.com.chatapplication;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceGroup;
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

public class PendingRequestActivity extends AppCompatActivity implements PendingRecyclerAdapter.IPendingListener{
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    ArrayList<User> pendingUserList;
    RecyclerView recyclerViewPending;
    PendingRecyclerAdapter pendingRecyclerAdapter;
    LinearLayoutManager layoutManager;
    String delete;
    User loggedUser;
    ProgressBar progressBarPendingRequest;
    Button buttonDone;
    TextView textViewNoPendingRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_request);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setTitle("Pending Request");
        progressBarPendingRequest= (ProgressBar) findViewById(R.id.progressBarPendingRequest);
        buttonDone= (Button) findViewById(R.id.buttonPendingRequestDone);
        textViewNoPendingRequest= (TextView) findViewById(R.id.textViewNoPendingRequest);
        recyclerViewPending= (RecyclerView) findViewById(R.id.recyclerViewPending);
        progressBarPendingRequest.setVisibility(View.VISIBLE);
        textViewNoPendingRequest.setVisibility(View.INVISIBLE);
        mDatabase=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();
        loggedUser= (User) getIntent().getExtras().getSerializable("loggedUser");
        DatabaseReference databaseReference= mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
                .child("friends").child("pendingRequest");
        Query query=databaseReference.orderByChild("pendingRequest");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBarPendingRequest.setVisibility(View.INVISIBLE);
                pendingUserList=new ArrayList<User>();
                if(dataSnapshot.getValue()!=null){
                    for(DataSnapshot userSnapshot:dataSnapshot.getChildren()){
                        User user=new User();
                        user.setFirstName(userSnapshot.child("firstName").getValue().toString());
                        user.setImage(userSnapshot.child("image").getValue().toString());
                        user.setLastName(userSnapshot.child("lastName").getValue().toString());
                        user.setUserId(userSnapshot.child("userId").getValue().toString());
                        user.setSex(userSnapshot.child("sex").getValue().toString());
                        pendingUserList.add(user);
                        pendingRecyclerAdapter=new PendingRecyclerAdapter(PendingRequestActivity.this,pendingUserList,PendingRequestActivity.this);
                        recyclerViewPending.setAdapter(pendingRecyclerAdapter);
                        layoutManager=new LinearLayoutManager(PendingRequestActivity.this);
                        recyclerViewPending.setLayoutManager(layoutManager);
                        pendingRecyclerAdapter.notifyDataSetChanged();
                    }
                }
                else {
                    textViewNoPendingRequest.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void acceptUser(final User user) {
        Toast.makeText(this, "Friend added", Toast.LENGTH_SHORT).show();
        DatabaseReference acceptDatabaseReference = mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
                .child("friends").child("confirmedRequest");
        acceptDatabaseReference.push().setValue(user);
        final DatabaseReference deleteReference=mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
                .child("friends").child("pendingRequest");
        Query query=deleteReference.orderByChild("userId").equalTo(user.getUserId());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    for(DataSnapshot deleteSnapshot:dataSnapshot.getChildren())
                    delete=deleteSnapshot.getKey().toString().trim();
                    deleteReference.child(delete).removeValue();
                    pendingUserList.remove(user);
                    pendingRecyclerAdapter=new PendingRecyclerAdapter(PendingRequestActivity.this,pendingUserList,PendingRequestActivity.this);
                    recyclerViewPending.setAdapter(pendingRecyclerAdapter);
                    layoutManager=new LinearLayoutManager(PendingRequestActivity.this);
                    recyclerViewPending.setLayoutManager(layoutManager);
                    pendingRecyclerAdapter.notifyDataSetChanged();
                    if(pendingUserList.size()==0){
                        textViewNoPendingRequest.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference reference=mDatabase.getReference("users").child(user.getUserId()).child("friends").child("confirmedRequest");
        reference.push().setValue(loggedUser);
        DatabaseReference sentReference=mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid()).child("friends").child("sentRequest");
        sentReference.push().setValue(user);


    }

    @Override
    public void rejectUser(final User user) {
        Toast.makeText(this, "Friend deleted", Toast.LENGTH_SHORT).show();
        final DatabaseReference rejectReference=mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
                .child("friends").child("pendingRequest");
        Query query=rejectReference.orderByChild("userId").equalTo(user.getUserId());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    for(DataSnapshot deleteSnapshot:dataSnapshot.getChildren())
                        delete=deleteSnapshot.getKey().toString().trim();
                    rejectReference.child(delete).removeValue();
                    pendingUserList.remove(user);
                    pendingRecyclerAdapter=new PendingRecyclerAdapter(PendingRequestActivity.this,pendingUserList,PendingRequestActivity.this);
                    recyclerViewPending.setAdapter(pendingRecyclerAdapter);
                    layoutManager=new LinearLayoutManager(PendingRequestActivity.this);
                    recyclerViewPending.setLayoutManager(layoutManager);
                    pendingRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
