package ch.hevs.a6452.grp2.autostop.autostop;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.PersonEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.PositionEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.TripEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Models.Position;
import ch.hevs.a6452.grp2.autostop.autostop.Utils.FirebaseConverter;
import ch.hevs.a6452.grp2.autostop.autostop.Utils.PotostopSession;
import ch.hevs.a6452.grp2.autostop.autostop.ViewModels.ProfileViewModel;
import ch.hevs.a6452.grp2.autostop.autostop.ViewModels.TripViewModel;

import static android.widget.Toast.makeText;

public class WaitingEoTActivity extends AppCompatActivity {

    private TripViewModel mViewModel;

    private TripEntity trip;

    public static final String TAG = "WaitingEoTActivity";

    @BindView(R.id.arrivTime)
    protected TextView arrivTime;

    @BindView(R.id.arrivTimeClock)
    protected TextClock textClock;

    @BindView(R.id.progressBar)
    protected ProgressBar progressBar;

    @BindView(R.id.buttonEndTrip)
    protected Button buttonEndTrip;

    //DatabaseReference tripRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_waitingeot);

        ButterKnife.bind(this);


        TripViewModel.Factory factory = new TripViewModel.Factory(this.getApplication(), "-LStZCH87vXHErdvgVYQ" );

        mViewModel = ViewModelProviders.of(this, factory).get(TripViewModel.class);


        //observeViewModel();

        //requestLocationUpdates();

        //Action listener for VALIDATE
        buttonEndTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "buttonEndTrip clicked");

                Intent intent = new Intent(WaitingEoTActivity.this, RatingTripActivity.class);
                startActivity(intent);
            }
        });
    }


    private void observeViewModel(){

        mViewModel.getTrip().observe(this, new Observer<TripEntity>() {
            @Override
            public void onChanged(@Nullable TripEntity tripEntity) {
                trip = tripEntity;
                Log.d("TripVM", "-----------Trip observed");
                if(trip!=null){
                    Log.d("TripVM", "-----------TripNOTNULL"+trip.getUid());
                }
            }
        });
    }


    private void requestLocationUpdates() {
        Log.d("TripVM", "-----------requestLocationUpdates");
        LocationRequest request = new LocationRequest();

        request.setInterval(10000);

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);


        if (permission == PackageManager.PERMISSION_GRANTED) {

           final Toast toast = makeText(this, "new location !!!", Toast.LENGTH_SHORT);


            client.requestLocationUpdates(request, new LocationCallback() {

                @Override
                public void onLocationResult(LocationResult locationResult) {
                    toast.show();

                    PositionEntity currentPosition = new PositionEntity();
                    currentPosition.setLatitude(locationResult.getLastLocation().getLatitude());
                    currentPosition.setLongitude(locationResult.getLastLocation().getLongitude());
                    currentPosition.setTimestamp(locationResult.getLastLocation().getTime());

                    if (currentPosition != null ) {
                        Log.d("TripVM", "-----------set current position");
                        //Log.d("TripVM", "-----------"+trip.getUid());
                        //trip.addPosition(currentPosition);
                        //mViewModel.updateTrip(trip);
                    }

                }
            }, null);
        }
    }


}
