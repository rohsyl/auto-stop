package ch.hevs.a6452.grp2.autostop.autostop.Fragments;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ch.hevs.a6452.grp2.autostop.autostop.MainActivity;
import ch.hevs.a6452.grp2.autostop.autostop.R;
import ch.hevs.a6452.grp2.autostop.autostop.Utils.FirebaseConverter;
import ch.hevs.a6452.grp2.autostop.autostop.ViewModels.ProfileViewModel;
import android.content.Intent;


public class FragmentProfile extends Fragment {

    private ProfileViewModel mViewModel;

    public static FragmentProfile newInstance() {
        return new FragmentProfile();
    }

    @BindView(R.id.profile_fullname)
    protected EditText txtFullname;

    @BindView(R.id.profile_sex)
    protected AppCompatSpinner spiSex;

    @BindView(R.id.profile_birthdate_btn)
    protected Button btnDate;

    @BindView(R.id.profile_birthdate_display)
    protected TextView lblDate;

    @BindView(R.id.profile_emergency_phone)
    protected EditText txtEmergencyPhone;

    @BindView(R.id.profile_emergency_mail)
    protected EditText txtEmergencyMail;

    @BindView(R.id.fab_save_team)
    protected FloatingActionButton btnSave;

    private Long birthdate = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel


        populateTitleSpinner();


        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long date = Calendar.getInstance().getTimeInMillis();

                DatePickerFragment dialogFragmentDatePicker = new DatePickerFragment();
                dialogFragmentDatePicker.setDate(new Date(date));
                dialogFragmentDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.YEAR, y);
                        c.set(Calendar.MONTH, m);
                        c.set(Calendar.DAY_OF_MONTH, d);

                        birthdate = c.getTimeInMillis();

                        lblDate.setText(FirebaseConverter.toNiceDateFormat(c.getTimeInMillis()));
                    }
                });
                dialogFragmentDatePicker.show(getFragmentManager(), "datepicker");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //reset errors
                txtFullname.setError(null);
                lblDate.setError(null);
                txtEmergencyPhone.setError(null);
                txtEmergencyMail.setError(null);

                String fullname = txtFullname.getText().toString();
                String emergencyPhone = txtEmergencyPhone.getText().toString();
                String emergencyEmail = txtEmergencyMail.getText().toString();

                boolean cancel = false;
                View focusView = null;

                if(TextUtils.isEmpty(fullname)){
                    txtFullname.setError(getString(R.string.profile_error_fullname_empty));
                    focusView = txtFullname;
                    cancel = true;
                }

                if(birthdate == null){
                    btnDate.setError(getString(R.string.profile_error_date_empty));
                    focusView = btnDate;
                    cancel = true;
                }

                if(TextUtils.isEmpty((emergencyPhone))){
                    txtEmergencyPhone.setError(getString(R.string.profile_error_phone_empty));
                    focusView = txtEmergencyPhone;
                    cancel = true;
                }

                if(TextUtils.isEmpty((emergencyEmail))){
                    txtEmergencyMail.setError(getString(R.string.profile_error_mail_empty));
                    focusView = txtEmergencyMail;
                    cancel = true;
                }


                if(cancel){
                    focusView.requestFocus();
                }
                else{
                    // TODO : save to db

                     mainActivity();
                }
            }
        });
    }

    private void mainActivity()
    {
        Intent i = new Intent(this.getActivity(), MainActivity.class);
        startActivity(i);


    }

    private void populateTitleSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.profile_sex_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spiSex.setAdapter(adapter);
    }

}
