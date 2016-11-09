package org.evanscmssm.auroranotifyer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by chrisevans on 10/12/16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("Yay", "Alarm Received!!");



        Intent startMain = new Intent(context, AuroraService.class);
        startMain.putExtra("URL", "http://services.swpc.noaa.gov/text/aurora-nowcast-map.txt");

        context.startService(startMain);

    }

}
