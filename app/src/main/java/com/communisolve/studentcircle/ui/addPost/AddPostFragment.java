package com.communisolve.studentcircle.ui.addPost;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.communisolve.studentcircle.Model.PostModel;
import com.communisolve.studentcircle.R;
import com.communisolve.studentcircle.databinding.FragmentAddPostBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import static android.app.Activity.RESULT_OK;
import static com.communisolve.studentcircle.utils.Constants.POSTS_REF;


public class AddPostFragment extends Fragment {
    public static final int Image_Request_Code = 1234;
    private Uri selectedImageUri;
    private Boolean imageSelected = false;
    FragmentAddPostBinding binding;
    private DatabaseReference postsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddPostBinding.inflate(inflater);
        postsRef = FirebaseDatabase.getInstance().getReference();

        binding.postImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cvProgressBar.setVisibility(View.VISIBLE);

                if (imageSelected) {
                    if (selectedImageUri != null) {
                        UploadPostImage(FirebaseAuth.getInstance().getUid(), selectedImageUri);
                    }else {
                        SubmitPostToFirebase(FirebaseAuth.getInstance().getUid(),"");
                    }
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            imageSelected = true;
            binding.postImg.setImageURI(data.getData());
        }
    }


    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Image_Request_Code);
    }

    private void UploadPostImage(String UID, Uri selectedImageUri) {
        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("Posts_Images").child(UID);
        final StorageReference ImageName = ImageFolder.child("Image" + selectedImageUri.getLastPathSegment());

        ImageName.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot ->
                ImageName.getDownloadUrl().addOnSuccessListener(uri ->
                        SubmitPostToFirebase(UID, uri.toString()))

        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                binding.cvProgressBar.setVisibility(View.GONE);
            }
        });
    }


    private void SubmitPostToFirebase(String UID, String imgurl) {
        PostModel postModel = new PostModel(
                UID,
                imgurl,
                binding.edtPost.getText().toString().trim(),
                System.currentTimeMillis()
        );


        postsRef.child(POSTS_REF).push()
                .setValue(postModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Posted", Toast.LENGTH_SHORT).show();
                            binding.cvProgressBar.setVisibility(View.GONE);
                            clearData();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                binding.cvProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void clearData() {
        binding.postImg.setImageResource(R.drawable.ic_baseline_image_24);
        binding.edtPost.setText("");
        imageSelected = false;
        selectedImageUri = null;
    }
}