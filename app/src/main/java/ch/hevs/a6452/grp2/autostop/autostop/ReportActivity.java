package ch.hevs.a6452.grp2.autostop.autostop;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import butterknife.BindView;
import butterknife.ButterKnife;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.PlateEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.ReportEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.TripEntity;
import ch.hevs.a6452.grp2.autostop.autostop.Utils.PotostopSession;


public class ReportActivity extends AppCompatActivity {


    public static final String TAG = "ReportActivity";

    private FirebaseDatabase mDatabase;

    private ReportEntity myReport;
    private PlateEntity myPlate;


    @BindView(R.id.editTextReport)
    protected EditText textedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance();
    }


    public void clickReport(View v){

        String reportText = textedit.getText().toString();
        long time= System.currentTimeMillis();

        if(reportText.length()==0){
            Toast.makeText(ReportActivity.this, R.string.error_report_text,Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            myReport = new ReportEntity();
            myReport.setMessage(reportText);
            myReport.setTimestamp(time);
            myReport.setTripUid(getIntent().getStringExtra("uidTrip"));

            addReportToPlate(myReport, getIntent().getStringExtra("uidPlate"));
            endReportTrip();
        }
    }

/*
    private void addReport(ReportEntity reportToAdd){
        mDatabase.getReference().child("plates/").child(myPlate.getUid()).setValue(reportToAdd).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    endReportTrip();
                }
            }
        });
    }
    */


    private void endReportTrip()
    {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

    }

    private void addReportToPlate(final ReportEntity report, final String uidPlate)
    {
        DatabaseReference refPlate = mDatabase.getReference(PotostopSession.NODE_PLATE).child(uidPlate);
        refPlate.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "snap"+dataSnapshot);
                myPlate = dataSnapshot.getValue(PlateEntity.class);
                myPlate.setUid(uidPlate);
                myPlate.addReport(report);
                addToPlate(myPlate.getUid());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error while getting current trip");
                System.out.println(databaseError.getMessage());
            }
        });





    }


    private void addToPlate(String uidPlate){

        DatabaseReference refPlate = mDatabase.getReference(PotostopSession.NODE_PLATE).child(uidPlate);

        refPlate.setValue(myPlate, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError == null){
                    System.out.println("TRIP SUCCESSFULLY udapted");
                }
                else {
                    System.out.println("Error while updating trip");
                    System.out.println(databaseError.getMessage());
                }
            }
        });

    }


}
