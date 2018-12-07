package ch.hevs.a6452.grp2.autostop.autostop.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ch.hevs.a6452.grp2.autostop.autostop.Entites.PositionEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.TripEntity;
import ch.hevs.a6452.grp2.autostop.autostop.ViewModels.TripViewModel;

public class TrackingService extends Service {

    private static final String TAG = "TrackingService";

    private FusedLocationProviderClient client;
    private LocationCallback locationCallback;
    private FirebaseDatabase mDatabase;
    private PositionEntity currentPosition;
    private TripEntity trip;
    private TripViewModel mViewModel;

    private String uidTrip;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        uidTrip = intent.getStringExtra("uidTrip");
        mDatabase = FirebaseDatabase.getInstance();
        Log.d(TAG, "Uid trip start " +uidTrip);
        retrievTrip(uidTrip);
        return Service.START_REDELIVER_INTENT;
    }


    public TrackingService() {


    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }



    //Our observer to retriev the trip object
    private void retrievTrip(String uid) {
        Log.d(TAG, "Retrive data");
        DatabaseReference refTrip = mDatabase.getReference(PotostopSession.NODE_TRIP).child(uid);
        refTrip.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "DataSnapShot"+dataSnapshot.getValue());
                trip = dataSnapshot.getValue(TripEntity.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error while getting current trip");
                System.out.println(databaseError.getMessage());
            }
        });
    }


    //Use to start tracking
    private void requestLocationUpdates() {
        Log.i(TAG, "RequestLocationUpdates");
        LocationRequest request = new LocationRequest();

        //Interval between each positions
        request.setInterval(5000);

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        client = LocationServices.getFusedLocationProviderClient(this);

        locationCallback =  new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {

                //Fill Position object
                currentPosition.setLatitude(locationResult.getLastLocation().getLatitude());
                currentPosition.setLongitude(locationResult.getLastLocation().getLongitude());
                currentPosition.setTimestamp(locationResult.getLastLocation().getTime());

                //Add the position to db
                if (currentPosition !=null && trip!=null ) {
                    Log.i(TAG, "New location : "+ locationResult);
                    trip.addPosition(currentPosition);
                    mViewModel.updateTrip(trip);
                }
            }
        };

        //If the permission is granted we start the tracking
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            client.requestLocationUpdates(request,locationCallback, null);
        }
    }

}
