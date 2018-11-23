package ch.hevs.a6452.grp2.autostop.autostop;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;

import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


import ch.hevs.a6452.grp2.autostop.autostop.Adapter.PlaceAutocompleteAdapter;

public class LocationActivity extends FragmentActivity{

    public static final String TAG = "LocationActivity";

    protected GeoDataClient  mGeoDataClient;

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;

    private static final LatLngBounds BOUNDS_VALAIS = new LatLngBounds(
            new LatLng(45.803399, 6.768166), new LatLng(46.534593, 8.36554000));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Construct a GeoDataClient for the Google Places API for Android.
        mGeoDataClient = Places.getGeoDataClient(this);

        setContentView(R.layout.activity_location);

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);


        // Set up the adapter that will retrieve suggestions from the Places Geo Data Client.
        mAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, BOUNDS_VALAIS, null);
        mAutocompleteView.setAdapter(mAdapter);

        // Set up the 'clear text' button that clears the text in the autocomplete view
        Button okButton = (Button) findViewById(R.id.button_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "okButton clicked");

                if(mAutocompleteView.getText().toString()==""){
                    return;
                }

                /* TODO Implement the activity validateTrip

                Intent intent = new Intent(this, LocationActivity.class);
                startActivity(intent);
                */
            }
        });
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence destination = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + destination + " place id : " + placeId);

            Toast.makeText(getApplicationContext(), "Clicked: " + destination +" Id: "+ placeId,
                    Toast.LENGTH_SHORT).show();
        }
    };



}