package com.example.googleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText edtUserName, edtPass1, edtPass2;
    Button btnAccept;
    String userName, password;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        mappingWidget();

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPass();

                if ((userName != null) || (password != null)) {
                    firebaseAuth.createUserWithEmailAndPassword(userName, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    createDialog();
                                }
                                else {
                                    Toast.makeText(RegisterActivity.this,
                                            "" + task.getException().getMessage(),
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

    private void mappingWidget() {
        edtUserName = findViewById(R.id.edtUser);
        edtPass1 = findViewById(R.id.edtPass);
        edtPass2 = findViewById(R.id.edtPass2);
        btnAccept = findViewById(R.id.btnAccept);
    }

    private void checkPass() {
        boolean valid = false;

        if (edtUserName.getText().length() <= 0) {
            edtUserName.setError("Username is empty.");
        }
        else {
            if (edtPass1.getText().length() <= 0) {
                edtPass1.setError("Password is empty");
            }
            else {
                if (edtPass2.getText().length() <= 0) {
                    edtPass2.setError("Confirm Password is empty");
                }
                else {
                    if (!isValid(edtUserName.getText().toString())) {
                        edtUserName.setError("User is a email address.");
                        valid = false;
                    }
                    else {
                        for (char ch2 : edtPass1.getText().toString().toCharArray()) {
                            if (!Character.isLetter(ch2) && !Character.isDigit(ch2)) {
                                edtPass1.setError("Password can only contain letters and numbers");
                                valid = false;
                                break;
                            }
                            else {
                                valid = true;
                            }
                        }
                    }

                    if (valid) {
                        if (!edtPass2.getText().toString().equals(edtPass1.getText().toString())) {
                            edtPass2.setError("Confirm password is not match.");
                        }
                        else {
                            userName = edtUserName.getText().toString();
                            password = edtPass2.getText().toString();
                        }
                    }
                }
            }
        }
    }

    /* check is valid email addess*/
    static boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

        builder.setCancelable(false);

        builder.setTitle("Register is Successful.")
                .setIcon(R.drawable.ic_check_black_24dp);

        builder.setPositiveButton("Back to Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onBackPressed();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
