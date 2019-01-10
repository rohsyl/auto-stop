package ch.hevs.a6452.grp2.autostop.autostop.utils;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ch.hevs.a6452.grp2.autostop.autostop.entities.PersonEntity;

public class FirebaseManager {

    private static FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

    public static void getUser(String uid, final FirebaseCallBack firebaseCallBack) {
        //Get the entity Person from an uid
        DatabaseReference ref = mFirebaseDatabase.getReference().child(PotostopSession.NODE_PERSON).child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PersonEntity user = dataSnapshot.getValue(PersonEntity.class);
                firebaseCallBack.onCallBack(user);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("LOAD PERSON CANCELED");
            }
        });
    }
}