package com.example.googleapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth.AuthStateListener mAuthListener;


    private static final int RC_SIGN_IN = 1000;

    TextView tvRegister;
    EditText edtUser, edtPass;
    String username, password;
    Button btnLogin;
    ImageView imgGG, imgFB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        createWidget();

        // create firebase auth.
        mAuth = FirebaseAuth.getInstance();

        // register Google sign in.
        registerGGSignInClient();

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaselogIn();
            }
        });

        imgGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        imgFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "TT", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    /* LOGIN WITH EMAIL & PASSWORD FIREBASE */
    private void createWidget() {
        tvRegister = findViewById(R.id.tvRegister);
        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);
        imgFB = findViewById(R.id.imgFF);
        imgGG = findViewById(R.id.imgGG);
    }

    /* check is valid email addess*/
    static boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    /* check valid email, check empty edittext... */
    private void checkPass() {
        boolean valid = false;

        if (edtUser.getText().length() <= 0) {
            edtUser.setError("Username is empty.");
        }
        else {
            if (edtPass.getText().length() <= 0) {
                edtPass.setError("Password is empty");
            }
            else {
                if (!isValid(edtUser.getText().toString())) {
                    edtUser.setError("User is a email address.");
                    valid = false;
                }
                else {
                    for (char ch2 : edtPass.getText().toString().toCharArray()) {
                        if (!Character.isLetter(ch2) && !Character.isDigit(ch2)) {
                            edtPass.setError("Password can only contain letters and numbers");
                            valid = false;
                            break;
                        }
                        else {
                            valid = true;
                        }
                    }
                }

                if (valid) {
                    username = edtUser.getText().toString();
                    password = edtPass.getText().toString();
                }
            }
        }
    }

    private void FirebaselogIn() {
        /* check empty, check valid email */
        checkPass();

        /* login
         * if successful kill login activity
         *  don't save username, and pass infomation */
        if ((username != null) || (password != null)) {
            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("username", username);

                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "" +
                                                task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else {
            return;
        }
    }

    //----------------------------------------------------------------------------------------------
    /* LOGIN WITH GOOGLE ACCOUNT */
    private void registerGGSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.android_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            finish();
                            Toast.makeText(LoginActivity.this, "User Login successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Could not Login", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUIWithGGAccount(account);
        } catch (ApiException e) {
            Log.w("SocicalApp", "signInResult:failed code=" + e.getStatusCode() + e.getMessage());
            //updateUIWithGGAccount(null);
        }
    }

    private void updateUIWithGGAccount(GoogleSignInAccount acct) {
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            username = personName;
        }
    }
}
