package ch.hevs.a6452.grp2.autostop.autostop.Utils;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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

import ch.hevs.a6452.grp2.autostop.autostop.Entites.PositionEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.TripEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Models.Trip;
import ch.hevs.a6452.grp2.autostop.autostop.R;

public class TrackingService extends Service {

    private static final String TAG = TrackingService.class.getSimpleName();

    private FusedLocationProviderClient client;
    private LocationCallback locationCallback;
    private PositionEntity currentPosition;
    private FirebaseDatabase mDatabase;
    private String mTripUid;
    private TripEntity trip;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        currentPosition = new PositionEntity();
        //buildNotification();
        requestLocationUpdates();
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        mTripUid=(String) intent.getExtras().get("uidTrip");
        Log.i(TAG, "trip uid " + mTripUid);
        getTrip(mTripUid);
        return flags;
    }

    @Override
    public void onDestroy() {
        trip.setStatus(Trip.STATUS_FINISHED);
        updateTrip(trip);
        client.removeLocationUpdates(locationCallback);
    }


    private void buildNotification() {
        Log.i(TAG, "Build notif ");

        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Trip en cours")
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher);
        startForeground(1, builder.build());
    }



    private void getTrip(String tripUid){
        mDatabase = FirebaseDatabase.getInstance();
        mTripUid = tripUid ;
        DatabaseReference refTrip = mDatabase.getReference(PotostopSession.NODE_TRIP).child(mTripUid);
        refTrip.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TripVM", "DataSnapShot"+dataSnapshot.getValue());
                trip = dataSnapshot.getValue(TripEntity.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error while getting current trip");
                System.out.println(databaseError.getMessage());
            }
        });
    }

    private void updateTrip(TripEntity trip){
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

    private void requestLocationUpdates() {
        Log.i(TAG, "track ");

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
                if (currentPosition !=null  ) {
                    Log.i(TAG, "New location : "+ currentPosition);
                    trip.addPosition(currentPosition);
                    updateTrip(trip);
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
