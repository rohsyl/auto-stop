package ch.hevs.a6452.grp2.autostop.autostop;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import butterknife.BindView;
import butterknife.ButterKnife;
import ch.hevs.a6452.grp2.autostop.autostop.Entites.ReportEntity;






public class ReportActivity extends AppCompatActivity {


    public static final String TAG = "ReportActivity";

    private ReportEntity reportEntity;


    @BindView(R.id.editTextReport)
    protected EditText textedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
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
            reportEntity = new ReportEntity();
            reportEntity.setMessage(reportText);
            reportEntity.setTimestamp(time);
            reportEntity.setTripUid(getIntent().getStringExtra("uidTrip"));
            String plate = getIntent().getStringExtra("uidPlate");
            Log.i(TAG,"report\""+reportEntity+"\"");
            addReport(reportEntity, plate);

        }
    }


    private void addReport(ReportEntity reportToAdd, String plateUID){
        final DatabaseReference refRoot = FirebaseDatabase.getInstance().getReference();
        String newReportUid = refRoot.child("plates").push().getKey();
        reportToAdd.setUid(newReportUid);

        refRoot.child("plates/").child(plateUID).setValue(reportToAdd).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    endReportTrip();
                }
            }
        });
    }
    private void endReportTrip()
    {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }


}
