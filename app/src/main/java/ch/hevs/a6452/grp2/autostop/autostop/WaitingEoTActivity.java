package ch.hevs.a6452.grp2.autostop.autostop;

import android.Manifest;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.AlertEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.PlateEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.PositionEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Models.Position;
import ch.hevs.a6452.grp2.autostop.autostop.Utils.PotostopSession;
import ch.hevs.a6452.grp2.autostop.autostop.Utils.TrackingService;


public class WaitingEoTActivity extends AppCompatActivity {
    //TODO : Allow the user to come back here if the trip is not finished.
    public static final String TAG = "WaitingEoTActivity";

    @BindView(R.id.arrivTime)
    protected TextView arrivTime;

    @BindView(R.id.arrivTimeClock)
    protected TextClock textClock;

    @BindView(R.id.progressBar)
    protected ProgressBar progressBar;

    @BindView(R.id.buttonEndTrip)
    protected Button buttonEndTrip;

    @BindView(R.id.buttonSendAlert)
    protected Button buttonAlert;

    private SharedPreferences mPrefs;
    private SmsManager smsManager;
    private PositionEntity lastPosition;
    private FirebaseDatabase mDatabase;
    private String uidTrip;

    private Intent serviceTracking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_waitingeot);

        ButterKnife.bind(this);

        uidTrip = getIntent().getStringExtra("uidTrip");
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance();

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

        buttonAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAlertDialog().show();
            }
        });
    }

    private AlertDialog createAlertDialog() {
        //Generate the dialog to confirm the alert
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.manual_alert_title);
        builder.setMessage(R.string.manual_alert_message);
        builder.setPositiveButton(R.string.manual_alert_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                confirmPopup();
                dialogInterface.cancel();
            }
        });
        builder.setNegativeButton(R.string.manual_alert_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alert.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(getResources().getColor(R.color.colorBlack));
                alert.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.colorAlert));
            }
        });
        return alert;
    }

    private void confirmPopup() {
        //Load the emergency number from local storage
        String emergencyNumber = mPrefs.getString(PotostopSession.LOCAL_EMERGENCY_NUMBER_TAG, "");
        AlertEntity alert = createAlert(emergencyNumber);
        //Send the SMS
        sendSms(forgeSms(), emergencyNumber);
        saveAlertInFirebase(alert);
    }

    private AlertEntity createAlert(String sendTo) {
        //Generate the alert
        AlertEntity alert = new AlertEntity();
        alert.setTimestamp(System.currentTimeMillis());
        alert.setTripUid(uidTrip);
        alert.setLastPosition(loadLastPosition());
        alert.setSendTo(sendTo);
        return alert;
    }

    private void saveAlertInFirebase(final AlertEntity alert) {
        DatabaseReference refRoot = mDatabase.getReference();
        final String uidAlert = refRoot.push().getKey();
        final DatabaseReference refAlert = mDatabase.getReference(PotostopSession.NODE_ALERT).child(uidAlert);
        //Store the alert in Firebase
        refAlert.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                refAlert.setValue(alert);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }

    private PositionEntity loadLastPosition() {
        //Get the last position recorded from the local storage
        float posLat = mPrefs.getFloat(PotostopSession.LOCAL_LAST_POSITION_LATITUDE_TAG, 0);
        float posLong = mPrefs.getFloat(PotostopSession.LOCAL_LAST_POSITION_LONGITUDE_TAG, 0);
        PositionEntity lastPosition = new PositionEntity();
        lastPosition.setLatitude((double) posLat);
        lastPosition.setLongitude((double) posLong);
        return lastPosition;
    }

    private void sendSms(String emergencyMessage, String number) {
        smsManager = SmsManager.getDefault();
        try {
            //Send the SMS
            smsManager.sendTextMessage(number, null, emergencyMessage, null, null);
            Toast.makeText(getApplicationContext(), R.string.toast_emergency_sms_send, Toast.LENGTH_LONG);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.toast_emergency_sms_failure, Toast.LENGTH_LONG);
        }
    }

    private String forgeSms() {
        PositionEntity lastPosition = loadLastPosition();
        //Display the position on the map
        String gMapsUrl = "https://www.google.com/maps/place/" + lastPosition.getLatitude() + "N+" +
                lastPosition.getLongitude() + "E";
        String message = "URGENCE : Je suis en stop et j'ai besoin d'aide. " +
                "Dernière position connue : " + gMapsUrl;
        return message;
    }

    private void startTracking() {
        Log.i(TAG, "Trip uid  : " + uidTrip);
        serviceTracking = new Intent(this, TrackingService.class);
        serviceTracking.putExtra("uidTrip", uidTrip);
        startService(serviceTracking);
    }
}