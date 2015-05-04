package cmsc491.myplacelist;

import android.app.Application;

import com.parse.Parse;

public class MyPlaceListApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "fI2KJnT6uHlVTWbxQ7sBvNDWOVov2UH0AzgTXDQC", "W6xWIwRu8aolaCQl84WRycw3y5dz208OLZp5in2e");
    }
}
