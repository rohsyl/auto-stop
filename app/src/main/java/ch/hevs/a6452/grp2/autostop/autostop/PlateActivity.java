package ch.hevs.a6452.grp2.autostop.autostop;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.hevs.a6452.grp2.autostop.autostop.entities.PlateEntity;
import ch.hevs.a6452.grp2.autostop.autostop.entities.PositionEntity;
import ch.hevs.a6452.grp2.autostop.autostop.entities.ReportEntity;

// This activity allows the user to input a plate number and/or a plate picture.
// The user can also ignore both and bypass the activity.
public class PlateActivity extends AppCompatActivity
{
    public static final String TAG = "PlateActivity";
    public static final String EXTRA_KEY_PLATE = "plate";
    public static final String EXTRA_KEY_DESTINATION = "destination";

    // Request codes
    public static final int REQUEST_DESTINATION = 90; // Trip destination request code
    private static final int PERMISSIONS_REQUEST = 1515; // OS permission request code
    private static final int CAMERA_RESULT = 2323; // Camera picture request code

    @BindView(R.id.etPlateNumber)
    protected EditText etPlateNumber; // EditText containing the plate number input by the user
    @BindView(R.id.fab_validate_plate)
    protected FloatingActionButton btnValidatePlate; // Validation button
    @BindView(R.id.button_remove_picture)
    protected FloatingActionButton btnRemovePic; // Button used to remove a previously taken picture
    @BindView(R.id.button_picture_plate)
    protected Button btnTakePicture; // Button to start the camera and take a picture of the plate
    @BindView(R.id.ivPlatePicture)
    protected ImageView platePreview; // An ImageView to show the plate picture to the user

    // This variable is used to keep the plate in memory while selecting a location
    private PlateEntity plate;

    // A Bitmap object containing the original plate picture
    private Bitmap picture;

    @Override
    // Creation of the activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plate);

        // ButterKnife binding
        ButterKnife.bind(this);

