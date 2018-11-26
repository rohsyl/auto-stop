package ch.hevs.a6452.grp2.autostop.autostop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlateActivity extends AppCompatActivity
{
    public static final String TAG = "PlateActivity";

    @BindView(value=R.id.etPlateNumber)
    protected EditText tvPlateNumber;

    @BindView(value=R.id.spCanton)
    protected Spinner spCanton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plate);

        ButterKnife.bind(this);
    }

    public void clickGo(View v)
    {
        String plateNumber = spCanton.getSelectedItem().toString();
        plateNumber += tvPlateNumber.getText().toString();

        if ( isPlateNumberValid( plateNumber ) )
        {
            Log.i(TAG, "Go button clicked");
            Intent i = new Intent(this, LocationActivity.class);
            startActivity( i );
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
}
