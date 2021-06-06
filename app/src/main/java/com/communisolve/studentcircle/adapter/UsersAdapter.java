package com.communisolve.studentcircle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.communisolve.studentcircle.Model.UserModel;
import com.communisolve.studentcircle.databinding.LayoutUserItemBinding;
import com.communisolve.studentcircle.ui.search.UserItemClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private LayoutUserItemBinding binding;

    private ArrayList<UserModel> userModels;
    private Context context;
    UserItemClickListener userItemClickListener;

    public UsersAdapter(ArrayList<UserModel> userModels, Context context, UserItemClickListener userItemClickListener) {
        this.userModels = userModels;
        this.context = context;
        this.userItemClickListener = userItemClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        binding = LayoutUserItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UsersAdapter.ViewHolder holder, int position) {
        UserModel userModel = userModels.get(position);
        Glide.with(context).load(userModel.getImage()).into(binding.userProfileImage);
        binding.username.setText(userModel.getName());
        binding.email.setText(userModel.getEmail());

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userItemClickListener.onUserItemClick(userModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

    public void filterList(ArrayList<UserModel> filteredList) {
        userModels = filteredList;
        notifyDataSetChanged();
    }
}
