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

public class PlateActivity extends AppCompatActivity {
    public static final String TAG = "PlateActivity";
    public static final String EXTRA_KEY_PLATE = "plate";
    public static final String EXTRA_KEY_DESTINATION = "destination";
    public static final int REQUEST_DESTINATION = 90;
    private static final int PERMISSIONS_REQUEST = 1515;
    private static final int CAMERA_RESULT = 2323;

    @BindView(R.id.etPlateNumber)
    protected EditText etPlateNumber;
    @BindView(R.id.fab_validate_plate)
    protected FloatingActionButton btnValidatePlate;
    @BindView(R.id.button_remove_picture)
    protected FloatingActionButton btnRemovePic;
    @BindView(R.id.button_picture_plate)
    protected Button btnTakePicture;
    @BindView(R.id.ivPlatePicture)
    protected ImageView platePreview;

    // This variable is used to keep the plate in memory while selecting a location
    private PlateEntity plate;
    private Bitmap picture;

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

    //Prevent the loss of the image if the user rotate his phone
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putByteArray("image", PlateEntity.convertPicture(picture));
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        byte[] bytesPicture = (byte[]) savedInstanceState.getSerializable("image");
        picture = PlateEntity.convertPicture(bytesPicture);
        platePreview.setImageBitmap(picture);
    }

    public void clickGo(View v) {
        String plateNumber = etPlateNumber.getText().toString();

        // Formatting the plate number and updating the view
        plateNumber = PlateEntity.formatPlateNumber(plateNumber);
        etPlateNumber.setText(plateNumber);

        Log.i(TAG, "Go button clicked");

        //Create the plate entity with inputs of the user
        plate = new PlateEntity();
        plate.setPlateNumber(plateNumber);
        plate.setPicture(PlateEntity.convertPicture(picture));
        plate.setReports(new ArrayList<ReportEntity>());

        checkPlateFlaged(plateNumber);

    }

    private void requestTripDestination() {
        Intent i = new Intent(this, LocationActivity.class);
        startActivityForResult(i, REQUEST_DESTINATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_DESTINATION) {
            if (resultCode == Activity.RESULT_OK) {
                Object destination = intent.getExtras().getSerializable(LocationActivity.EXTRA_KEY_LOCATION);

                if (destination instanceof PositionEntity) {
                    checkPlateAndReturnResult(plate, (PositionEntity) destination);
                }
            }
        }
        //If the request code is from the camera
        if (requestCode == CAMERA_RESULT) {
            try {
                //Store the picture and set it as the preview
                picture = (Bitmap) intent.getExtras().get("data");
                platePreview.setImageBitmap(picture);
            } catch (NullPointerException e) {
            }
        }
    }

    private void checkPlateAndReturnResult(final PlateEntity plateToCheck, final PositionEntity destination) {
        if (!plate.getPlateNumber().equals("")) {
            final DatabaseReference refRoot = FirebaseDatabase.getInstance().getReference();

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
                        //If plate number is empty
                        addPlateAndReturnResult(plateToCheck, destination);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        //If plate number is empty
        else
            returnPlateLocationResult(plate, destination);
    }

    private void addPlateAndReturnResult(final PlateEntity plateToAdd, final PositionEntity destination) {
        final DatabaseReference refRoot = FirebaseDatabase.getInstance().getReference();

        String newPlateUid = refRoot.child("plates").push().getKey();
        plateToAdd.setUid(newPlateUid);

        refRoot.child("plates/" + newPlateUid).setValue(plateToAdd).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    returnPlateLocationResult(plateToAdd, destination);
                }
            }
        });
    }

    private void returnPlateLocationResult(PlateEntity plate, PositionEntity destination) {
        Intent i = new Intent();
        i.putExtra(EXTRA_KEY_PLATE, plate);
        i.putExtra(EXTRA_KEY_DESTINATION, destination);
        setResult(Activity.RESULT_OK, i);
        finish();
    }

    private void checkCamera() {
        //Check if permission is not granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //If not : ask the user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST);
        }
        //Start camera activity
        else
            startCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        //If no permission, display warning message
        if (requestCode != PERMISSIONS_REQUEST || grantResults.length == 1
                && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.noCameraGranted, Toast.LENGTH_SHORT).show();
            return;
        }
        //Start camera activity
        else
            startCamera();
    }

    //Create intent for the camera
    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_RESULT);
    }

    private void checkPlateFlaged(final String plateToCheck) {

        final DatabaseReference refRoot = FirebaseDatabase.getInstance().getReference();
        refRoot.child("plates").orderByChild("plateNumber").equalTo(plateToCheck).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    PlateEntity plateToCheck = dataSnapshot.getChildren().iterator().next().getValue(PlateEntity.class);

                    if (plateToCheck.isflaged()) {

                        showAlertDialogButtonClicked();
                    } else {
                        requestTripDestination();
                    }
                } else {
                    requestTripDestination();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

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

    private void CancelTrip() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
