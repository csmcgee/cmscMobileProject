package cmsc491.myplacelist.fragments;

import android.app.Fragment;

import cmsc491.myplacelist.HomeActivity;
import cmsc491.myplacelist.LocationEditActivity;

public class MPLFragmentBase extends Fragment {
    public LocationEditActivity getLocationEditActivity(){
        return (LocationEditActivity)getActivity();
    }
}
