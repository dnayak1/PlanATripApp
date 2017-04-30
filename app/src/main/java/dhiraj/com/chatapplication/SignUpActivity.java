package dhiraj.com.chatapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SignUpActivity extends AppCompatActivity {
    EditText editTextSignUpFirstName;
    EditText editTextSignUpLastName;
    EditText editTextSignUpEmail;
    EditText editTextSignUpPassword;
    Spinner spinnerSignUpSex;
    ImageView imageViewSignUpProfilePic;
    Button buttonSignUpSignUp, buttonSignUpCancel;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    String firstName, lastName, email, password, sex, profileImage;
    InputStream inputStream = null;
    ProgressBar progressBarSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        editTextSignUpFirstName = (EditText) findViewById(R.id.editTextSignUpFirstNName);
        editTextSignUpLastName = (EditText) findViewById(R.id.editTextSignUpLastName);
        editTextSignUpEmail = (EditText) findViewById(R.id.editTextSignUpEmail);
        editTextSignUpPassword = (EditText) findViewById(R.id.editTextSignUpPassword);
        spinnerSignUpSex = (Spinner) findViewById(R.id.spinnerSignUpSex);
        imageViewSignUpProfilePic = (ImageView) findViewById(R.id.imageViewSignUpProfilePhoto);
        buttonSignUpSignUp = (Button) findViewById(R.id.buttonSignUpSignUp);
        buttonSignUpCancel = (Button) findViewById(R.id.buttonSignUpCancel);
        progressBarSignUp= (ProgressBar) findViewById(R.id.progressBarSignUp);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sex, android.R.layout.simple_spinner_dropdown_item);
        spinnerSignUpSex.setAdapter(adapter);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        buttonSignUpSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarSignUp.setVisibility(View.VISIBLE);
                buttonSignUpCancel.setEnabled(false);
                buttonSignUpSignUp.setEnabled(false);
                firstName = editTextSignUpFirstName.getText().toString().trim();
                lastName = editTextSignUpLastName.getText().toString().trim();
                email = editTextSignUpEmail.getText().toString().trim();
                password = editTextSignUpPassword.getText().toString().trim();
                if (spinnerSignUpSex.getSelectedItem().equals("Male"))
                    sex = "Male";
                else if (spinnerSignUpSex.getSelectedItem().equals("Female"))
                    sex = "Female";
                if (inputStream != null) {
                    if (firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty() && email != null && !email.isEmpty()
                            && password != null && !password.isEmpty() && sex != null && !sex.isEmpty()) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String path = "images/" + mAuth.getCurrentUser().getUid()+".png";
                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(path);
                                    storageReference.putStream(inputStream).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            final DatabaseReference databaseReference = mDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());
                                            final User user = new User();
                                            user.setFirstName(firstName.toUpperCase());
                                            user.setLastName(lastName.toUpperCase());
                                            user.setSex(sex);
                                            profileImage = taskSnapshot.getDownloadUrl().toString();
                                            user.setImage(profileImage);
                                            databaseReference.setValue(user);
                                            progressBarSignUp.setVisibility(View.INVISIBLE);
                                            buttonSignUpSignUp.setEnabled(true);
                                            buttonSignUpCancel.setEnabled(true);
                                            Toast.makeText(SignUpActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignUpActivity.this,WelcomeActivity.class));
                                        }
                                    });
                                    storageReference.putStream(inputStream).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressBarSignUp.setVisibility(View.INVISIBLE);
                                            buttonSignUpSignUp.setEnabled(true);
                                            buttonSignUpCancel.setEnabled(true);
                                            Toast.makeText(SignUpActivity.this, "Image uploading failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else{
                                    progressBarSignUp.setVisibility(View.INVISIBLE);
                                    buttonSignUpSignUp.setEnabled(true);
                                    buttonSignUpCancel.setEnabled(true);
                                    Toast.makeText(SignUpActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    } else {
                        progressBarSignUp.setVisibility(View.INVISIBLE);
                        buttonSignUpSignUp.setEnabled(true);
                        buttonSignUpCancel.setEnabled(true);
                        Toast.makeText(SignUpActivity.this, "Invalid data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressBarSignUp.setVisibility(View.INVISIBLE);
                    buttonSignUpSignUp.setEnabled(true);
                    buttonSignUpCancel.setEnabled(true);
                    Toast.makeText(SignUpActivity.this, "Profile image is not set", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageViewSignUpProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });

        buttonSignUpCancel.setOnClickListener(new View.OnClickListener() {
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
            imageViewSignUpProfilePic.setImageBitmap(bitmap);
        }
    }
}
