package cmsc491.myplacelist.fragments.interfaces;

import com.google.android.gms.maps.model.Marker;

public interface MPLMapListener {
    public void onMarkerClick(Marker marker, boolean isCurrentLocation);

    /**
     * Return false if you don't want default steps to occur.
     */
    public boolean onMapReady();
}
