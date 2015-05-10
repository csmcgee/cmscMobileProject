package cmsc491.myplacelist;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import cmsc491.myplacelist.models.Location;
import cmsc491.myplacelist.models.Place;

public class MyPlaceListApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Location.class);
        ParseObject.registerSubclass(Place.class);
        Parse.initialize(this, "fI2KJnT6uHlVTWbxQ7sBvNDWOVov2UH0AzgTXDQC", "W6xWIwRu8aolaCQl84WRycw3y5dz208OLZp5in2e");
    }
}
