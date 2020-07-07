package com.yash.socialblogg.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yash.socialblogg.Fragments.HomeFragment;
import com.yash.socialblogg.Fragments.ProfileFragment;
import com.yash.socialblogg.Fragments.SettingsFragment;
import com.yash.socialblogg.Model.Post;
import com.yash.socialblogg.R;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mNavDrawer;
    Toolbar toolbar;
    FloatingActionButton fab;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Dialog popAddPost;
    static int REQUEST_CODE = 1;

    ImageView popupUserImage, popupPostImage, popupAddBtn, nav_img;
    EditText popupTitle, popupDescription;
    ProgressBar popupProgg;
    public static final int CODE = 1;
    private Uri pickedImgUri = null;

    TextView nav_username, nav_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_layout);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        initPopup();
        setupPopupImage();

        NavigationView navigationView = findViewById(R.id.navigation_view);

        fab = findViewById(R.id.fab);
        nav_username = findViewById(R.id.nav_email);
        nav_email = findViewById(R.id.nav_email);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        });

        toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        //toolbar.setTitle("Home");

        mNavDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mNavDrawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        mNavDrawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    private void setupPopupImage() {

        popupPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndReq();
            }
        });

    }

    private void checkAndReq() {
        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(Home.this, "please allow to continue", Toast.LENGTH_SHORT).show();

            } else {
                // showMessage("tap again to choose image");
                ActivityCompat.requestPermissions(Home.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        CODE);
                //showMessage("tap again to choose image");

            }
        } else {
            openGallery();
        }
    }

    private void openGallery() {

        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, REQUEST_CODE);

    }

    private void initPopup() {
        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.setCanceledOnTouchOutside(false);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        popupUserImage = popAddPost.findViewById(R.id.popup_user_image);
        popupPostImage = popAddPost.findViewById(R.id.popup_img);
        popupTitle = popAddPost.findViewById(R.id.popup_title);
        popupDescription = popAddPost.findViewById(R.id.popup_description);
        popupAddBtn = popAddPost.findViewById(R.id.popup_add);
        popupProgg = popAddPost.findViewById(R.id.popup_progressBar);

        Glide.with(Home.this).load(currentUser.getPhotoUrl()).placeholder(R.drawable.placeholder_image)
                .into(popupUserImage);

        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!popupTitle.getText().toString().isEmpty()
                        && !popupDescription.toString().isEmpty()
                        && pickedImgUri != null) {
                    popupProgg.setVisibility(View.VISIBLE);
                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference().child("blog_images");
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownloadLink = uri.toString();
                                    Post post = new Post(popupTitle.getText().toString(), popupDescription.getText().toString(),
                                            imageDownloadLink, currentUser.getUid(), currentUser.getPhotoUrl().toString());
                                    addPost(post);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showMessage(e.getMessage());
                                    popupProgg.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    });


                } else {
                    showMessage("fill all fields an choose blog image...");
                }
            }
        });
    }

    private void addPost(Post post) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Posts").push();

        String key = myRef.getKey();
        post.setPostKey(key);

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Post Uploaded");
                popupProgg.setVisibility(View.INVISIBLE);
                popAddPost.dismiss();
            }
        });

    }

    private void showMessage(String message) {
        Toast.makeText(Home.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            pickedImgUri = data.getData();
            popupPostImage.setImageURI(pickedImgUri);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:

                toolbar.setTitle("Home");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
                break;

            case R.id.nav_profile:

                toolbar.setTitle("Profile");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment())
                        .commit();
                break;

            case R.id.nav_settings:
                toolbar.setTitle("Settings");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SettingsFragment())
                        .commit();
                break;

            case R.id.Sign_Out:

                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
                break;

        }

        mNavDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_frag, menu);
        return true;
    }




}
