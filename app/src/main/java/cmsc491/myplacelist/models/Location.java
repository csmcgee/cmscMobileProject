package cmsc491.myplacelist.models;


public class Location {
    public int id;
    public String name;
    public double lat, lng;

    public Location(int id, String name, double lat, double lng){
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }
}
