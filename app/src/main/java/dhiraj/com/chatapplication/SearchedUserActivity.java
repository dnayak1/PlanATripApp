package dhiraj.com.chatapplication;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class SearchedUserActivity extends AppCompatActivity {
    ImageView imageViewSearchedFriend;
    TextView textViewName;
    TextView textViewSex;
    Button buttonDone;
    Button buttonSendFriendRequest;
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    User user, currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_user);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setTitle("Search Detail");
        imageViewSearchedFriend = (ImageView) findViewById(R.id.imageView);
        textViewName = (TextView) findViewById(R.id.textViewSearchedNameValue);
        textViewSex = (TextView) findViewById(R.id.textViewSearchedSexValue);
        buttonDone = (Button) findViewById(R.id.buttonSearchedDone);
        buttonSendFriendRequest = (Button) findViewById(R.id.buttonSendFriendRequest);
        user = (User) getIntent().getExtras().getSerializable("user");
        textViewName.setText(user.getFirstName() + " " + user.getLastName());
        textViewSex.setText(user.getSex());
        Picasso.with(this).load(user.getImage()).into(imageViewSearchedFriend);
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final DatabaseReference currentUserDatabaseReference = mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid());
        currentUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = new User();
                currentUser.setUserId(mAuth.getCurrentUser().getUid());
                currentUser.setImage(dataSnapshot.child("image").getValue().toString());
                currentUser.setLastName(dataSnapshot.child("lastName").getValue().toString());
                currentUser.setFirstName(dataSnapshot.child("firstName").getValue().toString());
                currentUser.setSex(dataSnapshot.child("sex").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSendFriendRequest.setEnabled(true);
                finish();
            }
        });
        buttonSendFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SearchedUserActivity.this, "Friend request sent", Toast.LENGTH_SHORT).show();
                DatabaseReference databaseReference = mDatabase.getReference("users").child(user.getUserId());
                DatabaseReference friendReference = databaseReference.child("friends");
                DatabaseReference pendingRequest = friendReference.child("pendingRequest");
                pendingRequest.push().setValue(currentUser);
                DatabaseReference sentDatabaseReference = mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid());
                DatabaseReference sentFriendDatabaseReference = sentDatabaseReference.child("friends");
                DatabaseReference sentFriendRequestDatabaseReference = sentFriendDatabaseReference.child("sentRequest");
                sentFriendRequestDatabaseReference.push().setValue(user);
                buttonSendFriendRequest.setEnabled(false);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        buttonSendFriendRequest.setEnabled(true);
        DatabaseReference checkDatabaseReference = mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
                .child("friends").child("sentRequest");
        Query query = checkDatabaseReference.orderByChild("userId").equalTo(user.getUserId());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null)
                    buttonSendFriendRequest.setEnabled(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        buttonSendFriendRequest.setEnabled(true);
        super.onBackPressed();
    }
}
