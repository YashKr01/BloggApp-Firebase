package com.yash.socialblogg.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yash.socialblogg.Adapters.CommentAdapter;
import com.yash.socialblogg.Model.Comment;
import com.yash.socialblogg.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    ImageView imgPost;
    ImageView imgCurrentUser, imgUserPost;
    TextView txtPostDesc, txtPostDate, txtPostTite;
    EditText editComment;
    FloatingActionButton btnAddComment;

    String postKey;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    RecyclerView commentRV;
    CommentAdapter commentAdapter;
    List<Comment> commentList;
    static String COMMENT_KEY = "COmment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();


        imgPost = findViewById(R.id.post_detail_img);
        imgCurrentUser = findViewById(R.id.post_detail_current_user_img);
        imgUserPost = findViewById(R.id.post_detail_user_img);
        txtPostTite = findViewById(R.id.post_detail_title);
        txtPostDesc = findViewById(R.id.post_detail_desc);
        txtPostDate = findViewById(R.id.post_detail_date_name);
        editComment = findViewById(R.id.post_detail_comment);
        btnAddComment = findViewById(R.id.post_detail_add_comment_btn);
        commentRV = findViewById(R.id.comment_rec);

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddComment.setVisibility(View.INVISIBLE);
                DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(postKey).push();
                String commentContent = editComment.getText().toString();
                String uid = firebaseUser.getUid();
                String uname = firebaseUser.getDisplayName();
                String uimg = firebaseUser.getPhotoUrl().toString();

                Comment comment = new Comment(commentContent, uid, uimg, uname);

                commentRef.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("comment posted");
                        btnAddComment.setVisibility(View.VISIBLE);
                        editComment.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("ERROR!!" + e.getMessage());
                    }
                });

            }
        });


        String postImage = getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).placeholder(R.drawable.placeholder_image)
                .into(imgPost);

        String postTitle = getIntent().getExtras().getString("title");
        txtPostTite.setText(postTitle);

        String userPostImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userPostImage).into(imgUserPost);

        String postDescription = getIntent().getExtras().getString("description");
        txtPostDesc.setText(postDescription);

        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imgCurrentUser);

        postKey = getIntent().getExtras().getString("postKey");

        String date = timestampToString(getIntent().getExtras().getLong("postDate"));
        txtPostDate.setText(date);

        initComment();

    }

    private void initComment() {
        commentRV.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef = firebaseDatabase.getReference("COmment").child(postKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Comment comment = snap.getValue(Comment.class);
                    commentList.add(comment);
                }


                commentAdapter = new CommentAdapter(getApplicationContext(), commentList);
                commentRV.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        return date;
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }


}