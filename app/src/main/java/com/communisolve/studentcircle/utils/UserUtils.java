package com.communisolve.studentcircle.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.communisolve.studentcircle.Model.TokenModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.communisolve.studentcircle.utils.Constants.TOKENS_REF;

public class UserUtils {
    public static void updateUserToken(String uid, TokenModel tokenModel) {

        FirebaseDatabase.getInstance().getReference()
                .child(TOKENS_REF).child(uid).setValue(tokenModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("DEBUG", "onFailure: TOKEN -> " + e.getMessage());
            }
        });
    }

    public static void removeTOken(String uid) {
        FirebaseDatabase.getInstance().getReference()
                .child(TOKENS_REF).child(uid).setValue(null);
    }

    public static void followPerson(String currentUserUID, String followUID) {
        DatabaseReference followRef;
        followRef = FirebaseDatabase.getInstance().getReference();

        followRef.child(Constants.FOLLOWER_REF).child(followUID)
                .child(currentUserUID).child("id").setValue(currentUserUID)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference();
                            followingRef.child(Constants.FOLLOWING_REF).child(currentUserUID)
                                    .child(followUID).child("id").setValue(followUID);
                        }
                    }
                });
    }

    public static void unFollowPerson(String currentUserUID, String followerUID) {
        DatabaseReference followRef;
        followRef = FirebaseDatabase.getInstance().getReference();

        followRef.child(Constants.FOLLOWER_REF).child(followerUID)
                .child(currentUserUID).setValue(null)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference();
                            followRef.child(Constants.FOLLOWING_REF).child(currentUserUID)
                                    .child(followerUID).setValue(null);
                        }
                    }
                });
    }


}
