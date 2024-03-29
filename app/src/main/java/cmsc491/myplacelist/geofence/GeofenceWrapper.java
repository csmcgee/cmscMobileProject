package cmsc491.myplacelist.geofence;

import android.content.Context;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.List;

import cmsc491.myplacelist.google.GooglePlaceResponse;
import cmsc491.myplacelist.models.Location;


/**
 * Created by IanKop1 on 5/20/2015.
 */
public class GeofenceWrapper {


    private static GeofenceControlPanel controlPanel;


    public static void initialize(Context context){
        controlPanel = new GeofenceControlPanel(context);
    }

    public static void connectClient(){
        controlPanel.connectApiClient();
    }

    public static void disconnectClient(){
        controlPanel.disconnectApiClient();
    }

    public static void addFences(Context context, List<Location> places, float distance){
        if(!places.isEmpty())
            controlPanel.addGeofencesMarkerChosen(context, places, distance);
    }

    public static void clearFences(){
        controlPanel.clearFences();
    }



}
