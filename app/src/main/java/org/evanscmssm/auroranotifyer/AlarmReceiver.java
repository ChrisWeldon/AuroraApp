package org.evanscmssm.auroranotifyer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by chrisevans on 10/12/16.
 */
public class AlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("Yay", "Alarm Received!!");
        Intent startMain = new Intent(context, AuroraService.class);
        startMain.putExtra("URL", "https://mssm-cs-c9121.firebaseapp.com/aurora-nowcast-fake.txt");
        startMain.putExtra("LAT", 45.6);
        startMain.putExtra("LON", 30.4);

        context.startService(startMain);

    }

}
