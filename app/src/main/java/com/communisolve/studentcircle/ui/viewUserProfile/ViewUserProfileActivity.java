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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.communisolve.studentcircle.utils.Constants.POSTS_REF;
import static com.communisolve.studentcircle.utils.Constants.USER_REF;

public class ViewUserProfileActivity extends AppCompatActivity {
    private ActivityViewUserProfileBinding binding;

    private DatabaseReference UserRef;
    private DatabaseReference PostsRef;


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

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(FirebaseAuth.getInstance().getUid())){
            binding.btnFollow.setVisibility(View.GONE);
        }
        if (getIntent().hasExtra("userUID")) {
            getUserDetails(getIntent().getStringExtra("userUID"));
            showUserPosts(getIntent().getStringExtra("userUID"));
        }

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
                                binding.postsShimmar.startShimmer();
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