package ch.hevs.a6452.grp2.autostop.autostop.Fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
import ch.hevs.a6452.grp2.autostop.autostop.WaitingEoTActivity;


public class FragmentStart extends Fragment implements View.OnClickListener {

    public static final String TAG = "FragmentStart";
    public static final int REQUEST_NEW_TRIP = 92;

    private final int PERMISSIONS_REQUEST = 100;

    private Button buttonStartTrip;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        buttonStartTrip = (Button) view.findViewById(R.id.buttonStartTrip);
        buttonStartTrip.setOnClickListener(this);

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
            clickStartTrip();
        }
    }

    private void clickStartTrip()
    {

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
            return;
        }

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



        // Inserting in Firebase
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
}
