package ch.hevs.a6452.grp2.autostop.autostop.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ch.hevs.a6452.grp2.autostop.autostop.R;

public class FragmentRate extends Fragment {
    public static final String TAG = "FragmentRate";

    TextView dateTrip;
    TextView itineraryTrip;
    Button button_ok;
    Button buttonRepport;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_rate, container, false);


        //Initialize elements
        dateTrip = (TextView) view.findViewById(R.id.dateTrip);
        itineraryTrip = (TextView) view.findViewById(R.id.itineraryTrip);
        button_ok = (Button) view.findViewById(R.id.button_ok);
        buttonRepport = (Button) view.findViewById(R.id.buttonRepport);

        //Set the date of the trip to the view
        dateTrip.setText("Monday 1st september 2018");

        //Set the itinerary of the trip to the view
        itineraryTrip.setText("From Sierre to Val d'anniviers");

        //Action listener for OK
        button_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i(TAG, "Button_ok clicked");

                // TODO Implement trip validate succes

            }
        });

        //Action listener for REPORT
        buttonRepport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i(TAG, "ButtonRepport clicked");

                // TODO Implement link to repport field

            }
        });


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }
}
