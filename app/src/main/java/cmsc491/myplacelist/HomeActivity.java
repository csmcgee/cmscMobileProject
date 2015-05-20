package cmsc491.myplacelist;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.location.Geofence;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import cmsc491.myplacelist.fragments.LocationFragment;
import cmsc491.myplacelist.fragments.LocationListFragment;
import cmsc491.myplacelist.fragments.PlaceListFragment;
import cmsc491.myplacelist.geofence.GeofenceWrapper;
import cmsc491.myplacelist.models.Location;
import cmsc491.myplacelist.viewmodels.LocationDrawer;
import cmsc491.myplacelist.domain.MPLConsts;


public class HomeActivity extends ActionBarActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    public static LocationDrawer.LAdapter lAdapter;
    private long selectedId = -1;
    private ArrayList<Geofence> mGeofenceList;
    //private GeofenceControlPanel geoPanel;
    private Context currentContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if(ParseUser.getCurrentUser() == null){
            navigateToLogin();
            return;
        }




        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        lAdapter = new LocationDrawer.LAdapter(this, R.layout.location_drawer_item, new ArrayList<Location>());
        mDrawerList.setAdapter(lAdapter);
        mDrawerList.setOnItemClickListener(new LocationDIClickListener());
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //initialize geofence list
        mGeofenceList = new ArrayList<Geofence>();

        // Create new Geofences Control Panel
        //geoPanel = new GeofenceControlPanel(this);
        GeofenceWrapper.initialize(this);


        initializeActivity();
    }

    public void initializeActivity(){
        selectedId = -1; // for add location bug
        setProgressBarIndeterminateVisibility(true);
        currentContext = this;
        Location.getUserLocations(new FindCallback<Location>() {
            @Override
            public void done(final List<Location> results, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if(e != null)
                    return;

                GeofenceWrapper.addFence(getApplicationContext(), results, MPLConsts.BENCHMARK_DISTANCE);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lAdapter.clear();
                        lAdapter.addAll(results);
                        lAdapter.add(LocationDrawer.addLocation);
                        lAdapter.add(LocationDrawer.settingsLocation);
                        lAdapter.notifyDataSetChanged();

                        if(setLocationFragment()){
                            return;
                        }

                        Fragment fragment;

                        if(results.size() == 0){
                            fragment = new LocationFragment();
                            getSupportActionBar().setTitle("Add Location");
                        } else {
                            Bundle args = new Bundle();
                            fragment = new PlaceListFragment();
                            args.putString(PlaceListFragment.LOC_ID_ARG, lAdapter.getItem(0).getObjectId());
                            fragment.setArguments(args);
                            getSupportActionBar().setTitle(String.format("%s Places", lAdapter.getItem(0).getName()));
                        }
                        changeFragments(fragment);
                    }
                });
            }
        });
    }



//    @Override
//    protected void onResume() {
//        super.onResume();
//        Intent intent = getIntent();
//        String locationID = intent.getStringExtra(PPConsts.LOC_NAME);
//        setLocationFragment(locationID);
//    }

    protected void onStart(){
        super.onStart();
        GeofenceWrapper.connectClient();
    }

    @Override
    protected void onStop(){
        super.onStop();
        GeofenceWrapper.disconnectClient();
    }

    public Location getLocationFromDrawer(int position){
        return lAdapter.getItem(position);
    }

    private void setupDrawer(){
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void navigateToLogin(){
        Intent intent = new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        switch (id){
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();
                break;
            case R.id.action_add:
                Intent intent = new Intent(getApplicationContext(), PlaceCUDActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        if(mDrawerToggle != null)
            mDrawerToggle.syncState();
    }

    private class LocationDIClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(id == selectedId){
                mDrawerLayout.closeDrawer(mDrawerList);
                return;
            }
            selectedId = id;
            mDrawerList.setItemChecked(position, true);
            Fragment fragment = null;
            if(id == LocationDrawer.ADD_ID){
                fragment = new LocationFragment();
                getSupportActionBar().setTitle("Add Location");
            } else if(id == LocationDrawer.SETTINGS_ID){
                fragment = new LocationListFragment();
                getSupportActionBar().setTitle("Locations");
            } else{
                Bundle args = new Bundle();
                fragment = new PlaceListFragment();
                args.putString(PlaceListFragment.LOC_ID_ARG, lAdapter.getItem(position).getObjectId());
                fragment.setArguments(args);
                getSupportActionBar().setTitle(String.format("%s Places", lAdapter.getItem(position).getName()));
            }
            changeFragments(fragment);

        }
    }

    public boolean setLocationFragment(){
        Intent intent = getIntent();
        String locationID = intent.getStringExtra(MPLConsts.LOC_ID);
        String name = intent.getStringExtra(MPLConsts.LOC_NAME);

        if(locationID == null)
            return false;

        setLocationFragment(locationID, name);
        return true;

    }

    private void setLocationFragment(String objectID, String name){
        Bundle args = new Bundle();
        Fragment fragment = new PlaceListFragment();
        args.putString(PlaceListFragment.LOC_ID_ARG, objectID);
        fragment.setArguments(args);
        getSupportActionBar().setTitle(String.format("%s Places", name));
        changeFragments(fragment);
    }

    private void changeFragments(final Fragment fragment){
        mDrawerLayout.closeDrawer(mDrawerList);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();
            }
        }, 250);
    }
}
