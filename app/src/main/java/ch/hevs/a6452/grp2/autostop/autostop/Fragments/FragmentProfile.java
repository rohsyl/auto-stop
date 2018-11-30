package ch.hevs.a6452.grp2.autostop.autostop.Fragments;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
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
import ch.hevs.a6452.grp2.autostop.autostop.R;
import ch.hevs.a6452.grp2.autostop.autostop.Utils.FirebaseConverter;
import ch.hevs.a6452.grp2.autostop.autostop.ViewModels.ProfileViewModel;

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


        popolateTitleSpinner();


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

                        lblDate.setText(FirebaseConverter.toNiceDateFormat(c.getTimeInMillis()));
                    }
                });
                dialogFragmentDatePicker.show(getFragmentManager(), "datepicker");
            }
        });
    }

    private void popolateTitleSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.profile_sex_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spiSex.setAdapter(adapter);
    }

}