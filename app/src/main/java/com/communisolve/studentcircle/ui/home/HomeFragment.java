package com.communisolve.studentcircle.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.communisolve.studentcircle.Model.PostModel;
import com.communisolve.studentcircle.adapter.PostsAdapter;
import com.communisolve.studentcircle.callbacks.PostItemClickListener;
import com.communisolve.studentcircle.databinding.FragmentHomeBinding;
import com.communisolve.studentcircle.ui.viewPost.ViewPostActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.communisolve.studentcircle.utils.Constants.FOLLOWING_REF;
import static com.communisolve.studentcircle.utils.Constants.POSTS_REF;

public class HomeFragment extends Fragment implements PostItemClickListener {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    FirebaseAuth mAuth;
    private DatabaseReference PostsRef;
    private ArrayList<PostModel> postModels;
    private PostsAdapter adapter;
    public ArrayList<String> currentUserFollowingList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    PostItemClickListener postItemClickListener;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mAuth = FirebaseAuth.getInstance();
        PostsRef = FirebaseDatabase.getInstance().getReference();
        postItemClickListener = this;
        binding.postsShimmar.startShimmer();
        getCurrentUserFollowingList(mAuth.getUid());
        binding.postsRecyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(getContext());
        binding.postsRecyclerView.setLayoutManager(linearLayoutManager);
        if (isAdded() && getContext() != null)
            binding.postsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation()));

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded())
                    showUserPosts(mAuth.getUid());
            }
        }, 1500);

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
                                postModel.setPostUID(childSnapshot.getKey());
                                if (currentUserFollowingList.contains(postModel.getPostByUID())) {
                                    postModels.add(postModel);
                                }
                            }
                            if (postModels.size() > 0) {
                                binding.postsShimmar.stopShimmer();
                                binding.postsShimmar.setVisibility(View.GONE);
                                binding.postsRecyclerView.setVisibility(View.VISIBLE);
                                adapter = new PostsAdapter(postModels, getContext(), postItemClickListener);
                                binding.postsRecyclerView.setAdapter(adapter);
                            } else {
                                binding.postsShimmar.stopShimmer();
                                binding.postsShimmar.setVisibility(View.GONE);
                                binding.postsRecyclerView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            binding.postsShimmar.stopShimmer();
                            binding.postsShimmar.setVisibility(View.GONE);
                            binding.postsRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void getCurrentUserFollowingList(String uid) {
        DatabaseReference followRef;
        followRef = FirebaseDatabase.getInstance().getReference();
        followRef.child(FOLLOWING_REF).child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            currentUserFollowingList = new ArrayList<String>();
                            for (DataSnapshot childSnapShot : snapshot.getChildren()) {
                                currentUserFollowingList.add(childSnapShot.child("id").getValue(String.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onPostItemCLick(PostModel postModel, boolean isCommentedOnPost) {
        if (isCommentedOnPost){
            startActivity(new Intent(getContext(), ViewPostActivity.class).putExtra("postId",postModel.getPostUID()));
        }
    }
}