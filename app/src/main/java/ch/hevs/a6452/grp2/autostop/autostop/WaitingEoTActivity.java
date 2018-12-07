package ch.hevs.a6452.grp2.autostop.autostop;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.PositionEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.TripEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Models.Trip;
import ch.hevs.a6452.grp2.autostop.autostop.ViewModels.TripViewModel;


public class WaitingEoTActivity extends AppCompatActivity {

    public static final String TAG = "WaitingEoTActivity";

    @BindView(R.id.arrivTime)
    protected TextView arrivTime;

    @BindView(R.id.arrivTimeClock)
    protected TextClock textClock;

    @BindView(R.id.progressBar)
    protected ProgressBar progressBar;

    @BindView(R.id.buttonEndTrip)
    protected Button buttonEndTrip;

    private FusedLocationProviderClient client;
    private LocationCallback locationCallback;
    private PositionEntity currentPosition;
    private TripEntity trip;
    private TripViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_waitingeot);

        ButterKnife.bind(this);

        TripViewModel.Factory factory = new TripViewModel.Factory(this.getApplication(), getIntent().getStringExtra("uidTrip"));

        mViewModel = ViewModelProviders.of(this, factory).get(TripViewModel.class);

        currentPosition = new PositionEntity();

        observeViewModel();

        requestLocationUpdates();

        //Action listener for VALIDATE
        buttonEndTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "buttonEndTrip clicked");
                client.removeLocationUpdates(locationCallback);
                trip.setStatus(Trip.STATUS_FINISHED);
                mViewModel.updateTrip(trip);
                Intent intent = new Intent(WaitingEoTActivity.this, RatingTripActivity.class);
                intent.putExtra("uidTrip", trip.getUid());
                startActivity(intent);
            }
        });
    }

    //Our observer to retriev the trip object
    private void observeViewModel() {
        Log.i(TAG, "Observe trip : "+getIntent().getStringExtra("uidTrip"));
        mViewModel.getTrip().observe(this, new Observer<TripEntity>() {
            @Override
            public void onChanged(@Nullable TripEntity tripEntity) {
                trip = tripEntity;
            }
        });
    }


    //Use to start tracking
    private void requestLocationUpdates() {
        Log.i(TAG, "RequestLocationUpdates");
        LocationRequest request = new LocationRequest();

        //Interval between each positions
        request.setInterval(10000);

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
