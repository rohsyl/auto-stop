package ch.hevs.a6452.grp2.autostop.autostop.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ch.hevs.a6452.grp2.autostop.autostop.Entites.PersonEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Utils.FirebaseConverter;
import ch.hevs.a6452.grp2.autostop.autostop.Utils.PotostopSession;

public class ProfileViewModel extends AndroidViewModel {

    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;

    private final MutableLiveData<PersonEntity> mObservablePerson = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        mDatabase = FirebaseDatabase.getInstance();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference refPerson = mDatabase.getReference(PotostopSession.NODE_PERSON).child(mUser.getUid());
        refPerson.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PersonEntity person = dataSnapshot.getValue(PersonEntity.class);
                person.setUid(mUser.getUid());
                mObservablePerson.setValue(person);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error while getting current user");
                System.out.println(databaseError.getMessage());
            }
        });
    }
    // TODO: Implement the ViewModel

    public LiveData<PersonEntity> getPerson() {
        return mObservablePerson;
    }

    public void updatePerson(PersonEntity person){

        mDatabase.getReference(PotostopSession.NODE_PERSON).child(mUser.getUid()).setValue(person, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError == null){
                    System.out.println("USER SUCCESSFULLY udapted");
                }
                else {
                    System.out.println("Error while updating person");
                    System.out.println(databaseError.getMessage());
                }
            }
        });
    }
}
