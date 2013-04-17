package com.eiri.wifidb_uploader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

public class WiFiScanReceiver extends BroadcastReceiver {
  private static final String TAG = "WiFiScanReceiver";
  WiFiDemo wifiDemo;

  public WiFiScanReceiver(WiFiDemo wifiDemo) {
    super();
    this.wifiDemo = wifiDemo;
  }

  @Override
  public void onReceive(Context c, Intent intent) {
	  
    List<ScanResult> results = wifiDemo.wifi.getScanResults();

    for (ScanResult result : results) {
    	String bssid = result.BSSID;
    	String ssid = result.SSID;
    	int freq = result.frequency;
    	String capabilities = result.capabilities;
    	int level = result.level;
    	Log.d(TAG, "onReceive() message:1");
    	double[] gps = getGPS();
    	//double lat_d = gps[0];
    	//double lon_d = gps[1];
    	//String lat = Double.toString(lat_d);
    	//String lon = Double.toString(lon_d);
    	//Log.d(TAG, "onReceive() message:" + lat);
    	
    	// Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
         
        /* login.php returns true if username and password is equal to saranga */
        HttpPost httppost = new HttpPost("http://dev01.wifidb.net/wifidb/api/search.php");
 
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("bssid", bssid));
            nameValuePairs.add(new BasicNameValuePair("ssid", ssid));
            nameValuePairs.add(new BasicNameValuePair("freq", Integer.toString(freq)));
            nameValuePairs.add(new BasicNameValuePair("capabilities", ssid));
            nameValuePairs.add(new BasicNameValuePair("level", Integer.toString(level)));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
 
            // Execute HTTP Post Request
            Log.w("SENCIDE", "Execute HTTP Post Request");
            HttpResponse response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                Toast.makeText(wifiDemo, json, Toast.LENGTH_LONG).show();
                Log.w("response", json);
            }            
            
            
            
            Log.d(TAG, "onReceive() message: " + response);
             
	    } catch (UnsupportedEncodingException uee) {
	        Log.d("Exceptions", "UnsupportedEncodingException");
	        uee.printStackTrace();
	    } catch (ClientProtocolException cpe) {
	        Log.d("Exceptions", "ClientProtocolException");
	        cpe.printStackTrace();
	    } catch (IOException ioe) {
	        Log.d("Exceptions", "IOException");
	        ioe.printStackTrace();
	    }        
        
    	
    }

  }

private double[] getGPS() {
	 LocationManager lm = (LocationManager) getSystemService(
	  Context.LOCATION_SERVICE);
	 List<String> providers = lm.getProviders(true);

	 Location l = null;
	 
	 for (int i=providers.size()-1; i>=0; i--) {
	  l = lm.getLastKnownLocation(providers.get(i));
	  if (l != null) break;
	 }
	 
	 double[] gps = new double[2];
	 if (l != null) {
	  gps[0] = l.getLatitude();
	  gps[1] = l.getLongitude();
	 }

	 return gps;
}

private LocationManager getSystemService(String locationService) {
	// TODO Auto-generated method stub
	return null;
}
}