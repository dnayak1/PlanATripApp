package dhiraj.com.chatapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class WelcomeActivity extends AppCompatActivity implements GridRecyclerAdapter.IGridListener{
    EditText editTextSearchFriend;
    Button buttonSearch;
    RecyclerView recyclerViewSearchResult;
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    String search;
    GridRecyclerAdapter searchAdapter;
    ArrayList<User> userArrayList;
    LinearLayoutManager layoutManager;
    TextView textViewNoUserFound;
    ProgressBar progressBarSearch;
    User loggedUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().setTitle("Welcome");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        editTextSearchFriend= (EditText) findViewById(R.id.editTextWelcomeSearchFriends);
        buttonSearch= (Button) findViewById(R.id.buttonWelcomeSearch);
        textViewNoUserFound= (TextView) findViewById(R.id.textViewNoUserFound);
        recyclerViewSearchResult= (RecyclerView) findViewById(R.id.recyclerViewSearchedFriends);
        progressBarSearch= (ProgressBar) findViewById(R.id.progressBarSearch);
        mDatabase=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();
        DatabaseReference loggedReference=mDatabase.getReference().child("users");
        Query loggedQuery=loggedReference.orderByChild("users");
        loggedQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot:dataSnapshot.getChildren()){
                    if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userSnapshot.getKey())) {
                        loggedUser = new User();
                        loggedUser.setImage((String) userSnapshot.child("image").getValue());
                        loggedUser.setSex((String) userSnapshot.child("sex").getValue());
                        loggedUser.setFirstName((String) userSnapshot.child("firstName").getValue());
                        loggedUser.setLastName((String) userSnapshot.child("lastName").getValue());
                        loggedUser.setUserId(userSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerViewSearchResult!=null)
                    recyclerViewSearchResult.setAdapter(null);
                progressBarSearch.setVisibility(View.VISIBLE);
                userArrayList=new ArrayList<User>();
                search=editTextSearchFriend.getText().toString().trim();
                if(search!=null && !search.isEmpty()){
                    DatabaseReference databaseReference=mDatabase.getReference("users");
                    Query query=databaseReference.orderByChild("firstName").equalTo(search.toUpperCase());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            progressBarSearch.setVisibility(View.INVISIBLE);
                            if(dataSnapshot.getValue()==null)
                                textViewNoUserFound.setVisibility(View.VISIBLE);
                            else{
                                textViewNoUserFound.setVisibility(View.INVISIBLE);
                                userArrayList=new ArrayList<User>();
                                for(DataSnapshot userSnapshot:dataSnapshot.getChildren()){
                                    if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userSnapshot.getKey())){
                                        User user=new User();
                                        user.setImage((String) userSnapshot.child("image").getValue());
                                        user.setSex((String) userSnapshot.child("sex").getValue());
                                        user.setFirstName((String) userSnapshot.child("firstName").getValue());
                                        user.setLastName((String) userSnapshot.child("lastName").getValue());
                                        user.setUserId(userSnapshot.getKey());
                                        userArrayList.add(user);
                                    }

                                }
                                searchAdapter=new GridRecyclerAdapter(WelcomeActivity.this,userArrayList,WelcomeActivity.this);
                                recyclerViewSearchResult.setAdapter(searchAdapter);
                                layoutManager=new LinearLayoutManager(WelcomeActivity.this);
                                recyclerViewSearchResult.setLayoutManager(layoutManager);
                                searchAdapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
        });

    }

    @Override
    public void showDetail(User user) {
        Intent intent=new Intent(WelcomeActivity.this,SearchedUserActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pendingRequestMenu:
                Intent pendingIntent=new Intent(WelcomeActivity.this,PendingRequestActivity.class);
                pendingIntent.putExtra("loggedUser",loggedUser);
                startActivity(pendingIntent);
                return true;
            case R.id.friendsMenu:
                startActivity(new Intent(WelcomeActivity.this,FriendsActivity.class));
                return true;
            case R.id.createTripsMenu:
                Intent intent=new Intent(WelcomeActivity.this,CreateTripActivity.class);
                intent.putExtra("loggedUser",loggedUser);
                startActivity(intent);
                return true;
            case R.id.viewTripsMenu:
                Intent viewTripIntent=new Intent(WelcomeActivity.this,ViewTripsActivity.class);
                viewTripIntent.putExtra("loggedUser",loggedUser);
                startActivity(viewTripIntent);
                return true;
            case R.id.logoutMenu:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
