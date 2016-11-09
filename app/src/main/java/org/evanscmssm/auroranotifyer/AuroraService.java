package org.evanscmssm.auroranotifyer;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


/**
 * Created by chrisevans on 10/12/16.
 */
public class AuroraService extends IntentService implements Downloader.DownloadHandler<CoordinatesHandler>{
    private static final String LAT_LOC = "LAT";
    private static final String LON_LOC = "LON";
    private static final int NOTIFICATION_ID = 001;
    AlarmService As;
    Location targetLocation;
    SharedPreferences settings;

    public AuroraService() {
        super("EvansAuroraService");
    }
    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("Yay", "Aurora Service started!!");
        String url = workIntent.getStringExtra("URL");
        Double lat = MainActivity.getDouble(settings, "LAT", 8 );
        Double lon = MainActivity.getDouble(settings, "LON", 8);
        Log.d("onHandleIntent", "lat "+ lat );
        Log.d("onHandleIntent", "lon "+ lon );
        CoordinatesHandler.download(this, url, this );

        targetLocation = new Location("Location");//provider name is unecessary
        targetLocation.setLatitude(lat);//your coords of course
        targetLocation.setLongitude(lon);


    }

    public void onDownloadError(int code, String message){
        Log.d("Download error","There was a download error :( ");
        Log.d("Error", message);
    }

    public void onDownloadResult(CoordinatesHandler coor){


        Log.d("Yay", "There was a downlaod Result!!");

        String probability = Integer.toString(coor.getProbability(targetLocation));
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle("Aurora Probablity")
                        .setContentText(probability);

        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("PROB", coor.getProbability(targetLocation) );
        editor.commit();

        Intent resultIntent = new Intent(this, MainActivity.class);

        As = new AlarmService(this);
        As.startAlarm();

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());

    }
}
