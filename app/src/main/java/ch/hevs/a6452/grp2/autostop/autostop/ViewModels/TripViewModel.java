package ch.hevs.a6452.grp2.autostop.autostop.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import ch.hevs.a6452.grp2.autostop.autostop.Entites.TripEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Utils.PotostopSession;

public class TripViewModel extends AndroidViewModel {

    private FirebaseDatabase mDatabase;
    private String mTripUid;

    private final MutableLiveData<TripEntity> mObservableTrip = new MutableLiveData<>();

    public TripViewModel(@NonNull Application application, String tripUid) {
        super(application);
        mDatabase = FirebaseDatabase.getInstance();
        mTripUid = tripUid ;
        DatabaseReference refTrip = mDatabase.getReference(PotostopSession.NODE_TRIP).child(mTripUid);
        refTrip.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TripVM", "DataSnapShot"+dataSnapshot.getValue());
                TripEntity trip = dataSnapshot.getValue(TripEntity.class);
                trip.setUid(mTripUid);
                mObservableTrip.setValue(trip);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error while getting current trip");
                System.out.println(databaseError.getMessage());
            }
        });
    }

    public LiveData<TripEntity> getTrip() {
        return mObservableTrip;
    }

    public void updateTrip(TripEntity trip){

        mDatabase.getReference(PotostopSession.NODE_TRIP).child(mTripUid).setValue(trip, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError == null){
                    System.out.println("TRIP SUCCESSFULLY udapted");
                }
                else {
                    System.out.println("Error while updating trip");
                    System.out.println(databaseError.getMessage());
                }
            }
        });
    }


    public static class Factory implements ViewModelProvider.Factory {
        private Application mApplication;
        private String mTripUid;


        public Factory(Application application, String tripUid) {
            mApplication = application;
            mTripUid = tripUid;
        }


        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new TripViewModel(mApplication, mTripUid);
        }
    }
}
