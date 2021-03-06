package ch.hevs.a6452.grp2.autostop.autostop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RatingTripActivity extends AppCompatActivity {

    public static final String TAG = "RatingTripActivity";


    @BindView(R.id.button_ok)
    protected Button button_ok;
    @BindView(R.id.buttonReport)
    protected Button buttonReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rate);

        ButterKnife.bind(this);

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Ok clicked");

                //Go back to the main activity
                Intent intent = new Intent(RatingTripActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Sart the report activity
                Intent i = new Intent(RatingTripActivity.this, ReportActivity.class);
                i.putExtra("uidTrip", (getIntent().getStringExtra("uidTrip")));
                startActivity(i);
                finish();
            }
        });
    }
}
