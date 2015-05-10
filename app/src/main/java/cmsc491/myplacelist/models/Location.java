package cmsc491.myplacelist.models;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

@ParseClassName("Location")
public class Location extends ParseObject{
    private static final String CLASS_NAME = "Location";
    private static final String MPL_ID = "id";
    private static final String LOCATION_NAME = "name";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lng";
    private static final String USER = "user";

    public Location(){ }

    public Location(String name, Double lat, Double lng){
        put(LOCATION_NAME, name);
        put(LATITUDE, lat);
        put(LONGITUDE, lng);
    }

    public Location(int id, String name, Double lat, Double lng){
        put(MPL_ID, id);
        put(LOCATION_NAME, name);
        put(LATITUDE, lat);
        put(LONGITUDE, lng);
    }

    public static void saveLocation(String name, Double lat, Double lng, SaveCallback scb){
        saveLocation(new Location(name, lat, lng), scb);
    }

    public static void saveLocation(Location location, SaveCallback scb){
        location.put(USER, ParseUser.getCurrentUser());
        location.saveInBackground(scb);
    }

    public static void getUserLocations(FindCallback<Location> fcb){
        ParseQuery<Location> query = ParseQuery.getQuery(CLASS_NAME);
        query.whereEqualTo(USER, ParseUser.getCurrentUser());
        query.findInBackground(fcb);
    }

    public static void getLocationById(String id, GetCallback<Location> gcb){
        ParseQuery<Location> query = ParseQuery.getQuery(CLASS_NAME);
        query.getInBackground(id, gcb);
    }

    public long getId(){
        if(getObjectId() != null)
            return getObjectId().hashCode();
        if(getInt(MPL_ID) != 0)
            return getInt(MPL_ID);
        return -1;
    }

    public String getName(){
        return getString(LOCATION_NAME);
    }

    public void setName(String name){
        put(LOCATION_NAME, name);
    }

    public Double getLat(){
        return getDouble(LATITUDE);
    }

    public void setLat(Double lat){
        put(LATITUDE, lat);
    }

    public Double getLng(){
        return getDouble(LONGITUDE);
    }

    public void setLng(Double lng){
        put(LONGITUDE, lng);
    }

    public ParseUser getOwner(){
        return getParseUser(USER);
    }

    public String toString(){
        return getName();
    }

}
