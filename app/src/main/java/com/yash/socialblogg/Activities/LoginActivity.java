package com.yash.socialblogg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yash.socialblogg.R;

import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private Button loginLogin, loginReg;
    private CircleImageView loginImg;
    private ProgressBar loginProgg;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //CustomIntent.customType(LoginActivity.this, "rotateout-to-rotatein");



        mAuth = FirebaseAuth.getInstance();

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginLogin = findViewById(R.id.login_login);
        loginReg = findViewById(R.id.login_reg);
        loginImg = findViewById(R.id.image_login);
        loginProgg = findViewById(R.id.login_progg);

        loginProgg.setVisibility(View.INVISIBLE);

        loginReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                CustomIntent.customType(LoginActivity.this, "fadein-to-fadeout");
            }
        });

        loginImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });


        loginLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String mail = loginEmail.getText().toString();
                final String password = loginPassword.getText().toString();

                if (mail.isEmpty() || password.isEmpty()) {
                    showMessage("Verify all fields");
                } else {
                    loginProgg.setVisibility(View.VISIBLE);
                    signIn(mail, password);
                }

            }
        });

    }

    private void signIn(String mail, String password) {
        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    loginProgg.setVisibility(View.INVISIBLE);
                    showMessage("Sign In successful");
                    updateUI();
                } else {
                    showMessage(task.getException().getMessage());
                    loginProgg.setVisibility(View.INVISIBLE);
                }

            }
        });
    }

    private void updateUI() {
        Intent homeActivity = new Intent(LoginActivity.this, Home.class);
        startActivity(homeActivity);
        CustomIntent.customType(LoginActivity.this, "fadein-to-fadeout");
        finish();
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            updateUI();
        }

    }


}