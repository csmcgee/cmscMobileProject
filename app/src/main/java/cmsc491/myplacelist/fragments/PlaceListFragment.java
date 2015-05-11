package cmsc491.myplacelist.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import cmsc491.myplacelist.PlaceCUDActivity;
import cmsc491.myplacelist.PlaceViewActivity;
import cmsc491.myplacelist.R;
import cmsc491.myplacelist.models.Location;
import cmsc491.myplacelist.models.Place;

public class PlaceListFragment extends Fragment {
    private View v;
    public static final String LOC_ID_ARG = "LocID";
    private ListView placesList;
    private Location selectedLocation;
    private ArrayAdapter locationPlacesAdapter;
    private TextView noPlacesLbl;
    private ProgressBar progressBar;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_place_list, container, false);
        Bundle args = getArguments();
        placesList = (ListView) v.findViewById(R.id.locationPlacesList);

        placesList.setOnItemClickListener(new PlaceClickListener());

        noPlacesLbl = (TextView) v.findViewById(R.id.noPlacesLabel);
        progressBar = (ProgressBar) v.findViewById(R.id.locationPlacesListProgress);
        locationPlacesAdapter = new ArrayAdapter<Place>(v.getContext(), android.R.layout.simple_list_item_1, new ArrayList<Place>());
        placesList.setAdapter(locationPlacesAdapter);

        if(args != null && args.containsKey(LOC_ID_ARG)){
            String id = args.getString(LOC_ID_ARG);
            progressBar.setVisibility(View.VISIBLE);
            Location.getLocationById(id, new GetLocationCallback());
        }
        return v;
    }

    private class GetLocationCallback implements GetCallback<Location> {

        @Override
        public void done(Location location, ParseException e) {
            if(e == null){
                selectedLocation = location;
                Place.getPlacesByLocation(selectedLocation, new RetrievePlacesCallback());
            }else{
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private class RetrievePlacesCallback implements FindCallback<Place> {

        @Override
        public void done(List<Place> places, ParseException e) {
            progressBar.setVisibility(View.INVISIBLE);
            if (e == null) {
                locationPlacesAdapter.clear();
                locationPlacesAdapter.addAll(places);
                locationPlacesAdapter.notifyDataSetChanged();
                toggleResultsMessage(places);

            }
        }

        private void toggleResultsMessage(List<Place> places) {
            if (places.size() == 0){
                noPlacesLbl.setVisibility(View.VISIBLE);
            }else
                noPlacesLbl.setVisibility(View.INVISIBLE);
        }
    }

    private class PlaceClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), PlaceViewActivity.class);
            Place place = (Place) locationPlacesAdapter.getItem(position);
            intent.putExtra(PlaceCUDActivity.PLACE_ID, place.getObjectId());
            startActivity(intent);
        }
    }

}
