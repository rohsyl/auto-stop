package ch.hevs.a6452.grp2.autostop.autostop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import butterknife.BindView;
import butterknife.ButterKnife;
import ch.hevs.a6452.grp2.autostop.autostop.adapter.PlaceAutocompleteAdapter;
import ch.hevs.a6452.grp2.autostop.autostop.entities.PositionEntity;

public class LocationActivity extends FragmentActivity {

    public static final String TAG = "LocationActivity";

    public static final String EXTRA_KEY_LOCATION = "location";

    //Geo client for location response
    protected GeoDataClient mGeoDataClient;

    //Adapter for location proposition
    private PlaceAutocompleteAdapter mAdapter;

    //Autocomplete location field
    @BindView(R.id.autocomplete_places)
    protected AutoCompleteTextView mAutocompleteView;

    @BindView(R.id.fab_validate_location)
    protected FloatingActionButton btnValidateLocation;
    private PositionEntity location;

    //Limit bound for location propositon
    private static final LatLngBounds BOUNDS_VALAIS = new LatLngBounds(
            new LatLng(45.803399, 6.768166), new LatLng(46.534593, 8.36554000));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Construct a GeoDataClient for the Google Places API for Android.
        mGeoDataClient = Places.getGeoDataClient(this);

        setContentView(R.layout.activity_location);

        ButterKnife.bind(this);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);


        // Set up the adapter that will retrieve suggestions from the Places Geo Data Client.
        mAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, BOUNDS_VALAIS, null);
        mAutocompleteView.setAdapter(mAdapter);

        btnValidateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "buttonValidate clicked");

                //Do nothing if no location written
                if (mAutocompleteView.getText().toString() == "") {
                    return;
                }
                //Give destination to the next activity
                returnLocationResult(location);
            }
        });
    }


    //When you select a location from propositions
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

            // Getting infos about the place
            Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(item.getPlaceId());
            placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback);
        }
    };


    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsCallback
            = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(Task<PlaceBufferResponse> task) {
            try {
                PlaceBufferResponse places = task.getResult();

                // Get the Place object from the buffer.
                Place place = places.get(0);
                location = new PositionEntity();
                location.setName(place.getName().toString());
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);
                // The timestamp is not set since we don't know exactly when the passenger will be there

                places.release();
            } catch (RuntimeRemoteException e) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete.", e);
                return;
            }
        }
    };

    //Give destination to the next activity and start it
    private void returnLocationResult(PositionEntity location) {
        Intent i = new Intent();
        i.putExtra(EXTRA_KEY_LOCATION, location);
        i.putExtra("uidPlate", (getIntent().getStringExtra("uidPlate")));
        setResult(Activity.RESULT_OK, i);
        finish();
    }
}