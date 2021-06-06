package com.communisolve.studentcircle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.communisolve.studentcircle.Model.PostModel;
import com.communisolve.studentcircle.Model.UserModel;
import com.communisolve.studentcircle.callbacks.PostItemClickListener;
import com.communisolve.studentcircle.databinding.LayoutPostItemBinding;
import com.communisolve.studentcircle.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {

    LayoutPostItemBinding binding;
    ArrayList<PostModel> postModelArrayList;
    Context context;
    PostItemClickListener postItemClickListener;
    private DatabaseReference userRef;

    public PostsAdapter(ArrayList<PostModel> postModelArrayList, Context context, PostItemClickListener postItemClickListener) {
        this.postModelArrayList = postModelArrayList;
        this.context = context;
        this.postItemClickListener = postItemClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        binding = LayoutPostItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PostsViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PostsViewHolder holder, int position) {
        PostModel currentPost = postModelArrayList.get(position);
        showUserDetails(currentPost);
        Glide.with(context).load(currentPost.getImgURL()).into(binding.postImg);
        binding.txtPost.setText(currentPost.getPostText());
        Timestamp ts=new Timestamp(System.currentTimeMillis());
        Date date=new Date(ts.getTime());
        binding.timeAgo.setText(date.toString());
    }

    private void showUserDetails(PostModel currentPost) {
        userRef = FirebaseDatabase.getInstance().getReference();

        userRef.child(Constants.USER_REF).child(currentPost.getPostByUID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            Glide.with(context).load(userModel.getImage()).into(binding.postUserImg);
                            binding.fullName.setText(userModel.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return postModelArrayList.size();
    }

    public class PostsViewHolder extends RecyclerView.ViewHolder {
        public PostsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
