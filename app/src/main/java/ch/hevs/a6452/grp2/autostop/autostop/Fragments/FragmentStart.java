package ch.hevs.a6452.grp2.autostop.autostop.Fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.PlateEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.PositionEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.TripEntity;
import ch.hevs.a6452.grp2.autostop.autostop.LocationActivity;
import ch.hevs.a6452.grp2.autostop.autostop.Models.Position;
import ch.hevs.a6452.grp2.autostop.autostop.Models.Trip;
import ch.hevs.a6452.grp2.autostop.autostop.PlateActivity;
import ch.hevs.a6452.grp2.autostop.autostop.R;
import ch.hevs.a6452.grp2.autostop.autostop.Utils.PotostopSession;
import ch.hevs.a6452.grp2.autostop.autostop.WaitingEoTActivity;


public class FragmentStart extends Fragment implements View.OnClickListener {

    public static final String TAG = "FragmentStart";
    public static final int REQUEST_NEW_TRIP = 92;

    private Button buttonStartTrip;

    private AppCompatActivity context;

    //Firebase storage
    private StorageReference mStorageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        context = (AppCompatActivity) getActivity();

        buttonStartTrip = (Button) view.findViewById(R.id.buttonStartTrip);
        buttonStartTrip.setOnClickListener(this);

        //Instantiate Firebase storage
        mStorageRef = FirebaseStorage.getInstance().getReference();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }


    @Override
    public void onClick(View view)
    {
        if ( view == buttonStartTrip )
        {
            if(!PotostopSession.hasGpsPermission(context) || !PotostopSession.hasSmsPermission(context))
                askPermissions(context);
            //Check permissions and ask for it if necessary
            if(PotostopSession.hasGpsPermission(context) && PotostopSession.hasSmsPermission(context))
                clickStartTrip();

            else
                Toast.makeText(this.getActivity(), R.string.toast_need_permissions, Toast.LENGTH_SHORT).show();
        }
    }

    private void clickStartTrip()
    {
        Log.i(TAG, "ButtonStartTrip clicked");
        Intent i = new Intent( this.getActivity(), PlateActivity.class );
        startActivityForResult( i, REQUEST_NEW_TRIP );
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == REQUEST_NEW_TRIP)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                Object plate = intent.getExtras().getSerializable( PlateActivity.EXTRA_KEY_PLATE );
                Object destination = intent.getExtras().getSerializable( PlateActivity.EXTRA_KEY_DESTINATION );

                if ( plate instanceof PlateEntity && destination instanceof PositionEntity)
                {
                    insertNewTrip( (PlateEntity) plate, (PositionEntity) destination  );
                }
            }
        }
    }

    private void insertNewTrip( PlateEntity plate, PositionEntity destination )
    {
        DatabaseReference refRoot = FirebaseDatabase.getInstance().getReference();

        final String tripUid = refRoot.push().getKey();
        TripEntity trip = createNewTrip( tripUid, destination, plate.getUid() );

        // Adding the trip
        refRoot.child("/trips/" + tripUid + "/").setValue(trip)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if ( task.isSuccessful() )
                        {
                            startWaitingEoTActivity(tripUid);
                        }
                    }
                });

        //Add the picture in FireBase Storage
        if(plate.getPicture() != null)
            storePlatePicture(plate, trip);

        else
            System.out.println("Picture is null");
    }

    private void storePlatePicture(PlateEntity plate, TripEntity trip){
        String basePath = PotostopSession.STORAGE_PLATES_NODES;
        if(!plate.getPlateNumber().equals(""))
            storeInFireBase(plate.getPicture(), basePath + "/" +
                    plate.getUid() + "/tripId_" + trip.getUid());
        else
            storeInFireBase(plate.getPicture(), basePath + "/" +
                    PotostopSession.STORAGE_UNKNOWN_PLATE_NODES + "/tripId_" + trip.getUid());
    }

    private void storeInFireBase(byte[] picture, String path) {
        StorageReference platesRef = mStorageRef.child(path);
        platesRef.putBytes(picture).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Image uploaded");
            }
        });
    }

    private TripEntity createNewTrip( String tripUid, PositionEntity destination, String plateUid )
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (user != null ? user.getUid() : "NO_USER_ID");

        TripEntity trip = new TripEntity();
        trip.setUid( tripUid );
        trip.setStatus(Trip.STATUS_NOT_STARTED);
        trip.setDestination( destination );
        trip.setOwnerUid( userId );
        trip.setPlateUid( plateUid );
        trip.setPositions( new ArrayList<PositionEntity>());

        return trip;
    }

    private void startWaitingEoTActivity(String tripUid)
    {
        Intent i = new Intent( this.getActivity(), WaitingEoTActivity.class );
        i.putExtra("uidTrip", tripUid);
        startActivity( i );
    }

    public void askPermissions(AppCompatActivity activity){
        PotostopSession.askGps(activity, getContext());
        PotostopSession.askSms(activity, getContext());
    }
}