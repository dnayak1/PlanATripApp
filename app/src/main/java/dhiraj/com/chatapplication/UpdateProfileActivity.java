package dhiraj.com.chatapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

public class UpdateProfileActivity extends AppCompatActivity {
    User loggedUser;
    ImageView imageViewUpdateProfilePic;
    EditText editTextUpdateFirstName;
    EditText editTextUpdateLastName;
    Spinner spinnerUpdateSex;
    Button buttonUpdateCancel;
    Button buttonUpdateProfile;
    FirebaseStorage mStorage;
    FirebaseDatabase mDatabase;
    InputStream inputStream=null;
    String firstName,lastName,sex,profileImage;
    boolean updated=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setTitle("UpdateProfile");
        loggedUser= (User) getIntent().getExtras().getSerializable("loggedUser");
        imageViewUpdateProfilePic= (ImageView) findViewById(R.id.imageViewUpdateProfilePic);
        editTextUpdateFirstName= (EditText) findViewById(R.id.editTextUpdateFirstName);
        editTextUpdateLastName= (EditText) findViewById(R.id.editTextUpdateLastName);
        spinnerUpdateSex= (Spinner) findViewById(R.id.spinnerUpdateProfileSex);
        buttonUpdateCancel= (Button) findViewById(R.id.buttonUpdateProfileCancel);
        buttonUpdateProfile= (Button) findViewById(R.id.buttonUpdateProfileUpdate);
        mDatabase=FirebaseDatabase.getInstance();
        mStorage=FirebaseStorage.getInstance();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sex, android.R.layout.simple_spinner_dropdown_item);
        spinnerUpdateSex.setAdapter(adapter);
        Picasso.with(this).load(loggedUser.getImage()).into(imageViewUpdateProfilePic);
        editTextUpdateFirstName.setText(loggedUser.getFirstName());
        editTextUpdateLastName.setText(loggedUser.getLastName());
        if(loggedUser.getSex().equals("Male"))
            spinnerUpdateSex.setSelection(1);
        else
            spinnerUpdateSex.setSelection(2);
        buttonUpdateCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageViewUpdateProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName=editTextUpdateFirstName.getText().toString().toUpperCase();
                lastName=editTextUpdateLastName.getText().toString().toUpperCase();
                sex=spinnerUpdateSex.getSelectedItem().toString();
                final DatabaseReference databaseReference=mDatabase.getReference("users").child(loggedUser.getUserId());
                if(inputStream!=null){
                    String path = "images/" +loggedUser.getUserId()+".png";
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(path);
                    storageReference.putStream(inputStream).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            DatabaseReference imageReference=databaseReference.child("image");
                            imageReference.setValue(new String(taskSnapshot.getDownloadUrl().toString()));
                            updated=true;
                        }
                    });
                }
                if(!firstName.equals(loggedUser.getFirstName()) || !lastName.equals(loggedUser.getLastName()) ||
                        !sex.equals(loggedUser.getSex())){
                    DatabaseReference firstNameReference=databaseReference.child("firstName");
                    firstNameReference.setValue(new String(editTextUpdateFirstName.getText().toString()));
                    DatabaseReference lastNameReference=databaseReference.child("lastName");
                    lastNameReference.setValue(new String(editTextUpdateLastName.getText().toString()));
                    DatabaseReference sexReference=databaseReference.child("sex");
                    sexReference.setValue(new String(spinnerUpdateSex.getSelectedItem().toString()));
                    updated=true;
                }
                if(updated){
                    Toast.makeText(UpdateProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(UpdateProfileActivity.this, "Nothing  to update", Toast.LENGTH_SHORT).show();
                }
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
            imageViewUpdateProfilePic.setImageBitmap(bitmap);
        }
    }
}
