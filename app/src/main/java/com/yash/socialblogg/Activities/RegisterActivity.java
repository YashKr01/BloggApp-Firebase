package com.yash.socialblogg.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yash.socialblogg.R;

import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class RegisterActivity extends AppCompatActivity {

    EditText regEmail, regPassword, regName, regConfPass, regLoc;
    Button regLogin, regReg;
    ImageView regImg;
    ProgressBar regProgg;

    static int REQUEST_CODE = 1;
    static int PReqcode = 1;
    Uri pickedImgUri;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        regEmail = findViewById(R.id.reg_email);
        regPassword = findViewById(R.id.reg_pass);
        regLogin = findViewById(R.id.reg_login);
        regReg = findViewById(R.id.reg_reg);
        regImg = findViewById(R.id.reg_img);
        regName = findViewById(R.id.reg_name);
        regLoc = findViewById(R.id.reg_location);
        regConfPass = findViewById(R.id.reg_confirm_pass);
        regProgg = findViewById(R.id.reg_progress);

        regProgg.setVisibility(View.INVISIBLE);

        regLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                //finish();
                CustomIntent.customType(RegisterActivity.this, "bottom-to-up");

            }
        });

        regReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = regEmail.getText().toString();
                final String password = regPassword.getText().toString();
                final String password2 = regConfPass.getText().toString();
                final String name = regName.getText().toString();
                final String loc = regLoc.getText().toString();

                if (email.isEmpty() || name.isEmpty() || loc.isEmpty() || password.isEmpty() || !password.equals(password2)) {
                    showMessage("Verify all fields");
                    regProgg.setVisibility(View.INVISIBLE);
                } else {
                    if (pickedImgUri == null) {
                        showMessage("Choose profile image to continue");
                    } else {
                        regProgg.setVisibility(View.VISIBLE);
                        createUserAccount(email, name, password);
                    }
                }
            }
        });


        regImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndReq();
                } else
                    openGallery();
            }
        });
    }

    private void createUserAccount(String email, final String name, final String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            showMessage("registered successfully");
                            updateUserIfo(name, pickedImgUri, mAuth.getCurrentUser());
                        } else {
                            showMessage("ERROR" + task.getException().getMessage());
                            regProgg.setVisibility(View.INVISIBLE);
                        }

                    }
                });

    }

    private void updateUserIfo(final String name, final Uri pickedImgUri, final FirebaseUser currentUser) {
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("user_photos");
        final StorageReference imgPath = storage.child(pickedImgUri.getLastPathSegment());
        imgPath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imgPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(pickedImgUri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //showMessage("Registered");
                                            updateUI();
                                        }
                                    }
                                });
                    }
                });

            }
        });

    }

    private void updateUI() {
        Intent intent = new Intent(getApplicationContext(), Home.class);
        startActivity(intent);
        CustomIntent.customType(RegisterActivity.this, "fadein-to-fadeout");
        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {

        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, REQUEST_CODE);

    }

    private void checkAndReq() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegisterActivity.this, "please allow to continue", Toast.LENGTH_SHORT).show();

            } else {
                // showMessage("tap again to choose image");
                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqcode);
                //showMessage("tap again to choose image");

            }
        } else {
            openGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            pickedImgUri = data.getData();
            regImg.setImageURI(pickedImgUri);
        }
    }
}