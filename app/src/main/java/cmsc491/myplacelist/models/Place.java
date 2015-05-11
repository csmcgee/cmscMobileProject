package cmsc491.myplacelist.models;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

@ParseClassName("Place")
public class Place extends ParseObject {
    public static final String CLASS_NAME = "Place";
    public static final String LOCATION = "location";
    public static final String PLACE_NAME = "name";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String NOTES = "notes";

    public Place(){}

    public Place(Location location, String name, Double lat, Double lng, String notes){
        put(LOCATION, location);
        put(PLACE_NAME, name);
        put(LATITUDE, lat);
        put(LONGITUDE, lng);
        put(NOTES, notes);
    }

    public static void savePlace(Place place, SaveCallback scb){
        place.saveInBackground(scb);
    }

    public static void getPlacesByLocation(Location location, FindCallback<Place> fcb){
        ParseQuery<Place> query = ParseQuery.getQuery(CLASS_NAME);
        query.whereEqualTo(LOCATION, location);
        query.findInBackground(fcb);
    }

    public static void getPlaceById(String id, GetCallback<Place> gcb){
        ParseQuery<Place> query = ParseQuery.getQuery(CLASS_NAME);
        query.getInBackground(id, gcb);
    }

    public void setLocation(Location location){
        put(LOCATION, location);
    }

    public Location getLocation(){
        return (Location) getParseObject(LOCATION);
    }

    public String getName(){
        return getString(PLACE_NAME);
    }

    public void setName(String name){
        put(PLACE_NAME, name);
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

    public String getNotes(){
        return getString(NOTES);
    }

    public void setNotes(String notes){
        put(NOTES, notes);
    }

    public String toString(){
        return getString(PLACE_NAME);
    }


}
