package cmsc491.myplacelist;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.gms.maps.model.Marker;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

import cmsc491.myplacelist.fragments.MPLGoogleMapFragment;
import cmsc491.myplacelist.fragments.interfaces.MPLMapListener;
import cmsc491.myplacelist.models.Location;

public class PlaceCUDActivity extends ActionBarActivity{
    private MPLGoogleMapFragment mplFragment;
    private Spinner mSpinner;
    private ArrayAdapter<Location> spinLocationAdapter;
    private Activity self;
    private EditText mPlaceName, mPlaceLat, mPlaceLng, mPlaceNotes;

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
                setProgressBarIndeterminateVisibility(false);
                if(e == null){
                    spinLocationAdapter = new ArrayAdapter<Location>(self,
                            android.R.layout.simple_dropdown_item_1line, locations);
                    mSpinner.setAdapter(spinLocationAdapter);
                    spinLocationAdapter.notifyDataSetChanged();
                }
            }
        });

        mplFragment = new MPLGoogleMapFragment();
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.placeMPLMapContainer);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.placeMPLMapContainer, mplFragment).commit();

        mplFragment.setMPLMapListener(new PlaceCUD_MPL_Listener());

    }

    private void initializeUI(){
        mSpinner = (Spinner) findViewById(R.id.locationPlaceSpinner);
        mPlaceName = (EditText) findViewById(R.id.placeNameField);
        mPlaceLat = (EditText) findViewById(R.id.placeLatField);
        mPlaceLng = (EditText) findViewById(R.id.placeLngField);
        mPlaceNotes = (EditText) findViewById(R.id.placeNotesField);
    }

    private class PlaceCUD_MPL_Listener implements MPLMapListener {

        @Override
        public void onMarkerClick(Marker marker, boolean isCurrentLocation) {
            mPlaceName.setText(marker.getTitle());
            mPlaceLat.setText(String.format("%.8f", marker.getPosition().latitude));
            mPlaceLng.setText(String.format("%.8f", marker.getPosition().longitude));
        }

        @Override
        public boolean onMapReady() {
            return true;
        }
    }
}
