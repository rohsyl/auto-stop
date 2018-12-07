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
import ch.hevs.a6452.grp2.autostop.autostop.Utils.TrackingService;
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

    private  String uidTrip;

    private Intent serviceTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_waitingeot);

        ButterKnife.bind(this);

        uidTrip = getIntent().getStringExtra("uidTrip");

        startTracking();


        //Action listener for VALIDATE
        buttonEndTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "buttonEndTrip clicked");
                stopService(serviceTracking);
                Intent intent = new Intent(WaitingEoTActivity.this, RatingTripActivity.class);
                intent.putExtra("uidTrip", uidTrip);
                startActivity(intent);
            }
        });
    }


    private void startTracking(){


        Log.i(TAG, "Trip uid  : "+ uidTrip);
        serviceTracking = new Intent(this, TrackingService.class);
        serviceTracking.putExtra("uidTrip", uidTrip);
        startService(serviceTracking);

    }

}
