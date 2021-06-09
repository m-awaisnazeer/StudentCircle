package com.communisolve.studentcircle.ui.viewPost;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.communisolve.studentcircle.Model.PostModel;
import com.communisolve.studentcircle.Model.UserModel;
import com.communisolve.studentcircle.databinding.ActivityViewPostBinding;
import com.communisolve.studentcircle.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import static com.communisolve.studentcircle.utils.Constants.POSTS_REF;

public class ViewPostActivity extends AppCompatActivity {
    private ActivityViewPostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if (getIntent().hasExtra("postId")) {
            getPostById(getIntent().getStringExtra("postId"));
            getPostLikesById(getIntent().getStringExtra("postId"));
            getPostCommentsById(getIntent().getStringExtra("postId"));

        }
    }

    private void getPostCommentsById(String postId) {

    }

    private void getPostLikesById(String postId) {

    }

    private void getPostById(String postId) {
        FirebaseDatabase.getInstance().getReference()
                .child(POSTS_REF)
                .child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            PostModel postModel = snapshot.getValue(PostModel.class);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }


    private void showUserDetails(String currentPostBYUID) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();

        userRef.child(Constants.USER_REF).child(currentPostBYUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            binding.fullName.setText(userModel.getName());
                            Glide.with(getApplicationContext()).load(userModel.getImage()).into(binding.postUserImg);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

}