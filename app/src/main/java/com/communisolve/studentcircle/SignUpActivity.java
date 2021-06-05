package com.communisolve.studentcircle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.communisolve.studentcircle.Model.UserModel;
import com.communisolve.studentcircle.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import static com.communisolve.studentcircle.utils.Constants.USER_REF;
import static com.communisolve.studentcircle.utils.Constants.currentUser;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference();

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.signUpProgressBar.setVisibility(View.GONE);
                if (binding.edtEmail.getText().toString().isEmpty()) {
                    binding.edtEmail.setError("Enter Email");
                    binding.signUpProgressBar.setVisibility(View.GONE);
                    return;
                } else if (binding.edtPassword.getText().toString().isEmpty()) {
                    binding.edtPassword.setError("Enter Password");
                    binding.signUpProgressBar.setVisibility(View.GONE);
                    return;
                } else {
                    mAuth.createUserWithEmailAndPassword(binding.edtEmail.getText().toString(),
                            binding.edtPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        UserRef.child(USER_REF).child(mAuth.getUid())
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {
                                                            UserModel userModel = snapshot.getValue(UserModel.class);
                                                            currentUser = userModel;
                                                            binding.signUpProgressBar.setVisibility(View.GONE);
                                                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                            finish();
                                                        } else {
                                                            binding.signUpProgressBar.setVisibility(View.GONE);
                                                            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                                                            finish();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                                    }
                                                });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(SignUpActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}