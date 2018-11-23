package ch.hevs.a6452.grp2.autostop.autostop.Fragments;


import android.content.Intent;
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
import ch.hevs.a6452.grp2.autostop.autostop.R;


public class FragmentStart extends Fragment {

    public static final String TAG = "FragmentStart";

    Button buttonStartTrip;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        buttonStartTrip = (Button) view.findViewById(R.id.buttonStartTrip);

        buttonStartTrip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i(TAG, "ButtonStartTrip clicked");

                /* TODO Implement the activity select Plate

                Intent intent = new Intent(this, LocationActivity.class);
                startActivity(intent);
                */
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
