package com.example.googleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    TextView tvRegister;
    EditText edtUser, edtPass;
    String username, password;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        createWidget();

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
                checkPass();

                if ((username != null) || (password != null)) {
                    mAuth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // oke
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
        });
    }

    private void createWidget() {
        tvRegister = findViewById(R.id.tvRegister);
        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);
    }

    /* check is valid email addess*/
    static boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

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
}
