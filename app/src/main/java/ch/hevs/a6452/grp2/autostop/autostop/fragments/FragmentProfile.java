package ch.hevs.a6452.grp2.autostop.autostop.fragments;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.hevs.a6452.grp2.autostop.autostop.entities.PersonEntity;
import ch.hevs.a6452.grp2.autostop.autostop.fragments.Picker.DatePickerFragment;
import ch.hevs.a6452.grp2.autostop.autostop.MainActivity;
import ch.hevs.a6452.grp2.autostop.autostop.R;
import ch.hevs.a6452.grp2.autostop.autostop.utils.FirebaseConverter;
import ch.hevs.a6452.grp2.autostop.autostop.utils.PotostopSession;
import ch.hevs.a6452.grp2.autostop.autostop.viewmodels.ProfileViewModel;
import android.content.Intent;
import android.widget.Toast;

public class FragmentProfile extends Fragment {

    private ProfileViewModel mViewModel;

    public static FragmentProfile newInstance() {
        return new FragmentProfile();
    }

    // bind with view

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

    private PersonEntity person = null;

    private SharedPreferences mPrefs;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        // get prefs
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // define view model
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        populateTitleSpinner();

        observeViewModel();

        // date picker for birth date
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long date = Calendar.getInstance().getTimeInMillis();
                if(birthdate != null)
                    date = birthdate;

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

                        lblDate.setText(PotostopSession.toNiceDateFormat(c.getTimeInMillis()));
                    }
                });
                dialogFragmentDatePicker.show(getFragmentManager(), "datepicker");
            }
        });

        // listener for save button
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
                //Remove spaces
                emergencyPhone = emergencyPhone.replace("\\s", "");
                String emergencyEmail = txtEmergencyMail.getText().toString();

                boolean cancel = false;
                View focusView = null;

                // valid input check

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

                if(!isPhoneNumberValid(emergencyPhone)){
                    txtEmergencyPhone.setError(getString(R.string.profile_error_phone_error));
                    focusView = txtEmergencyPhone;
                    cancel = true;
                }

                if(TextUtils.isEmpty((emergencyEmail))){
                    txtEmergencyMail.setError(getString(R.string.profile_error_mail_empty));
                    focusView = txtEmergencyMail;
                    cancel = true;
                }

                if(!isEmailValid(emergencyEmail)){
                    txtEmergencyMail.setError(getString(R.string.label_mail_error));
                    focusView = txtEmergencyMail;
                    cancel = true;
                }


                if(cancel){
                    focusView.requestFocus();
                }
                // populate profile
                else{
                    person.setSex(spiSex.getSelectedItemPosition());
                    person.setFullname(txtFullname.getText().toString());
                    person.setBirthDate(birthdate);

                    person.setEmergencyEmail(txtEmergencyMail.getText().toString());
                    person.setEmergencyPhone(txtEmergencyPhone.getText().toString());

                    //Locally stores the emergency number
                    SharedPreferences.Editor mEditor = mPrefs.edit();
                    mEditor.putString(PotostopSession.LOCAL_EMERGENCY_NUMBER_TAG,
                            txtEmergencyPhone.getText().toString());
                    mEditor.commit();

                    mViewModel.updatePerson(person);

                    Toast.makeText(FragmentProfile.this.getActivity(),
                            getString(R.string.profile_save_message), Toast.LENGTH_SHORT).show();
                    mainActivity();
                }
            }
        });
    }

    private boolean isEmailValid(String email) {
        //Check the pattern of the email input
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isPhoneNumberValid(String phone){
        //Check the pattern of the phone input
        String expression = "^(0041|041|\\+41|\\+\\+41|41)?(0|\\(0\\))?([1-9]\\d{1})(\\d{3})(\\d{2})(\\d{2})$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private void mainActivity()
    {
        Intent i = new Intent(this.getActivity(), MainActivity.class);
        startActivity(i);
    }

    private void observeViewModel(){

        mViewModel.getPerson().observe(this, new Observer<PersonEntity>() {
            @Override
            public void onChanged(@Nullable PersonEntity personEntity) {
            person = personEntity;
            spiSex.setSelection(person.getSex());
            txtFullname.setText(person.getFullname());
            txtEmergencyMail.setText(personEntity.getEmergencyEmail());
            txtEmergencyPhone.setText(personEntity.getEmergencyPhone());
            if(person.getBirthDate() != 0){
                birthdate = person.getBirthDate();
                lblDate.setText(PotostopSession.toNiceDateFormat(birthdate));
            }
            }
        });
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
