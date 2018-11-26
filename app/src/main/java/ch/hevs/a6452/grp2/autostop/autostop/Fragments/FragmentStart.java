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

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.hevs.a6452.grp2.autostop.autostop.PlateActivity;
import ch.hevs.a6452.grp2.autostop.autostop.R;


public class FragmentStart extends Fragment implements View.OnClickListener {

    public static final String TAG = "FragmentStart";

    private Button buttonStartTrip;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        buttonStartTrip = (Button) view.findViewById(R.id.buttonStartTrip);
        buttonStartTrip.setOnClickListener(this);

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

    private void clickStartTrip()
    {
        Log.i(TAG, "ButtonStartTrip clicked");
        Intent i = new Intent( this.getActivity(), PlateActivity.class );
        startActivity( i );
    }

    @Override
    public void onClick(View view)
    {
        if ( view == buttonStartTrip )
        {
            clickStartTrip();
        }
    }
}
