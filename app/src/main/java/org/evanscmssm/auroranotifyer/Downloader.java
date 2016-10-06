package org.evanscmssm.auroranotifyer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by chrisevans on 10/5/16.
 */
public class Downloader {

    public void Downloader(){

    }

    public interface StreamHandler{
        boolean handleStream(InputStream strm);
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

    public void downloadURL(Context context, String url){
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(url);
        } else {
            Log.d("Network","no network or something");
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        BufferedReader reader   = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

        new CoordinatesHandler(reader).update();


        return "hello";
    }
}
