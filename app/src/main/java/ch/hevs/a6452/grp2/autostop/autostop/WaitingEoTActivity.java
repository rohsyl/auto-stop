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

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.hevs.a6452.grp2.autostop.autostop.Utils.TrackingService;


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
                finish();
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
