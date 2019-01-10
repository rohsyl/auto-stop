package ch.hevs.a6452.grp2.autostop.autostop.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ch.hevs.a6452.grp2.autostop.autostop.Entites.PersonEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.TripEntity;

public class PotostopSession {

    public static final int PERMISSIONS_LOCALIZATION_REQUEST = 100;
    public static final int PERMISSIONS_SMS_REQUEST = 200;

    public static final String LOCAL_EMERGENCY_NUMBER_TAG = "emergency_contact";
    public static final String LOCAL_LAST_POSITION_LATITUDE_TAG = "posLat";
    public static final String LOCAL_LAST_POSITION_LONGITUDE_TAG = "posLong";

    public static final String NODE_PERSON = "persons";
    public static final String NODE_TRIP = "trips";
    public static final String NODE_PLATE = "plates";
    public static final String NODE_REPORT = "reports";
    public static final String NODE_ALERT = "alerts";
    public static final String STORAGE_PLATES_NODES = "Plates";
    public static final String STORAGE_UNKNOWN_PLATE_NODES = "UnknownPlates";

    public static void askGps(AppCompatActivity activity, Context context){
        //Check if permission is not granted
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_LOCALIZATION_REQUEST);
        }
    }

    public static boolean hasGpsPermission(AppCompatActivity context){
        //Check if permission is not granted
        return hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public static void askSms(AppCompatActivity activity, Context context){
        //Check if permission is not granted
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSIONS_SMS_REQUEST);
        }
    }

    public static boolean hasSmsPermission(AppCompatActivity context){
        //Check if permission is not granted
        return hasPermission(context, Manifest.permission.SEND_SMS);
    }

    private static boolean hasPermission(AppCompatActivity context, String permission){
        //Check if permission is not granted
        return (ContextCompat.checkSelfPermission(context,
                permission) == PackageManager.PERMISSION_GRANTED);
    }

}

