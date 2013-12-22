package com.example.eta;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;
import android.util.Log;

public class UpdateLocationService extends IntentService {

	private static final String TAG = "UpdateLocationService";
	private String jsonStr;
	private static final int UPDATE_INTERVAL = 1000 * 15; 	//Every 15 seconds to activate GPS to work once and then upload.
														  	//Thus, even GPS is on, but it doesn't always on working status, as well as
														 	//the post process.
	private static Context CONTEXT;
	private double longitude, latitude;	
	private Location loc;

	
	
	public UpdateLocationService() {
		super(TAG);
		// TODO Auto-generated constructor stub
	}


	
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
		Log.i(TAG, "in onHandl");
		LocationManager locManager = (LocationManager) CONTEXT.getSystemService(Context.LOCATION_SERVICE);
		// check GPS
		if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

			startActivity(new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			// you can also popup a dialog box to bring them to the settings
			// menu
		}

	
		
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the location
				// provider.
				Log.i(TAG, "in onHandl3");
				makeUseOfNewLocation(location);
			}

			private void makeUseOfNewLocation(Location location) {
				
				Log.i(TAG, "(s)latitude is" + location.getLatitude());
				Log.i(TAG, "(s)longitude is" + location.getLongitude());

			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		Log.i(TAG, "in onHandl2");
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locationListener);
		loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		latitude = loc.getLatitude();
		longitude = loc.getLongitude();
		Log.i(TAG, "latitude is" + latitude);
		Log.i(TAG, "longitude is" + longitude);
		
		uploadLocation();
		
		locManager.removeUpdates(locationListener);		

	}

	public static void setServiceAlarm(Context context, boolean isOn) {
		Intent i = new Intent(context, UpdateLocationService.class);
		CONTEXT = context;
		PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Log.i(TAG, "set ServiceAlarm successfully1 ");

		if (isOn) {
			alarmManager.setRepeating(AlarmManager.RTC,
					System.currentTimeMillis(), UPDATE_INTERVAL, pi);
			Log.i(TAG, "set ServiceAlarm successfully2 ");
		} else {
			alarmManager.cancel(pi);
			pi.cancel();
		}
	}

	public void uploadLocation() {

		try {
			jsonStr = getJsonStr();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Gets the URL from the UI's text field.
		String stringUrl = "http://cs9033-homework.appspot.com/";
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new PostToServerTask().execute(stringUrl, jsonStr);
		} else {
			Log.i(TAG, "No network connection available.");
		}		
	}

	public String getJsonStr() throws JSONException {
		JSONObject jsObject = new JSONObject();



		jsObject.put("command", "UPDATE_LOCATION");
		jsObject.put("latitude", latitude);
		jsObject.put("longitude", longitude);
		jsObject.put("datetime", System.currentTimeMillis());
		return jsObject.toString();
	}

}
