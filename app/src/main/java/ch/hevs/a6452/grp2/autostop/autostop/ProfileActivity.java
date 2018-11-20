package ch.hevs.a6452.grp2.autostop.autostop;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.hevs.a6452.grp2.autostop.autostop.Fragments.Picker.DatePickerFragment;
import ch.hevs.a6452.grp2.autostop.autostop.Utils.FirebaseConverter;

public class ProfileActivity extends AppCompatActivity {


    @BindView(R.id.profile_fullname)
    protected EditText txtFullname;

    @BindView(R.id.profile_sex)
    protected AppCompatSpinner spiSex;

    @BindView(R.id.profile_birthdate_btn)
    protected Button btnDate;

    @BindView(R.id.profile_birthdate_display)
    protected TextView lblDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.profile_sex_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spiSex.setAdapter(adapter);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment dialogFragmentDatePicker = new DatePickerFragment();
                Long date = Calendar.getInstance().getTimeInMillis();

                dialogFragmentDatePicker.setDate(new Date(date));
                dialogFragmentDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.YEAR, y);
                        c.set(Calendar.MONTH, m);
                        c.set(Calendar.DAY_OF_MONTH, d);

                        lblDate.setText(FirebaseConverter.toNiceDateFormat(c.getTimeInMillis()));
                    }
                });
                dialogFragmentDatePicker.show(getFragmentManager(), "datepicker");
            }
        });

    }

}
