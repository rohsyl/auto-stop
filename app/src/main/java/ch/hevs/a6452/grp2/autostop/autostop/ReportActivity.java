package ch.hevs.a6452.grp2.autostop.autostop;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import butterknife.BindView;
import butterknife.ButterKnife;
import ch.hevs.a6452.grp2.autostop.autostop.entities.PlateEntity;
import ch.hevs.a6452.grp2.autostop.autostop.entities.ReportEntity;
import ch.hevs.a6452.grp2.autostop.autostop.utils.PotostopSession;


public class ReportActivity extends AppCompatActivity {

    public static final String TAG = "ReportActivity";

    private FirebaseDatabase mDatabase;

    private ReportEntity myReport;
    private String uidTrip;
    private String uidPlate;

    @BindView(R.id.editTextReport)
    protected EditText textedit;
    @BindView(R.id.editPlateNumber)
    protected EditText editPlateNumber;


    // Method of creating the report activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance();
        //Fetch the intent
        uidTrip = getIntent().getStringExtra("uidTrip");
        //Load the current plate
        getPlateUid(uidTrip);
    }

    // Method when you click on the report button
    public void clickReport(View v) {
        String reportText = textedit.getText().toString();
        String plateNumber = PlateEntity.formatPlateNumber(editPlateNumber.getText().toString());
        long time = System.currentTimeMillis();

        /* if the text of the report is empty
         *  we return a Toast saying : "What happende ?" */
        if (reportText.length() == 0) {
            Toast.makeText(ReportActivity.this, R.string.error_report_text, Toast.LENGTH_SHORT).show();
            return;
        }
        /* We create a new ReportEntity and fill all the fileds of the report*/
        else {
            myReport = new ReportEntity();
            myReport.setMessage(reportText);
            myReport.setTimestamp(time);
            myReport.setTripUid(uidTrip);
            myReport.setPlateNumber(plateNumber);
            storeReportInFirebase(myReport);
            endReportTrip();
        }
    }

    // Method at the end of the report the user return to the main activity
    private void endReportTrip() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    // This method returns the ID of the plate for the trip ended now
    private void getPlateUid(final String uidTrip) {
        final DatabaseReference refTrip = mDatabase.getReference(PotostopSession.NODE_TRIP).child(uidTrip).child("plateUid");
        refTrip.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uidPlate = (String) dataSnapshot.getValue();
                getCurrentPlate(uidPlate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // This method returns the plate number of the ID plate we get above
    private void getCurrentPlate(final String uidPlate) {
        try {
            final DatabaseReference refPlate = mDatabase.getReference(PotostopSession.NODE_PLATE).child(uidPlate).child("plateNumber");
            refPlate.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String plateNumber = (String) dataSnapshot.getValue();
                    editPlateNumber.setText(plateNumber);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            System.out.println("No plate set at the creation of the trip.");
            return;
        }
    }

    // Method to record the report in firebase
    private void storeReportInFirebase(final ReportEntity report) {
        DatabaseReference refRoot = mDatabase.getReference();
        final String uidReport = refRoot.push().getKey();
        final DatabaseReference refReport = mDatabase.getReference(PotostopSession.NODE_REPORT).child(uidReport);
        refReport.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                refReport.setValue(report);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }
}
