package ch.hevs.a6452.grp2.autostop.autostop.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ch.hevs.a6452.grp2.autostop.autostop.entities.PersonEntity;
import ch.hevs.a6452.grp2.autostop.autostop.utils.PotostopSession;

/**
 * This viewmodel is used in the mainactivity
 */
public class MainActivityViewModel extends AndroidViewModel {

    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;

    private MutableLiveData<String> personFullname = new MutableLiveData<>();
    private MutableLiveData<String> personEmail = new MutableLiveData<>();

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        mDatabase = FirebaseDatabase.getInstance();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mUser != null){
            DatabaseReference refPerson = mDatabase.getReference(PotostopSession.NODE_PERSON).child(mUser.getUid());
            // event listener on the currently signed in person
            refPerson.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get the value
                    PersonEntity person = dataSnapshot.getValue(PersonEntity.class);
                    if(person != null){
                        person.setUid(mUser.getUid());
                        personFullname.setValue(person.getFullname());
                        personEmail.setValue(person.getEmail());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("Error while getting current user");
                    System.out.println(databaseError.getMessage());
                }
            });
        }

    }

    /**
     * Return an observer on the currently signed in person fullname
     * @return
     */
    public LiveData<String> getFullname(){ return personFullname; }

    /**
     * Return an observer on the currently signed in person email
     * @return
     */
    public LiveData<String> getEmail(){ return personEmail; }
}
