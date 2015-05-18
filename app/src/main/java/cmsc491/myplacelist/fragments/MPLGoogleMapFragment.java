package cmsc491.myplacelist.fragments;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.HashMap;

import cmsc491.myplacelist.R;
import cmsc491.myplacelist.fragments.interfaces.MPLMapListener;
import cmsc491.myplacelist.google.GooglePlaceResponse;
import cmsc491.myplacelist.google.GoogleSearchAO;

public class MPLGoogleMapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap gMap;
    private LocationManager locationManager;
    private Button searchBtn;
    private EditText searchText;
    private MPLMapListener listener;
    private Marker currentPosMarker;
    private LatLng currentPosition;
    private ProgressBar progressBar;
    private FrameLayout scrollBlocker;
    private View.OnTouchListener onTouchListener;
    private LatLng searchPosition;
    private HashMap<Marker, String> placeIdMarkerMap;

    public void setMPLMapListener(MPLMapListener listener){
        this.listener = listener;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        View v = inflater.inflate(R.layout.google_map_fragment, container, false);
        initializeMap();
        searchBtn = (Button) v.findViewById(R.id.searchBtn);
        searchText = (EditText) v.findViewById(R.id.searchText);
        progressBar = (ProgressBar) v.findViewById(R.id.searchProgress);
        scrollBlocker = (FrameLayout) v.findViewById(R.id.scroll_blocker_frame);
        scrollBlocker.setOnTouchListener(onTouchListener);
        placeIdMarkerMap = new HashMap<Marker, String>();


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchText.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                new GoogleSearchRequestTask().execute(query);
            }
        });
        return v;
    }

    private void initializeMap(){
        MapFragment mapFragment = (MapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void setScrollBlocker(View.OnTouchListener onTouchListener){
        this.onTouchListener = onTouchListener;
    }

    public void addMarker(String name, LatLng coordinates){
        gMap.addMarker(new MarkerOptions()
        .position(coordinates)
        .title(name)
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

        gMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
        gMap.moveCamera(CameraUpdateFactory.zoomTo(11));
    }

    public String getPlaceIdByMarker(Marker marker){
        return placeIdMarkerMap.get(marker);
    }

    public void clearMap(){
        gMap.clear();
        // Get last known location from GPS, if null then network provider
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location == null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(location != null) {
            LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
            currentPosMarker = gMap.addMarker(new MarkerOptions()
                    .position(myCoordinates)
                    .title("Current Location"));
            currentPosition = currentPosMarker.getPosition();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setInfoWindowAdapter(new MPLGoogleInfoAdapter());
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                boolean isCurrentLocation = marker.getId().equals(currentPosMarker.getId());
                if(listener != null)
                    listener.onMarkerClick(marker, isCurrentLocation);
                return false;
            }
        });
        // Get last known location from GPS, if null then network provider
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location == null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(location != null) {
            LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
            currentPosMarker = gMap.addMarker(new MarkerOptions()
                    .position(myCoordinates)
                    .title("Current Location"));
            currentPosition = currentPosMarker.getPosition();

        if(listener == null || listener.onMapReady(gMap)){
                gMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates));
                gMap.moveCamera(CameraUpdateFactory.zoomTo(11));
            }
        }
    }

    public void setSearchPosition(LatLng coords){
        searchPosition = coords;
    }

    private class GoogleSearchRequestTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... queries) {
            GoogleSearchAO search = new GoogleSearchAO();
            if(searchPosition == null)
                searchPosition = new LatLng(currentPosition.latitude, currentPosition.longitude);
            String response = search.searchPlaces(queries[0], searchPosition.latitude, searchPosition.longitude);
            return response;
        }

        protected void onPostExecute(String result){
            GooglePlaceResponse response = new Gson().fromJson(result, GooglePlaceResponse.class);
            gMap.clear();
            LatLng currentPosition = currentPosMarker.getPosition();
            // Add current position
            gMap.addMarker(new MarkerOptions()
                    .position(currentPosition)
                    .title(currentPosMarker.getTitle()));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(currentPosMarker.getPosition());
            placeIdMarkerMap.clear();

            // Add all places returned from response
            for(int i = 0; i < response.results.size(); i++){
                GooglePlaceResponse.Place place = response.results.get(i);
                LatLng coordinates = new LatLng(place.geometry.location.lat, place.geometry.location.lng);
                builder.include(coordinates);
                MarkerOptions markerOptions = buildGooglePlaceMarker(place, coordinates);

                Marker marker = gMap.addMarker(markerOptions);
                placeIdMarkerMap.put(marker, place.place_id);
            }

            progressBar.setVisibility(View.INVISIBLE);

            if(listener != null)
                listener.afterSearch();

            // Zoom to fit all markers
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), 100);
            gMap.animateCamera(cu);
        }
        // Convenience method to help build google place marker
        private MarkerOptions buildGooglePlaceMarker(GooglePlaceResponse.Place place, LatLng coordinates){
            MarkerOptions marker = new MarkerOptions().position(coordinates);
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            marker.title(place.name);
            marker.snippet(place.formatted_address);
            return marker;
        }
    }

    private class MPLGoogleInfoAdapter implements GoogleMap.InfoWindowAdapter {
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.info_window_layout, null);
            TextView title = (TextView) v.findViewById(R.id.infoWindowTitle);
            TextView lineOne = (TextView) v.findViewById(R.id.infoLine1);
            title.setText(marker.getTitle());

            // Special case for current location
            if (!marker.getId().equals(currentPosMarker.getId()))
                lineOne.setText(marker.getSnippet());

            return v;
        }
    }
}
