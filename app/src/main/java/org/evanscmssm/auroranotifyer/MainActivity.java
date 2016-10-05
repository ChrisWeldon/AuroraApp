package org.evanscmssm.auroranotifyer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;


public class MainActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    GoogleApiClient mGoogleApiClient = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create an instance of GoogleAPIClient.

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},1
                    /*LocationService.MY_PERMISSION_ACCESS_COURSE_LOCATION*/);
        }
    }

    protected void onStart(){
        mGoogleApiClient.connect();
        super.onStart();

    }
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    LocationRequest mLocationRequest;

    //TextView mLatitudeText = (TextView)findViewById(R.id.latText);
    //TextView mLongitudeText =(TextView)findViewById(R.id.lonText);;
    Location mLastLocation = null;

    String lat, lon;

    public void updateLoc(View w){

    }

    @Override
    public void onConnected(Bundle connectionHint){

        try {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(10000); // Update location every second

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {
                Log.d("Log", "Setting Text");
                lat = String.valueOf(mLastLocation.getLatitude());
                lon = String.valueOf(mLastLocation.getLongitude());
                updateUI();
            }
        }catch(SecurityException e){
            Log.d("Log", "Security Exception");
        }


    }



    public void onLocationChanged(Location l){
        updateUI();
    }

    void updateUI() {
        Log.d("Log", lat);
        Log.d("Log", lon);
        //mLatitudeText.setText(lat);
        //mLongitudeText.setText(lon);
    }




    @Override
    public void onConnectionSuspended(int i){
        Log.d("Log", "ConnectionSuspended");

    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        Log.d("Log", "Connection Failed");
    }

    public void myHandler(View w){

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute("http://services.swpc.noaa.gov/text/aurora-nowcast-map.txt");
        } else {
            Log.d("Network","no network or something");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public interface StreamHandler{
        boolean handleStream(InputStream strm);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean downloadUrl(String myurl, StreamHandler stream){
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("Network", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return stream.handleStream(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        }catch(IOException e) {
            return false;
        }finally
        {
            if (is != null) {
                try {
                    is.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }


    private class DownloadWebpageTask extends AsyncTask<String, Void, Boolean> {
        //USING DOWNLOAD URL
        @Override
        protected Boolean doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            return downloadUrl(urls[0], new StreamHandler() {
                @Override
                public boolean handleStream(InputStream strm) {
                    return true;
                }
            });

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Boolean result) {
            if(result == true){
                Log.d("network", "true");
            }else{
                Log.d("network", "false");
            }


        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        BufferedReader reader   = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

        new CoordinatesHandler(reader).update();


        return "hello";
    }


}
