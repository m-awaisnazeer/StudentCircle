package com.communisolve.studentcircle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.communisolve.studentcircle.Model.PostModel;
import com.communisolve.studentcircle.Model.UserModel;
import com.communisolve.studentcircle.R;
import com.communisolve.studentcircle.callbacks.PostItemClickListener;
import com.communisolve.studentcircle.databinding.LayoutPostItemBinding;
import com.communisolve.studentcircle.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import static com.communisolve.studentcircle.utils.Constants.LIKES_REF;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {

    LayoutPostItemBinding binding;
    ArrayList<PostModel> postModelArrayList;
    Context context;
    PostItemClickListener postItemClickListener;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    boolean isCurrentPostLiked = false;

    public PostsAdapter(ArrayList<PostModel> postModelArrayList, Context context, PostItemClickListener postItemClickListener) {
        this.postModelArrayList = postModelArrayList;
        this.context = context;
        this.postItemClickListener = postItemClickListener;
        mAuth = FirebaseAuth.getInstance();
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
        showUserDetails(currentPost, holder);
        Glide.with(context).load(currentPost.getImgURL()).into(binding.postImg);
        binding.txtPost.setText(currentPost.getPostText());
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Date date = new Date(ts.getTime());
        binding.timeAgo.setText(date.toString());
        isCurrentPostLiked = false;

        showFavoritePostsByUser(currentPost, holder);

        holder.like_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCurrentPostLiked) {
                    FirebaseDatabase.getInstance().getReference()
                            .child(LIKES_REF).child(currentPost.getPostUID())
                            .child(mAuth.getUid())
                            .setValue(null);
                    Toast.makeText(context, "unlike", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "like", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference()
                            .child(LIKES_REF).child(currentPost.getPostUID())
                            .child(mAuth.getUid())
                            .child("id").setValue(mAuth.getUid());
                }
            }
        });

        holder.comment_on_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postItemClickListener.onPostItemCLick(currentPost,true);
            }
        });
    }

    private void showFavoritePostsByUser(PostModel currentPost, PostsViewHolder holder) {
        FirebaseDatabase.getInstance().getReference()
                .child(LIKES_REF).child(currentPost.getPostUID())
                .child(mAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            isCurrentPostLiked = true;
                            holder.likesPost.setImageResource(R.drawable.ic_baseline_favorite_24);
                            getNumberOfLikesByPost(currentPost, holder);

                        } else {
                            holder.likesPost.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                            isCurrentPostLiked = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

    }

    private void getNumberOfLikesByPost(PostModel currentPost, PostsViewHolder holder) {
        FirebaseDatabase.getInstance().getReference()
                .child(LIKES_REF).child(currentPost.getPostUID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        holder.no_of_likes.setText(String.valueOf(snapshot.getChildrenCount()) + " Likes");
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void showUserDetails(PostModel currentPost, PostsViewHolder holder) {
        userRef = FirebaseDatabase.getInstance().getReference();

        userRef.child(Constants.USER_REF).child(currentPost.getPostByUID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            Glide.with(context).load(userModel.getImage()).into(holder.postUserProfileImage);
                            holder.PostUserTV.setText(userModel.getName());
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
        ImageView postUserProfileImage, likesPost;
        TextView PostUserTV, no_of_likes, no_of_comments;
        LinearLayout like_post, comment_on_post;

        public PostsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            postUserProfileImage = binding.postUserImg;
            likesPost = binding.likeImg;
            PostUserTV = binding.fullName;
            no_of_likes = binding.noOfLikes;
            no_of_comments = binding.noOfComments;

            like_post = binding.likePost;
            comment_on_post = binding.commentOnPost;
        }
    }
}
