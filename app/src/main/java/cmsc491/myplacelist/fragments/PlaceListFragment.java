package cmsc491.myplacelist.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cmsc491.myplacelist.R;

public class PlaceListFragment extends Fragment {
    private View v;
    public static final String LOC_ID_ARG = "LocID";
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_place_list, container, false);
        Bundle args = getArguments();
        if(args != null && args.containsKey(LOC_ID_ARG)){
            String id = args.getString(LOC_ID_ARG);

        }
        return v;
    }
}
