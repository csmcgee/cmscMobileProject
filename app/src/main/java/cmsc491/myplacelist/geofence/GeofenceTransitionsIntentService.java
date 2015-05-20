package cmsc491.myplacelist.geofence;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import cmsc491.myplacelist.HomeActivity;
import cmsc491.myplacelist.R;
import cmsc491.myplacelist.domain.MPLConsts;

public class GeofenceTransitionsIntentService extends IntentService {

    public String name, id;
//    public double lat, lng;
    protected static final String TAG = "geofence-transitions-service";

    public GeofenceTransitionsIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            Log.e("Geofence",
                    String.format("GeofenceTransitionIntentService:onHandleIntent(): Error code %d",
                    geofencingEvent.getErrorCode()));
            return;
        }

        name = intent.getStringExtra(MPLConsts.LOC_NAME);
        id = intent.getStringExtra(MPLConsts.LOC_ID);
//        address = intent.getStringExtra(MPLConsts.PLACE_ADDR);
//        lat = intent.getDoubleExtra(MPLConsts.PLACE_LAT, 0);
//        lng = intent.getDoubleExtra(MPLConsts.PLACE_LNG, 0);

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL ||
           geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
           geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            sendNotification(name, geofenceTransition);
        }
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return getString(R.string.geofence_transition_dwelling);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }

    private void sendNotification(String notificationDetails, int gfTransitionType) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), HomeActivity.class);
        notificationIntent.setAction(MPLConsts.GF_ACTION);
        notificationIntent.putExtra(MPLConsts.LOC_NAME, name);
        notificationIntent.putExtra(MPLConsts.LOC_ID, id);
//        notificationIntent.putExtra(MPLConsts.PLACE_ADDR, address);
//        notificationIntent.putExtra(MPLConsts.PLACE_LAT, lat);
//        notificationIntent.putExtra(MPLConsts.PLACE_LNG, lng);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(HomeActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

//        String contentText = String.format("%s %s",
//                getTransitionString(gfTransitionType), getString(R.string.notification_text));
        String contentText = "Click here to view favorite places!";

        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(contentText)
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }


}
