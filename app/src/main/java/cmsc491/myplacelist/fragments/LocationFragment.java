package cmsc491.myplacelist.fragments;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;

import cmsc491.myplacelist.HomeActivity;
import cmsc491.myplacelist.R;
import cmsc491.myplacelist.fragments.interfaces.MPLMapListener;
import cmsc491.myplacelist.models.Location;

public class LocationFragment extends MPLFragmentBase {
    public static final String LOCATION_ID = "locID";
    private EditText locationName, latInput, lngInput;
    private Button cancelDeleteBtn, saveBtn;
    private View v;
    private ProgressBar progressBar;
    private Location location;
    private MPLGoogleMapFragment mplFrag;
    private boolean locationFromLoad;

    public void checkArguments(){
        Bundle args = getArguments();
        if(args != null && args.containsKey(LOCATION_ID)){
            location = getLocationEditActivity().getLocation();
            latInput.setText(location.getLat().toString());
            lngInput.setText(location.getLng().toString());
            locationName.setText(location.getName());
            locationFromLoad = true;
            cancelDeleteBtn.setText("Delete");
            return;
        }

        locationFromLoad = false;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.location_fragment, container, false);
        mplFrag = new MPLGoogleMapFragment();
        initializeUI();
        checkArguments();

        mplFrag.setMPLMapListener(new LocationFragMPLListener());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO refactor to check for all required inputs
                String name = locationName.getText().toString();
                Double lat = Double.parseDouble(latInput.getText().toString());
                Double lng = Double.parseDouble(lngInput.getText().toString());

                progressBar.setVisibility(View.VISIBLE);
                if(locationFromLoad){
                    location.setName(name);
                    location.setLat(lat);
                    location.setLng(lng);
                    location.saveInBackground(new SaveLocationCallback());
                }else{
                    Location.saveLocation(name, lat, lng, new SaveLocationCallback());
                }
            }
        });

        cancelDeleteBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(!locationFromLoad){
                    ((HomeActivity)getActivity()).initializeActivity();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                location.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if(e == null){
                            Intent intent = new Intent(getActivity(), HomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {

                        }
                    }
                });
            }
        });

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.mplFragment_container, mplFrag).commit();
        return v;
    }

    private void initializeUI(){
        locationName = (EditText) v.findViewById(R.id.locationName);
        latInput = (EditText) v.findViewById(R.id.latInput);
        lngInput = (EditText) v.findViewById(R.id.lngInput);
        saveBtn = (Button) v.findViewById(R.id.saveBtn);
        progressBar = (ProgressBar) v.findViewById(R.id.locationFragProgress);
        cancelDeleteBtn = (Button) v.findViewById(R.id.deleteBtn);
    }

    private class SaveLocationCallback implements SaveCallback {

        @Override
        public void done(ParseException e) {
            progressBar.setVisibility(View.INVISIBLE);
            if(e == null){
                Toast.makeText(getActivity(), "Save Successful", Toast.LENGTH_LONG).show();
                if(!locationFromLoad) {
                    Intent intent = new Intent(getActivity(), HomeActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

            }else {
            }
        }
    }

    private class LocationFragMPLListener implements MPLMapListener {

        @Override
        public void onMarkerClick(Marker marker, boolean isCurrentLocation) {

            if(!isCurrentLocation)
                locationName.setText(marker.getTitle());
            else
                locationName.setText("");
            latInput.setText(String.format("%.10f",marker.getPosition().latitude));
            lngInput.setText(String.format("%.10f",marker.getPosition().longitude));
        }

        @Override
        public boolean onMapReady(GoogleMap map) {
            if(location != null){
                LatLng coordinates = new LatLng(location.getLat(), location.getLng());
                mplFrag.addMarker(location.getName(), coordinates);
                return false;
            }

            return true;
        }
    }
}