        // Adding listeners to the buttons
        btnValidatePlate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickGo(view);
            }
        });
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if permission is granted
                checkCamera();
            }
        });
        btnRemovePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Set the picture to null and reset the preview
                picture = null;
                platePreview.setImageResource(R.drawable.ic_plaque_bidon_v4);
            }
        });
    }

    // Function called when the activity instance is saved (notably when the user rotates his/her device)
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        // If the user rotates his/her device, the image is saved so that it can be restored later
        bundle.putByteArray("image", PlateEntity.convertPicture(picture));
    }


    // Function called when the activity instance is restored (notably when the device rotation is over)
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restoring the image bytes to a Bitmap object and to the ImageView
        byte[] bytesPicture = (byte[]) savedInstanceState.getSerializable("image");
        picture = PlateEntity.convertPicture(bytesPicture);
        platePreview.setImageBitmap(picture);
    }

    // Function called when the user validates his/her input
    public void clickGo(View v) {

        //Getting the pate number
        String plateNumber = etPlateNumber.getText().toString();

        // Formatting the plate number and updating the view
        plateNumber = PlateEntity.formatPlateNumber(plateNumber);
        etPlateNumber.setText(plateNumber);

        Log.i(TAG, "Go button clicked");

        // Creating the plate entity with inputs of the user
        plate = new PlateEntity();
        plate.setPlateNumber(plateNumber);
        plate.setPicture(PlateEntity.convertPicture(picture));
        plate.setReports(new ArrayList<ReportEntity>());

        // We check if the plate was flagged by another user
        checkPlateFlaged(plateNumber);
    }

    // Starts the destination activity to get the trip's destination
    private void requestTripDestination() {
        Intent i = new Intent(this, LocationActivity.class);
        startActivityForResult(i, REQUEST_DESTINATION);
    }

    // Function called to handle request results
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Handling the trip destination request
        if (requestCode == REQUEST_DESTINATION) {

            // If the user validated his/her input
            if (resultCode == Activity.RESULT_OK) {
                Object destination = intent.getExtras().getSerializable(LocationActivity.EXTRA_KEY_LOCATION);

                // Once the destination was input, the cumulated result is returned to the calling activity
                if (destination instanceof PositionEntity) {
                    checkPlateAndReturnResult(plate, (PositionEntity) destination);
                }
            }
        }

        // Handling the plate picture request
        if (requestCode == CAMERA_RESULT) {
            try {
                // Store/updates the plate picture and set it as the preview
                picture = (Bitmap) intent.getExtras().get("data");
                platePreview.setImageBitmap(picture);
            } catch (NullPointerException e) {
            }
        }
    }

    // Checks if the plate number points to an already existing plate entity
    // If so, uses the existing entity; calls a method to create it and return the results otherwise.
    private void checkPlateAndReturnResult(final PlateEntity plateToCheck, final PositionEntity destination) {

        // If the plate number is not empty, we return the result
        if (!plate.getPlateNumber().equals("")) {

            // Getting a Firebase reference
            final DatabaseReference refRoot = FirebaseDatabase.getInstance().getReference();

            // Looking for a plate entity with the right plate number
            refRoot.child("plates").orderByChild("plateNumber").equalTo(plateToCheck.getPlateNumber()).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // If the plate exists, we reuse the data we found in the database
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() == 1) {
                        final PlateEntity existingPlate = dataSnapshot.getChildren().iterator().next().getValue(PlateEntity.class);
                        //Set the picture for an existing plate
                        existingPlate.setPicture(PlateEntity.convertPicture(picture));
                        returnPlateLocationResult(existingPlate, destination);
                    } else {
                        // if  a plate entity was not found, we create it and return the result
                        addPlateAndReturnResult(plateToCheck, destination);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        // If plate number is empty, we return the result directly
        else
            returnPlateLocationResult(plate, destination);
    }

    // Generates a UID for the new plate entity and adds it to the database, then returns the result
    private void addPlateAndReturnResult(final PlateEntity plateToAdd, final PositionEntity destination) {
        final DatabaseReference refRoot = FirebaseDatabase.getInstance().getReference();

        // Getting a new UID
        String newPlateUid = refRoot.child("plates").push().getKey();
        plateToAdd.setUid(newPlateUid);

        // Adding the entity to the database
        refRoot.child("plates/" + newPlateUid).setValue(plateToAdd).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    // Returning the result
                    returnPlateLocationResult(plateToAdd, destination);
                }
            }
        });
    }

    // Returns the plate and destination as a request result to the calling activity
    private void returnPlateLocationResult(PlateEntity plate, PositionEntity destination) {
        Intent i = new Intent();
        i.putExtra(EXTRA_KEY_PLATE, plate);
        i.putExtra(EXTRA_KEY_DESTINATION, destination);
        setResult(Activity.RESULT_OK, i);
        finish();
    }

    // Checks if the camera can be used. Opens the camera if granted to do so, asks for the permission otherwise.
    private void checkCamera() {
        // If the camera permission is not granted, we ask the user to get it
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            // Camera permission request
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST);
        }

        // Otherwise the camera is started
        else
            startCamera();
    }

    // Handling the camera permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {

        // If permission was not granted, a warning message is displayed
        if (requestCode != PERMISSIONS_REQUEST || grantResults.length == 1
                && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.noCameraGranted, Toast.LENGTH_SHORT).show();
            return;
        }

        // If the permission was granted, the camera is started
        else
            startCamera();
    }

    // Starts the camera
    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_RESULT);
    }

    // Checks if the plate was flagged as dangerous by another user
    private void checkPlateFlaged(final String plateToCheck) {

        // Getting a Firebase reference
        final DatabaseReference refRoot = FirebaseDatabase.getInstance().getReference();

        // looks for the plate number in the database
        refRoot.child("plates").orderByChild("plateNumber").equalTo(plateToCheck).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    PlateEntity plateToCheck = dataSnapshot.getChildren().iterator().next().getValue(PlateEntity.class);

                    // An alert is shown if the plate was flagged
                    if (plateToCheck.isflaged()) {

                        showAlertDialogButtonClicked();
                    }

                    // If the plate was not flagged, the result is returned
                    else {
                        requestTripDestination();
                    }
                }

                // If the plate was not found, the result is returned
                else {
                    requestTripDestination();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // Shows an alert to tell the user the plate was flagged
    public void showAlertDialogButtonClicked() {

        // setup the alert builder
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("AlertDialog");
        builder.setMessage("This plate has a report do you want to continue the trip or escape??");

        // add the buttons
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                requestTripDestination();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                CancelTrip();
            }
        });
        // create and show the alert dialog
        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            }
        });
        dialog.show();
    }

    // Finishes this activity and goes back to the main activity
    private void CancelTrip() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
