package ch.hevs.a6452.grp2.autostop.autostop;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.PlateEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.PositionEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.TripEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Models.Report;
import ch.hevs.a6452.grp2.autostop.autostop.Models.Trip;

public class PlateActivity extends AppCompatActivity
{
    public static final String TAG = "PlateActivity";
    public static final String EXTRA_KEY_PLATE = "plate";
    public static final String EXTRA_KEY_DESTINATION = "destination";
    public static final int REQUEST_DESTINATION = 90;

    @BindView(value=R.id.etPlateNumber)
    protected EditText tvPlateNumber;

    @BindView(value=R.id.spCanton)
    protected Spinner spCanton;

    @BindView(R.id.fab_validate_plate)
    protected FloatingActionButton btnValidatePlate;

    // This variable is used to keep the plate in memory while selecting a location
    private PlateEntity plate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plate);

        ButterKnife.bind(this);

        btnValidatePlate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickGo(view);
            }
        });
    }

    public void clickGo(View v)
    {
        String plateNumber = spCanton.getSelectedItem().toString();
        plateNumber += tvPlateNumber.getText().toString();

        if ( isPlateNumberValid( plateNumber ) )
        {
            Log.i(TAG, "Go button clicked");

            plate = new PlateEntity();
            plate.setPlateNumber( plateNumber );
            plate.setReports(new ArrayList<Report>());
            requestTripDestination();
        }

        else
        {
            Toast.makeText(this, "Invalid plate number!", Toast.LENGTH_LONG).show();
            Log.i(TAG, "Invalid plate number: \""+plateNumber+"\"");
        }
    }

    private boolean isPlateNumberValid( String plateNumber )
    {
        String platePattern = "[A-Z]{2}[0-9]+";

        return plateNumber.matches( platePattern );
    }

    private void requestTripDestination()
    {
        Intent i = new Intent(this, LocationActivity.class);
        startActivityForResult( i, REQUEST_DESTINATION );
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == REQUEST_DESTINATION)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                Object destination = intent.getExtras().getSerializable( LocationActivity.EXTRA_KEY_LOCATION );

                if ( destination instanceof PositionEntity )
                {
                    checkPlateAndReturnResult( plate, (PositionEntity) destination );
                }
            }
        }
    }

    private void checkPlateAndReturnResult(final PlateEntity plateToCheck, final PositionEntity destination )
    {
        // TODO Fabien: Ajouter la plaque si elle n'existe pas, sinon la récupérer

        final DatabaseReference refRoot = FirebaseDatabase.getInstance().getReference();

        refRoot.child("plates").orderByChild("plateNumber").equalTo(plateToCheck.getPlateNumber()).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                // If the plate exists, we reuse the data we found in the database
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() == 1 )
                {
                    final PlateEntity existingPlate = dataSnapshot.getChildren().iterator().next().getValue(PlateEntity.class);
                    returnPlateLocationResult( existingPlate, (PositionEntity) destination  );
                }

                else
                {
                    addPlateAndReturnResult( plateToCheck, destination );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addPlateAndReturnResult( final PlateEntity plateToAdd, final PositionEntity destination )
    {
        final DatabaseReference refRoot = FirebaseDatabase.getInstance().getReference();

        String newPlateUid = refRoot.child("plates").push().getKey();
        plateToAdd.setUid( newPlateUid );
        refRoot.child("plates/" + newPlateUid).setValue( plateToAdd ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    returnPlateLocationResult( plateToAdd, (PositionEntity) destination  );
                }
            }
        });
    }

    private void returnPlateLocationResult( PlateEntity plate, PositionEntity destination )
    {
        Intent i = new Intent();
        i.putExtra(EXTRA_KEY_PLATE, plate);
        i.putExtra(EXTRA_KEY_DESTINATION, destination);
        setResult(Activity.RESULT_OK, i);
        finish();
    }
}
