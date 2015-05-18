package cmsc491.myplacelist.viewmodels;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import java.util.ArrayList;

import cmsc491.myplacelist.R;
import cmsc491.myplacelist.models.Location;

public class LocationDrawer {
    public static final int ADD_ID = 99999;
    public static final int SETTINGS_ID = 98989;
    public static Location addLocation = new Location(ADD_ID, "Add Location", new Double(0), new Double(0));
    public static Location settingsLocation = new Location(SETTINGS_ID, "Settings", new Double(0), new Double(0));
    public static ArrayList<Location> locations;

    public static class LAdapter extends ArrayAdapter<Location> {
        private ArrayList<Location> locations;
        public LAdapter(Context context, int resource, ArrayList<Location> locations) {
            super(context, resource, locations);
            this.locations = locations;
        }

        public long getItemId(int position){
            return locations.get(position).getId();
        }

        public View getView(int position, View convertView, ViewGroup parent){
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.location_drawer_item, parent, false);
            }

            Location i = locations.get(position);
            if(i != null){
                TextView name = (TextView) convertView.findViewById(R.id.drawerItemName);
                ImageView imageView = (ImageView) convertView.findViewById(R.id.itemIcon);
                TextDrawable circle = TextDrawable.builder()
                        .buildRound(i.getName().substring(0,1), i.getColor());
                imageView.setImageDrawable(circle);
                name.setText(i.getName());
            }

            return convertView;
        }
    }
}
