package ch.hevs.a6452.grp2.autostop.autostop.Utils;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ch.hevs.a6452.grp2.autostop.autostop.Entites.PersonEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.TripEntity;

public class PotostopSession {
    private static PotostopSession instance = null;

    private PersonEntity currentUser = null;
    private TripEntity currentTrip = null;
    private FirebaseUser mUser = null;
    private FirebaseDatabase mData = null;

    public static final String NODE_PERSON = "persons";
    public static final String NODE_EMERGENCY_PERSON = "emergency_contact";
    public static final String NODE_TRIP = "trips";

    private PotostopSession(){
        init();
    }

    private void init() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mData = FirebaseDatabase.getInstance();
        loadPerson();
        loadTrip();
    }

    public static PotostopSession getInstance(){
        if(instance == null)
            instance = new PotostopSession();

        return instance;
    }

    private void loadPerson(){
        if(mUser == null)
            return;
    }

    private void loadTrip(){
        if(mUser == null)
            return;
    }

    public PersonEntity getCurrentUser(){
        return currentUser;
    }

    public TripEntity getCurrentTrip(){
        return currentTrip;
    }

    @Override
    public String toString() {
        return "CURRENT SESSION ||| Uid : " + currentUser.getUid() + " | ";
    }
}

