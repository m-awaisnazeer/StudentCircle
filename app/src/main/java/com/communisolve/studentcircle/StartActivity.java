package com.communisolve.studentcircle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.communisolve.studentcircle.Model.UserModel;
import com.communisolve.studentcircle.databinding.ActivityStartBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import static com.communisolve.studentcircle.utils.Constants.USER_REF;
import static com.communisolve.studentcircle.utils.Constants.currentUser;

public class StartActivity extends AppCompatActivity {
    private ActivityStartBinding binding;

    FirebaseAuth mAuth;
    private DatabaseReference UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference();

        if (mAuth.getCurrentUser()!=null){
            UserRef.child(USER_REF).child(mAuth.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                UserModel userModel = snapshot.getValue(UserModel.class);
                                currentUser = userModel;
                                binding.cvProgressBar.setVisibility(View.GONE);
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                finish();
                            } else {
                                binding.cvProgressBar.setVisibility(View.GONE);
                                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            binding.cvProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }else {
            binding.cvProgressBar.setVisibility(View.GONE);
            binding.selectionLayout.setVisibility(View.VISIBLE);

        }

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            }
        });
    }
}