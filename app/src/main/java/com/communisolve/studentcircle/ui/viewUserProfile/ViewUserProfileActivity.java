package com.communisolve.studentcircle.ui.viewUserProfile;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.communisolve.studentcircle.Model.PostModel;
import com.communisolve.studentcircle.Model.UserModel;
import com.communisolve.studentcircle.adapter.PostsAdapter;
import com.communisolve.studentcircle.callbacks.PostItemClickListener;
import com.communisolve.studentcircle.databinding.ActivityViewUserProfileBinding;
import com.communisolve.studentcircle.utils.UserUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.communisolve.studentcircle.utils.Constants.FOLLOWER_REF;
import static com.communisolve.studentcircle.utils.Constants.FOLLOWING_REF;
import static com.communisolve.studentcircle.utils.Constants.POSTS_REF;
import static com.communisolve.studentcircle.utils.Constants.USER_REF;
import static com.communisolve.studentcircle.utils.Constants.currentSelectedUserUID;

public class ViewUserProfileActivity extends AppCompatActivity {
    private ActivityViewUserProfileBinding binding;

    private DatabaseReference UserRef;
    private DatabaseReference FollowRef;
    private DatabaseReference PostsRef;
    private FirebaseAuth mAuth;
    private Boolean isFollowingToThisUser = false;

    private ArrayList<PostModel> postModels;
    private PostsAdapter adapter;
    PostItemClickListener postItemClickListener;

    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.postsRecyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(this);
        binding.postsRecyclerView.setLayoutManager(linearLayoutManager);

        UserRef = FirebaseDatabase.getInstance().getReference();
        PostsRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        if (getIntent().hasExtra("userUID")) {
            getUserDetails(getIntent().getStringExtra("userUID"));
            showUserPosts(getIntent().getStringExtra("userUID"));

        }



        if (currentSelectedUserUID != null && !currentSelectedUserUID.isEmpty()) {
            getFollowersCount(currentSelectedUserUID);
            getFollowingCount(currentSelectedUserUID);
            TrackingFollowUnFlollow(currentSelectedUserUID, mAuth.getUid());

            Toast.makeText(this, ""+currentSelectedUserUID, Toast.LENGTH_SHORT).show();

            if (currentSelectedUserUID.equals(FirebaseAuth.getInstance().getUid())) {
                binding.btnFollow.setVisibility(View.GONE);
            }else {
                binding.btnFollow.setVisibility(View.VISIBLE);
            }
        }

        binding.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.btnFollow.getText().toString().equals("Following")){
                    binding.btnFollow.setText("Following");
                    UserUtils.unFollowPerson(mAuth.getUid(),currentSelectedUserUID);
                }else {
                    binding.btnFollow.setText("Follow");
                    UserUtils.followPerson(mAuth.getUid(),currentSelectedUserUID);
                }
            }
        });

    }

    private void TrackingFollowUnFlollow(String currentSelectedUserUID, String currentUser) {
        FollowRef = FirebaseDatabase.getInstance().getReference();
        FollowRef.child(FOLLOWER_REF).child(currentSelectedUserUID).child(currentUser)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            binding.btnFollow.setText("Following");
                            isFollowingToThisUser = true;
                        } else {
                            binding.btnFollow.setText("Follow");
                            isFollowingToThisUser = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void getFollowingCount(String currentSelectedUserUID) {
        FollowRef = FirebaseDatabase.getInstance().getReference();
        FollowRef.child(FOLLOWING_REF).child(currentSelectedUserUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            binding.txtFollowing.setText(String.valueOf(snapshot.getChildrenCount()));
                        }else {
                            binding.txtFollowing.setText(String.valueOf(0));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void getFollowersCount(String currentSelectedUserUID) {
        FollowRef = FirebaseDatabase.getInstance().getReference();
        FollowRef.child(FOLLOWER_REF).child(currentSelectedUserUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            binding.txtFollowers.setText(String.valueOf(snapshot.getChildrenCount()));
                        }else {
                            binding.txtFollowers.setText(String.valueOf(0));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void getUserDetails(String userUID) {
        UserRef.child(USER_REF).child(userUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            binding.shimmarProfileLayout.stopShimmer();
                            binding.shimmarProfileLayout.setVisibility(View.GONE);
                            binding.viewLayout.setVisibility(View.VISIBLE);
                            if (userModel != null) {
                                Glide.with(getApplicationContext()).load(userModel.getImage()).into(binding.imgProfile);
                                binding.txtFullName.setText(userModel.getName());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showUserPosts(String userUID) {

        PostsRef.child(POSTS_REF)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            postModels = new ArrayList<>();
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                PostModel postModel = childSnapshot.getValue(PostModel.class);
                                if (postModel.getPostByUID().equals(userUID)) {
                                    postModels.add(postModel);
                                }
                            }
                            if (postModels.size() > 0) {
                                binding.postsShimmar.stopShimmer();
                                binding.postsShimmar.setVisibility(View.GONE);
                                binding.postsRecyclerView.setVisibility(View.VISIBLE);
                                adapter = new PostsAdapter(postModels, getApplicationContext(), postItemClickListener);
                                binding.postsRecyclerView.setAdapter(adapter);
                                Toast.makeText(ViewUserProfileActivity.this, "" + postModels.size(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "No Posts Posted By User", Toast.LENGTH_SHORT).show();
                                binding.postsShimmar.stopShimmer();
                                binding.postsShimmar.setVisibility(View.GONE);
                                binding.postsRecyclerView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "No Posts Posted By User", Toast.LENGTH_SHORT).show();
                            binding.postsShimmar.stopShimmer();
                            binding.postsShimmar.setVisibility(View.GONE);
                            binding.postsRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

}