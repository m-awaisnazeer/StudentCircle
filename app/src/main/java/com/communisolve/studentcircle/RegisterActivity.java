package com.communisolve.studentcircle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.communisolve.studentcircle.Model.UserModel;
import com.communisolve.studentcircle.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import static com.communisolve.studentcircle.utils.Constants.USER_REF;
import static com.communisolve.studentcircle.utils.Constants.currentUser;

public class RegisterActivity extends AppCompatActivity {

    public static final int Image_Request_Code = 1234;
    private Uri selectedImageUri;
    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference();

        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cvProgressBar.setVisibility(View.VISIBLE);
                if (binding.email.getText().toString().isEmpty()) {
                    binding.email.setError("Enter Email");
                    binding.cvProgressBar.setVisibility(View.GONE);
                    return;
                } else if (binding.name.getText().toString().isEmpty()) {
                    binding.email.setError("Enter Name");
                    binding.cvProgressBar.setVisibility(View.GONE);
                    return;
                } else if (binding.phone.getText().toString().isEmpty()) {
                    binding.email.setError("Enter Phone");
                    binding.cvProgressBar.setVisibility(View.GONE);
                    return;
                } else if (binding.status.getText().toString().isEmpty()) {
                    binding.email.setError("Enter Status");
                    binding.cvProgressBar.setVisibility(View.GONE);
                    return;
                } else if (binding.city.getText().toString().isEmpty()) {
                    binding.email.setError("Enter Address");
                    binding.cvProgressBar.setVisibility(View.GONE);
                    return;
                } else if (selectedImageUri == null) {
                    Toast.makeText(RegisterActivity.this, "Select User Image to Upload", Toast.LENGTH_SHORT).show();
                    binding.cvProgressBar.setVisibility(View.GONE);
                    return;
                } else {
                    UploadUserImage(mAuth.getUid(), selectedImageUri);
                }
            }
        });

        binding.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            binding.userImage.setImageURI(data.getData());
        }
    }


    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Image_Request_Code);
    }

    private void UploadUserImage(String UID, Uri selectedImageUri) {
        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("Users_Images").child(UID);
        final StorageReference ImageName = ImageFolder.child("Image" + selectedImageUri.getLastPathSegment());

        ImageName.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot ->
                ImageName.getDownloadUrl().addOnSuccessListener(uri ->
                        SubmitProfileToFirebase(UID, uri.toString()))
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                binding.cvProgressBar.setVisibility(View.GONE);
            }
        });

    }

    private void SubmitProfileToFirebase(String UID, String imgurl) {
        UserModel userModel = new UserModel(
                binding.name.getText().toString(),
                imgurl,
                binding.email.getText().toString(),
                binding.phone.getText().toString(),
                UID,
                binding.status.getText().toString(),
                binding.city.getText().toString()
        );
        currentUser = userModel;
        UserRef.child(USER_REF).child(mAuth.getUid())
                .setValue(userModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Profile Created", Toast.LENGTH_SHORT).show();
                            binding.cvProgressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                binding.cvProgressBar.setVisibility(View.GONE);
            }
        });
    }

}