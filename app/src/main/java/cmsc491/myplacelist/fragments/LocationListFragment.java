package cmsc491.myplacelist.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import cmsc491.myplacelist.LocationEditActivity;
import cmsc491.myplacelist.R;
import cmsc491.myplacelist.models.Location;
import cmsc491.myplacelist.viewmodels.LocationDrawer;

public class LocationListFragment extends Fragment {
    private View v;
    private LocationDrawer.LAdapter lAdapter;
    private ListView listView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.location_list_fragment, container, false);
        lAdapter = new LocationDrawer.LAdapter(v.getContext(), R.layout.location_drawer_item, new ArrayList<Location>());
        listView = (ListView) v.findViewById(R.id.locationList);
        listView.setAdapter(lAdapter);

        Location.getUserLocations(new FindCallback<Location>() {
            @Override
            public void done(List<Location> locations, ParseException e) {
                if(e == null){
                    lAdapter.clear();
                    lAdapter.addAll(locations);
                    lAdapter.notifyDataSetChanged();
                }else {

                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), LocationEditActivity.class);
            intent.putExtra(LocationFragment.LOCATION_ID, lAdapter.getItem(position).getObjectId());
            startActivity(intent);
            }
        });

        return v;
    }
}
