package cmsc491.myplacelist.viewmodels;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cmsc491.myplacelist.R;
import cmsc491.myplacelist.models.Location;

public class LocationDrawer {
    public static final int ADD_ID = 99999;
    public static Location addLocation = new Location(ADD_ID, "Add Location", 0, 0);
    public static ArrayList<Location> locations = getUserLocations();

    public static ArrayList<Location> getUserLocations(){
        // Retrieve user saved locations
        ArrayList<Location> locs = new ArrayList<Location>();
        locs.add(addLocation);
        return locs;
    }

    public static Location getLocation(int index){
        return locations.get(index);
    }

    public static class LAdapter extends ArrayAdapter<Location>{
        private ArrayList<Location> locations;
        public LAdapter(Context context, int resource, ArrayList<Location> locations) {
            super(context, resource, locations);
            this.locations = locations;
        }

        public long getItemId(int position){
            return locations.get(position).id;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.location_drawer_item, parent, false);
            }

            Location i = locations.get(position);
            if(i != null){
                TextView name = (TextView) convertView.findViewById(R.id.drawerItemName);
                name.setText(i.name);
            }

            return convertView;
        }
    }
}
