package com.communisolve.studentcircle.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.communisolve.studentcircle.Model.UserModel;
import com.communisolve.studentcircle.adapter.UsersAdapter;
import com.communisolve.studentcircle.databinding.FragmentSearchBinding;
import com.communisolve.studentcircle.ui.viewUserProfile.ViewUserProfileActivity;
import com.communisolve.studentcircle.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.communisolve.studentcircle.utils.Constants.USER_REF;


public class SearchFragment extends Fragment implements UserItemClickListener {

    private SearchViewModel searchViewModel;
    private FragmentSearchBinding binding;

    FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    UsersAdapter adapter;
    private ArrayList<UserModel> userModels;

    UserItemClickListener userItemClickListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userItemClickListener = this;
        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference();
        userModels = new ArrayList();

        binding.searchUserRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.searchUserRecyclerView.setLayoutManager(linearLayoutManager);
        binding.searchUserRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation()));

        UserRef.child(USER_REF).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userModels.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        userModels.add(dataSnapshot.getValue(UserModel.class));
                    }
                    adapter = new UsersAdapter(userModels, getContext(), userItemClickListener);
                    binding.searchUserRecyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.edtSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        return root;
    }


    private void filter(String text) {
        ArrayList<UserModel> filteredList = new ArrayList<>();
        for (UserModel item : userModels) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getEmail().toLowerCase().contains(text.toLowerCase())
            ) {
                filteredList.add(item);
            }
        }
        if (adapter!=null)
            adapter.filterList(filteredList);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onPause() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onUserItemClick(UserModel userModel) {
        Constants.currentSelectedUserUID = userModel.getUid();
        startActivity(new Intent(getContext(), ViewUserProfileActivity.class).putExtra("userUID", userModel.getUid()));
    }
}