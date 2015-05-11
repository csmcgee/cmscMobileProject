package cmsc491.myplacelist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;

import cmsc491.myplacelist.models.Place;

public class PlaceViewActivity extends ActionBarActivity implements OnMapReadyCallback {
    private GoogleMap gMap;

    private TextView mPlaceName, mPlaceNotes;
    private Button editBtn;
    private Place activityPlace;
    private Intent mIntent;
    public static final String PLACE_ID = "placeID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_view);
        setProgressBarIndeterminateVisibility(true);
        intializeMap();
        initializeUI();

    }

    private void intializeMap(){
        if(gMap == null){
            MapFragment mapFrag = ((MapFragment) getFragmentManager().findFragmentById(R.id.placeGoogleMapFragment));
            mapFrag.getMapAsync(this);
        }
    }

    private void initializeUI(){
        mIntent = getIntent();
        mPlaceName = (TextView) findViewById(R.id.placeNameView);
        editBtn = (Button) findViewById(R.id.placeViewEditBtn);
        mPlaceNotes = (TextView) findViewById(R.id.placeNotesView);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlaceCUDActivity.class);
                intent.putExtra(PlaceCUDActivity.PLACE_ID, activityPlace.getObjectId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        Place.getPlaceById(mIntent.getStringExtra(PLACE_ID), new GetCallback<Place>() {
            @Override
            public void done(Place place, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if(e == null){
                    activityPlace = place;
                    mPlaceName.setText(place.getName());
                    mPlaceNotes.setText(place.getNotes());
                    setMapMarker(activityPlace.getLat(), activityPlace.getLng());
                }
            }
        });
    }

    private void setMapMarker(Double lat, Double lng){
        LatLng coordinates = new LatLng(lat, lng);
        gMap.addMarker(new MarkerOptions()
                .position(coordinates)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

        gMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
        gMap.moveCamera(CameraUpdateFactory.zoomTo(13));
    }
}
