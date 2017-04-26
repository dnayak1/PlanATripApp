package dhiraj.com.chatapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    private FirebaseAuth mAuth;
    EditText editTextSignInEmail;
    EditText editTextSignInPassword;
    Button buttonSignIn;
    Button buttonSignUp;
    String email, password;
    ProgressBar progressBarSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setIcon(R.drawable.trip);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("   Plan Your Trip");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        editTextSignInEmail= (EditText) findViewById(R.id.editTextLoginInEmail);
        editTextSignInPassword= (EditText) findViewById(R.id.editTextSignInPassword);
        buttonSignIn= (Button) findViewById(R.id.buttonSignIn);
        buttonSignUp= (Button) findViewById(R.id.buttonSignUp);
        progressBarSignIn= (ProgressBar) findViewById(R.id.progressBarLogin);
        mAuth=FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarSignIn.setVisibility(View.VISIBLE);
                buttonSignIn.setEnabled(false);
                buttonSignUp.setEnabled(false);
                email=editTextSignInEmail.getText().toString().trim();
                password=editTextSignInPassword.getText().toString().trim();
                if(email!=null && !email.isEmpty() && password!=null && !password.isEmpty()){
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBarSignIn.setVisibility(View.INVISIBLE);
                            if(!task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Invalid user credentials", Toast.LENGTH_SHORT).show();
                                buttonSignIn.setEnabled(true);
                                buttonSignUp.setEnabled(true);
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this,WelcomeActivity.class));
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignUpActivity.class));
            }
        });

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        buttonSignIn.setEnabled(true);
        buttonSignUp.setEnabled(true);
    }
}
