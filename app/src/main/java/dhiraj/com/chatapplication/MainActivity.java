package dhiraj.com.chatapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    private FirebaseAuth mAuth;
    EditText editTextSignInEmail;
    EditText editTextSignInPassword;
    Button buttonSignIn;
    Button buttonSignUp;
    String email, password;
    ProgressBar progressBarSignIn;
    GoogleApiClient mGoogleApiClient;
    SignInButton signInButton;
    private static final int RC_SIGN_IN=1;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private static final String TAG="Main_Activity";
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
        signInButton= (SignInButton) findViewById(R.id.signIn);
        mAuth=FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);
        mDatabase=FirebaseDatabase.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient=new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        final GoogleSignInAccount account=acct;
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            DatabaseReference reference=mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid());
                            User user=new User();
                            user.setFirstName(account.getGivenName().toUpperCase());
                            user.setLastName(account.getFamilyName().toUpperCase());
                            user.setImage(account.getPhotoUrl().toString());
                            user.setSex("Male");
                            reference.setValue(user);
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this,WelcomeActivity.class));
                        }
                    }
                    
                    
                });
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}
