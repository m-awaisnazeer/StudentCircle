package com.communisolve.studentcircle.utils;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.communisolve.studentcircle.Model.TokenModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import static com.communisolve.studentcircle.utils.Constants.TOKENS_REF;

public class UserUtils {
    public static void updateUserToken(String uid, TokenModel tokenModel) {

        FirebaseDatabase.getInstance().getReference()
                .child(TOKENS_REF).child(uid).setValue(tokenModel)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()){

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("DEBUG", "onFailure: TOKEN -> "+e.getMessage());
            }
        });
    }

    public static void removeTOken(String uid) {
        FirebaseDatabase.getInstance().getReference()
                .child(TOKENS_REF).child(uid).setValue(null);
    }
}
