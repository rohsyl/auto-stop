package ch.hevs.a6452.grp2.autostop.autostop.utils;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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

import ch.hevs.a6452.grp2.autostop.autostop.entities.PositionEntity;
import ch.hevs.a6452.grp2.autostop.autostop.entities.TripEntity;
import ch.hevs.a6452.grp2.autostop.autostop.models.Trip;
import ch.hevs.a6452.grp2.autostop.autostop.R;
import ch.hevs.a6452.grp2.autostop.autostop.WaitingEoTActivity;

public class TrackingService extends Service {

    private static final String TAG = TrackingService.class.getSimpleName();

    //Location provider for GPS
    private FusedLocationProviderClient client;

    //Callback for response from location provider
    private LocationCallback locationCallback;

    //Use to the last location locally
    private SharedPreferences mPrefs;

    private FirebaseDatabase mDatabase;
    private String mTripUid;
    private TripEntity trip;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //When the service is created
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        //Instantiate the shared pref with default preferences
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //Start the notification popup
        startNotification();

        //Start the gps requests
        requestLocationUpdates();
    }

    //When service start
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Retrieve the trip reference
        mTripUid = (String) intent.getExtras().get("uidTrip");

        //Retrieve the trip object
        getTrip(mTripUid);
        return flags;
    }

    //When service stop
    @Override
    public void onDestroy() {

        //Change the trip status
        trip.setStatus(Trip.STATUS_FINISHED);

        //Update the trip
        updateTrip(trip);

        //Stop the gps requests
        client.removeLocationUpdates(locationCallback);
        stopSelf();
    }

    //Create and start the notification popup
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startNotification() {

        //Select channel ID and Name
        String CHANNEL_ONE_ID = "ch.hevs.a6452.grp2.autostop.TRIPSTARTED";
        String CHANNEL_ONE_NAME = "Channel One";
        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            //Create the notification channel
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }

        //Set notification icon
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setChannelId("ch.hevs.a6452.grp2.autostop.TRIPSTARTED")
                .setContentTitle(getString(R.string.title_notif_trip))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .build();

        //Create the notification
        Intent notificationIntent = new Intent(getApplicationContext(), WaitingEoTActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notification.contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        //Start the notification
        startForeground(1003, notification);
    }

    //Retrieve trip object from trip reference
    private void getTrip(String tripUid) {
        mDatabase = FirebaseDatabase.getInstance();
        mTripUid = tripUid;
        DatabaseReference refTrip = mDatabase.getReference(PotostopSession.NODE_TRIP).child(mTripUid);
        refTrip.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                trip = dataSnapshot.getValue(TripEntity.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error while getting current trip");
                System.out.println(databaseError.getMessage());
            }
        });
    }

    //Update the trip with new data
    private void updateTrip(TripEntity trip) {
        mDatabase.getReference(PotostopSession.NODE_TRIP).child(mTripUid).setValue(trip, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                } else {
                    System.out.println(databaseError.getMessage());
                }
            }
        });
    }

    //Start the gps requests
    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();

        //Set interval of gps requests in milliseconds
        request.setInterval(20000);

        //Set accuracy of gps positions
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //Instantiate gps provider
        client = LocationServices.getFusedLocationProviderClient(this);

        //Instantiate callback for positions response
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                //Fill position object with lat, lng and timestamp
                PositionEntity currentPosition = new PositionEntity();
                currentPosition.setLatitude(locationResult.getLastLocation().getLatitude());
                currentPosition.setLongitude(locationResult.getLastLocation().getLongitude());
                currentPosition.setTimestamp(locationResult.getLastLocation().getTime());

                //Add the position to db
                if (currentPosition != null) {
                    Log.i(TAG, "New location : " + currentPosition);

                    //Store last position locally
                    SharedPreferences.Editor mEditor = mPrefs.edit();
                    mEditor.putFloat(PotostopSession.LOCAL_LAST_POSITION_LATITUDE_TAG,
                            currentPosition.getLatitude().floatValue());
                    mEditor.putFloat(PotostopSession.LOCAL_LAST_POSITION_LONGITUDE_TAG,
                            currentPosition.getLongitude().floatValue());
                    mEditor.commit();

                    if (trip != null) {

                        //Add the new position to the trip
                        trip.addPosition(currentPosition);

                        //Update the trip in DB
                        updateTrip(trip);
                    }
                }
            }
        };

        //If the permission is granted we start the tracking
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Start the tracking
            client.requestLocationUpdates(request, locationCallback, null);
        }
    }
}