package dhiraj.com.chatapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CreateTripActivity extends AppCompatActivity {
    EditText editTextTripTitle;
    EditText editTextTripLocation;
    ImageView imageViewTripCoverImage;
    Button buttonCreateTrip, buttonCreateTripCancel;
    String title,location,id,newId;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;
    InputStream inputStream;
    ArrayList<User> friendArrayList;
    User loggedUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setTitle("Create Trip");
        editTextTripTitle= (EditText) findViewById(R.id.editTextTripTitle);
        editTextTripLocation= (EditText) findViewById(R.id.editTextTripLocation);
        imageViewTripCoverImage= (ImageView) findViewById(R.id.imageViewTripCoverImage);
        buttonCreateTripCancel= (Button) findViewById(R.id.buttonCreateTripCancel);
        buttonCreateTrip= (Button) findViewById(R.id.buttonCreateTrip);
        mAuth=FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance();
        mStorage=FirebaseStorage.getInstance();
        loggedUser= (User) getIntent().getExtras().getSerializable("loggedUser");
        imageViewTripCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });
        buttonCreateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editTextTripTitle.getText().toString().trim().isEmpty() && !editTextTripLocation.getText().toString().trim().isEmpty()){
                    DatabaseReference viewTripDatabaseReference=mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid())
                            .child("friends").child("confirmedRequest");
                    Query query=viewTripDatabaseReference.orderByChild("confirmedRequest");
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            friendArrayList=new ArrayList<User>();
                            for(DataSnapshot friendSnapshot:dataSnapshot.getChildren()){
                                User user=new User();
                                user.setImage(friendSnapshot.child("image").getValue().toString());
                                user.setLastName(friendSnapshot.child("lastName").getValue().toString());
                                user.setSex(friendSnapshot.child("sex").getValue().toString());
                                user.setUserId(friendSnapshot.child("userId").getValue().toString());
                                user.setFirstName(friendSnapshot.child("firstName").getValue().toString());
                                friendArrayList.add(user);
                            }
                            if(inputStream!=null){
                                title=editTextTripTitle.getText().toString();
                                location=editTextTripLocation.getText().toString();
                                id=mAuth.getCurrentUser().getUid().toString();
                                String path = "trip/" + mAuth.getCurrentUser().getUid()+".png";
                                StorageReference storageReference = FirebaseStorage.getInstance().getReference(path);
                                storageReference.putStream(inputStream).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        final DatabaseReference databaseReference = mDatabase.getReference().child("trips");
                                        DatabaseReference newDatabaseReference=databaseReference.push();
                                        newId=newDatabaseReference.getKey();
                                        Trip trip=new Trip();
                                        trip.setCreatedBy(id);
                                        trip.setTitle(title);
                                        trip.setLocation(location);
                                        trip.setImage(taskSnapshot.getDownloadUrl().toString());
                                        newDatabaseReference.setValue(trip);
                                        DatabaseReference addViewTripReference=newDatabaseReference.child("viewTripUsers");
                                        if(friendArrayList!=null && friendArrayList.size()>0) {
                                            for (User user : friendArrayList) {
                                                DatabaseReference addUserReference = addViewTripReference.child(user.getUserId());
                                                addUserReference.setValue(user);
                                            }
                                        }
                                        DatabaseReference sameUserReference=newDatabaseReference.child("viewTripUsers").child(loggedUser.getUserId());
                                        sameUserReference.setValue(loggedUser);
                                        DatabaseReference joinedUserReference=newDatabaseReference.child("joinedUsers").child(loggedUser.getUserId());
                                        joinedUserReference.setValue(loggedUser);

                                        Toast.makeText(CreateTripActivity.this, "Trip created successfully", Toast.LENGTH_SHORT).show();
                                        buttonCreateTrip.setEnabled(false);
                                    }
                                });
                                storageReference.putStream(inputStream).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CreateTripActivity.this, "Image uploading failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else{
                                Toast.makeText(CreateTripActivity.this, "Photo not added", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    Toast.makeText(CreateTripActivity.this, "Enter All Details", Toast.LENGTH_SHORT).show();
                }
                
            }
        });

        buttonCreateTripCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri = data.getData();
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            inputStream = getContentResolver().openInputStream(imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            imageViewTripCoverImage.setImageBitmap(bitmap);
        }
    }
}
