package com.example.eta;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private final static String TAG = "MainActivity";
	private static boolean justOpenApp = true; 
	private static boolean uploadingIsOn = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(justOpenApp){
			UpdateLocationService.setServiceAlarm(this, false);
			justOpenApp = false;
		}
		setContentView(R.layout.activity_main);
		
		if(uploadingIsOn){
			ImageView imageServiceOnV = (ImageView) findViewById(R.id.upload_status_on_image);
			imageServiceOnV.setVisibility(View.VISIBLE);
			TextView textServiceOnV = (TextView) findViewById(R.id.upload_on_text);
			textServiceOnV.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_create_trip:
				createTrip();
				return true;
			case R.id.action_settings:
				openSettings();
				return true;
	            default:
	                return super.onOptionsItemSelected(item);
	        }
	}
	
	
	// functions in this activity
	public void createTrip(){
		Intent intent = new Intent(this, CreateTripActivity.class);
		startActivity(intent);
	}
	
	public void viewTripList(View view){
		Intent intent = new Intent(this, ViewTripListActivity.class);
		startActivity(intent);
	}
	
	
	public void startUploadLocation(View view){
		UpdateLocationService.setServiceAlarm(this, true);
		Log.i(TAG, "uploading is on!");
		uploadingIsOn = true;
		ImageView imageServiceOnV = (ImageView) findViewById(R.id.upload_status_on_image);
		imageServiceOnV.setVisibility(View.VISIBLE);
		TextView textServiceOnV = (TextView) findViewById(R.id.upload_on_text);
		textServiceOnV.setVisibility(View.VISIBLE);
	}
	
	public void stopUploadLocation(View view){
		UpdateLocationService.setServiceAlarm(this, false);
		Log.i(TAG, "uploading is off!");
		uploadingIsOn = false;
		ImageView imageServiceOnV = (ImageView) findViewById(R.id.upload_status_on_image);
		imageServiceOnV.setVisibility(View.INVISIBLE);
		TextView textServiceOnV = (TextView) findViewById(R.id.upload_on_text);
		textServiceOnV.setVisibility(View.INVISIBLE);
	}
	
	public void openSettings(){
		//to be implement...
	}


}

