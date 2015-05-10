package cmsc491.myplacelist;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;

import com.parse.GetCallback;
import com.parse.ParseException;

import cmsc491.myplacelist.fragments.LocationFragment;
import cmsc491.myplacelist.models.Location;

public class LocationEditActivity extends ActionBarActivity {
    private Location editLocation;
    private LocationFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_settings_activity);

        fragment = new LocationFragment();
        Intent intent = getIntent();
        setProgressBarIndeterminateVisibility(true);
        if (intent != null && intent.getStringExtra(LocationFragment.LOCATION_ID) != null) {
            final String id = intent.getStringExtra(LocationFragment.LOCATION_ID);
            Location.getLocationById(id, new GetCallback<Location>() {
                @Override
                public void done(Location location, ParseException e) {
                    setProgressBarIndeterminateVisibility(false);
                    if (e == null) {
                        editLocation = location;
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        Bundle args = new Bundle();
                        args.putString(LocationFragment.LOCATION_ID, id);
                        fragment.setArguments(args);
                        transaction.add(R.id.settingsContainer, fragment);
                        transaction.commit();
                    }
                }
            });
        }
    }

    public Location getLocation(){
        return editLocation;
    }
}
