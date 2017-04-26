package dhiraj.com.chatapplication;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class FriendsActivity extends AppCompatActivity implements GridRecyclerAdapter.IGridListener {
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    ArrayList<User> friendArrayList;
    GridRecyclerAdapter friendRecyclerAdapter;
    LinearLayoutManager friendLayoutManager;
    RecyclerView recyclerViewFriends;
    TextView textViewNoFriends;
    ProgressBar progressBarFriends;
    Button buttonBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setTitle("Friends");
        recyclerViewFriends= (RecyclerView) findViewById(R.id.recyclerViewFriends);
        textViewNoFriends= (TextView) findViewById(R.id.textViewNoFriends);
        progressBarFriends= (ProgressBar) findViewById(R.id.progressBarFriends);
        buttonBack= (Button) findViewById(R.id.buttonFriendsBack);
        mDatabase=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();
        progressBarFriends.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference=mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
                .child("friends").child("confirmedRequest");
        Query query=databaseReference.orderByChild("confirmedRequest");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBarFriends.setVisibility(View.INVISIBLE);
                if(dataSnapshot.getValue()==null){
                    textViewNoFriends.setVisibility(View.VISIBLE);
                }
                else{
                    friendArrayList=new ArrayList<User>();
                    for(DataSnapshot friendSnapshot:dataSnapshot.getChildren()){
                        User user=new User();
                        user.setImage(friendSnapshot.child("image").getValue().toString());
                        user.setLastName(friendSnapshot.child("lastName").getValue().toString());
                        user.setSex(friendSnapshot.child("sex").getValue().toString());
                        user.setUserId(friendSnapshot.child("userId").getValue().toString());
                        user.setFirstName(friendSnapshot.child("firstName").getValue().toString());
                        friendArrayList.add(user);
                        friendRecyclerAdapter=new GridRecyclerAdapter(FriendsActivity.this,friendArrayList,FriendsActivity.this);
                        recyclerViewFriends.setAdapter(friendRecyclerAdapter);
                        friendLayoutManager=new LinearLayoutManager(FriendsActivity.this);
                        recyclerViewFriends.setLayoutManager(friendLayoutManager);
                        friendRecyclerAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void showDetail(User user) {

    }
}
