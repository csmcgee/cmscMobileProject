package cmsc491.myplacelist;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.List;

import cmsc491.myplacelist.fragments.MPLGoogleMapFragment;
import cmsc491.myplacelist.fragments.MPLGooglePlaceDetailsFragment;
import cmsc491.myplacelist.fragments.interfaces.MPLMapListener;
import cmsc491.myplacelist.models.Location;
import cmsc491.myplacelist.models.Place;

/**
 * Place create, update, and delete activity.
 */
public class PlaceCUDActivity extends ActionBarActivity{
    private MPLGoogleMapFragment mplFragment;
    private Spinner mSpinner;
    private ArrayAdapter<Location> spinLocationAdapter;
    private Activity self;
    private EditText mPlaceName, mPlaceLat, mPlaceLng, mPlaceNotes;
    private CheckBox mPlaceDetailsCheckbox;
    private Button mNegBtn, mPosBtn;
    private ScrollView scrollView;
    public static final String PLACE_ID = "placeID";
    private boolean edit_state;
    private Intent mIntent;
    private Place selectedPlace;
    private RelativeLayout googlePlaceDetailsContainer;
    private MPLGooglePlaceDetailsFragment mplGPDF;
    private String selectedMarkerPlaceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_add_edit_activity);

        self = this;
        initializeUI();

        setProgressBarIndeterminateVisibility(true);
        Location.getUserLocations(new FindCallback<Location>() {
            @Override
            public void done(List<Location> locations, ParseException e) {
                if(e == null){
                    spinLocationAdapter = new ArrayAdapter<Location>(self,
                            android.R.layout.simple_dropdown_item_1line, locations);
                    mSpinner.setAdapter(spinLocationAdapter);
                    spinLocationAdapter.notifyDataSetChanged();

                    Location firstLocation = locations.get(0);

                    mplFragment.setSearchPosition(new LatLng(firstLocation.getLat(), firstLocation.getLng()));

                    if(mIntent.hasExtra(PLACE_ID)){
                        setUpPlaceValues();
                        return;
                    }
                    setProgressBarIndeterminateVisibility(false);
                }
            }
        });

        mplFragment = new MPLGoogleMapFragment();
        mplFragment.setScrollBlocker(new ScrollFragContainerListener());

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.placeMPLMapContainer);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.placeMPLMapContainer, mplFragment).commit();

        mplFragment.setMPLMapListener(new PlaceCUD_MPL_Listener());
    }

    private void initializeUI(){
        scrollView = (ScrollView) findViewById(R.id.scroll_view_place);
        mSpinner = (Spinner) findViewById(R.id.locationPlaceSpinner);
        mPlaceName = (EditText) findViewById(R.id.placeNameField);
        mPlaceLat = (EditText) findViewById(R.id.placeLatField);
        mPlaceLng = (EditText) findViewById(R.id.placeLngField);
        mPlaceNotes = (EditText) findViewById(R.id.placeNotesField);
        mNegBtn = (Button) findViewById(R.id.negPlaceBtn);
        mPosBtn = (Button) findViewById(R.id.posPlaceBtn);
        mPlaceDetailsCheckbox = (CheckBox) findViewById(R.id.placeIncludeDetails);
        googlePlaceDetailsContainer = (RelativeLayout) findViewById(R.id.googlePlaceDetailsEditContainer);
        mIntent = getIntent();
        mPosBtn.setOnClickListener(new SavePlaceEvent());
        mNegBtn.setOnClickListener(new NegButtonCallback());
        mSpinner.setOnItemSelectedListener(new LocationSpinnerSelectListener());
        mPlaceDetailsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                if(!isChecked){
                    if(mplGPDF != null)
                        transaction.remove(mplGPDF).commit();
                } else {
                    mplGPDF = new MPLGooglePlaceDetailsFragment();
                    mplGPDF.setPlaceId(selectedMarkerPlaceId);
                    transaction.add(R.id.googlePlaceDetailsEditContainer,mplGPDF).commit();
                }
            }
        });
        edit_state = false;
    }

    private void setUpPlaceValues(){
        setProgressBarIndeterminateVisibility(true);
        Place.getPlaceById(mIntent.getStringExtra(PLACE_ID), new GetCallback<Place>() {
            @Override
            public void done(Place place, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if(e == null){
                    edit_state = true;
                    mPlaceName.setText(place.getName());
                    mPlaceLat.setText(place.getLat().toString());
                    mPlaceLng.setText(place.getLng().toString());
                    mPlaceNotes.setText(place.getNotes());
                    int position = spinLocationAdapter.getPosition(place.getLocation());
                    mSpinner.setSelection(position);
                    mNegBtn.setText("Delete");
                    selectedPlace = place;

                    if(selectedPlace.getGooglePlaceID() != null && !selectedPlace.getGooglePlaceID().isEmpty()){
                        selectedMarkerPlaceId = selectedPlace.getGooglePlaceID();
                        mPlaceDetailsCheckbox.setChecked(true);
                    }


                    LatLng coordinates = new LatLng(selectedPlace.getLat(), selectedPlace.getLng());
                    mplFragment.addMarker(selectedPlace.getName(), coordinates);
                }
            }
        });
    }

    private class PlaceCUD_MPL_Listener implements MPLMapListener {

        @Override
        public void onMarkerClick(Marker marker, boolean isCurrentLocation) {
            mPlaceName.setText(marker.getTitle());
            mPlaceLat.setText(String.format("%.8f", marker.getPosition().latitude));
            mPlaceLng.setText(String.format("%.8f", marker.getPosition().longitude));
            selectedMarkerPlaceId = mplFragment.getPlaceIdByMarker(marker);
            mPlaceDetailsCheckbox.setChecked(false);
        }

        @Override
        public boolean onMapReady(GoogleMap map) {
            return true;
        }

        @Override
        public void afterSearch() {
            Location selectedLocation = (Location) mSpinner.getSelectedItem();
            LatLng coordinates = new LatLng(selectedLocation.getLat(), selectedLocation.getLng());
            mplFragment.addMarker(selectedLocation.getName(), coordinates);
        }
    }

    private class SavePlaceEvent implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Place place = buildPlaceFromInput();
            setProgressBarIndeterminateVisibility(true);
            if(edit_state){
                selectedPlace.setName(mPlaceName.getText().toString());
                selectedPlace.setLat(Double.parseDouble(mPlaceLat.getText().toString()));
                selectedPlace.setLng(Double.parseDouble(mPlaceLng.getText().toString()));
                selectedPlace.setNotes(mPlaceNotes.getText().toString());
                selectedPlace.setLocation(place.getLocation());
                selectedPlace.setGooglePlaceId(place.getGooglePlaceID());

                selectedPlace.saveInBackground(new SavePlaceCallback());
            }
            else
                Place.savePlace(place, new SavePlaceCallback());
        }
    }

    private class SavePlaceCallback implements SaveCallback{

        @Override
        public void done(ParseException e) {
            setProgressBarIndeterminateVisibility(false);
            if(e == null){
                Toast.makeText(getApplicationContext(), "Save successful", Toast.LENGTH_LONG).show();
                if(!edit_state){
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        }
    }

    private class NegButtonCallback implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            final Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            if(!edit_state){
                startActivity(intent);
                return;
            }

            selectedPlace.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Toast.makeText(getApplicationContext(), "Deletion Successful.", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private class LocationSpinnerSelectListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Location location = spinLocationAdapter.getItem(position);
            LatLng locationCoord = new LatLng(location.getLat(), location.getLng());
            mplFragment.clearMap();
            mplFragment.setSearchPosition(locationCoord);
            mplFragment.addMarker(location.getName(), locationCoord);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class ScrollFragContainerListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch(action){
                case MotionEvent.ACTION_DOWN:
                    scrollView.requestDisallowInterceptTouchEvent(true);
                    return false;
                case MotionEvent.ACTION_UP:
                    scrollView.requestDisallowInterceptTouchEvent(true);
                    return false;
                case MotionEvent.ACTION_MOVE:
                    scrollView.requestDisallowInterceptTouchEvent(true);
                    return false;
                default:
                    return true;
            }
        }
    }

    private Place buildPlaceFromInput(){
        Location location = (Location)mSpinner.getSelectedItem();
        String name = mPlaceName.getText().toString();
        Double lat = Double.parseDouble(mPlaceLat.getText().toString());
        Double lng = Double.parseDouble(mPlaceLng.getText().toString());
        String notes = mPlaceNotes.getText().toString();
        Place place = new Place(location, name, lat, lng, notes);

        if(mPlaceDetailsCheckbox.isChecked())
            place.setGooglePlaceId(selectedMarkerPlaceId);
        return place;
    }




}
