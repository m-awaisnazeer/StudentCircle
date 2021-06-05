package com.communisolve.studentcircle.ui.profile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.communisolve.studentcircle.databinding.ProfileFragmentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import static com.communisolve.studentcircle.utils.Constants.POSTS_REF;
import static com.communisolve.studentcircle.utils.Constants.currentUser;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    private ProfileFragmentBinding binding;
    FirebaseAuth mAuth;
    private DatabaseReference PostsRef;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ProfileFragmentBinding.inflate(inflater);
        binding.postsShimmar.startShimmer();
        binding.shimmarProfileLayout.startShimmer();

        mAuth = FirebaseAuth.getInstance();
        PostsRef = FirebaseDatabase.getInstance().getReference();

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showUserProfile();
                showUserPosts();
            }
        }, 1000);

        return binding.getRoot();
    }

    private void showUserPosts() {
        binding.postsShimmar.startShimmer();
        binding.postsShimmar.setVisibility(View.GONE);
        binding.postsRecyclerView.setVisibility(View.VISIBLE);

        PostsRef.child(POSTS_REF).child(mAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showUserProfile() {
        binding.shimmarProfileLayout.stopShimmer();
        binding.shimmarProfileLayout.setVisibility(View.GONE);
        binding.viewLayout.setVisibility(View.VISIBLE);

        if (currentUser != null) {
            Glide.with(this).load(currentUser.getImage()).into(binding.imgProfile);
            binding.txtFullName.setText(currentUser.getName());
        }
    }


}