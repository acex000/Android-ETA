package com.example.eta;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;

public class ViewTripActivity extends Activity {
	private final static String TAG="ViewTripActivity";
	private final static String PEOPLE = "people";
	private final static String DISTANCE_LEFT = "distance_left";
	private final static String TIME_LEFT = "time_left";
	
	private int tripId;
	private Trip trip;
	private String peopleNameStr="";
	private TextView tripNameV, tripDestinationV, tripContentV, tripDatetimeV, peopleListV, tripGivenIdV;
	private String jsonStr;
	private String stringUrl="http://cs9033-homework.appspot.com/";
	private String postResult;
	private PostToServerTask ptsTask;
	private JSONObject resultJsObject;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_view_trip);
		//get tripId from intent
		Intent intent = getIntent();
		tripId = intent.getIntExtra(ViewTripListActivity.EXTRA_TRIP_ID, 0);
		//get the specified trip from Database
		TripDatabaseHelper dbh = new TripDatabaseHelper(this);
		List<Trip> tripList = dbh.getAllTrips();
		trip = tripList.get(tripId-1);
		//get the list of people invited in the chosen trip
		List<Person> personList = dbh.getPerson(tripId);
		for(int i=0;i<personList.size();i++)
			peopleNameStr = peopleNameStr + personList.get(i).getName()+"\n";
				
		//set TextViews
		tripNameV = (TextView) findViewById(R.id.view_trip_name);
		tripDestinationV = (TextView) findViewById(R.id.view_trip_destination);
		tripContentV = (TextView) findViewById(R.id.view_trip_content);
		tripDatetimeV = (TextView) findViewById(R.id.view_trip_datetime);
		peopleListV = (TextView) findViewById(R.id.view_trip_people);
		tripGivenIdV = (TextView) findViewById(R.id.view_trip_given_id);
		
		tripNameV.setText(trip.getName());
		tripDestinationV.setText(trip.getDestination());
		tripContentV.setText(trip.getContent());
		String[] dateArr = String.valueOf(trip.getDate()).split(" ");
		tripDatetimeV.setText(dateArr[0]+" "+dateArr[1]+" "+dateArr[2]+"\n"+dateArr[3]+" "+dateArr[4]+" "+dateArr[5]);
		peopleListV.setText(peopleNameStr);
		tripGivenIdV.setText(trip.getIdGivenByServer());
		
		// Show the Up button in the action bar.
		setupActionBar();
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_trip, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void checkTripStatus(View view) throws JSONException, InterruptedException, ExecutionException{
		jsonStr = getJsonStr();

		// get connection
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			// post request to server
			ptsTask = new PostToServerTask();
			postResult = ptsTask.execute(stringUrl, jsonStr).get();
			// here I understand with get() function, the async task may become a
			// sync task. However, I still use this,
			// because the local database store operations are based on the
			// result in this task(here is ID given by server).
			// Thus, we need to make sure the post task finished first. Since
			// this task execution is extremely quick, users
			// won't feel any block when UI thread wait for the post task.
		} else {
			Log.i(TAG, "No network connection available.");
		}
		resultJsObject = strToJsObject(postResult);
		showStatus(resultJsObject);
	}
	
	//get JSON string of trip_status request
	public String getJsonStr() throws JSONException{
		JSONObject jsObject = new JSONObject();
		jsObject.put("command", "TRIP_STATUS");
		jsObject.put("trip_id", trip.getIdGivenByServer());
		return jsObject.toString(); 
	}
	
	public JSONObject strToJsObject(String string) throws JSONException{
		JSONObject jsObject = new JSONObject(string);
		
//		Log.i(TAG, jsObject.getJSONArray(DISTANCE_LEFT).getString(0));
		return jsObject;
	}
	
	public void showStatus(JSONObject jsObject) throws JSONException{				
		LinearLayout  linearLayout = (LinearLayout) findViewById(R.id.status_linearlayout);
		//remove the old results to display new result clearly
		linearLayout.removeAllViews();
		
		LinearLayout titleLayout = new LinearLayout(this);
		TextView nameTitleV = new TextView(this);
		TextView distanceLeftTitleV = new TextView(this);
		TextView timeLeftTitleV = new TextView(this);
	
		nameTitleV.setText(PEOPLE);
		distanceLeftTitleV.setText(DISTANCE_LEFT);		
		timeLeftTitleV.setText(TIME_LEFT);
		
		titleLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		nameTitleV.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
		distanceLeftTitleV.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
		timeLeftTitleV.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.2f));
		
		titleLayout.setHorizontalGravity(1);
		
		titleLayout.addView(nameTitleV);
		titleLayout.addView(distanceLeftTitleV);
		titleLayout.addView(timeLeftTitleV);
		linearLayout.addView(titleLayout);
		
		for(int i=0;i<jsObject.getJSONArray(PEOPLE).length();i++){
			LinearLayout innerLayout = new LinearLayout(this);
			TextView nameV = new TextView(this);
			TextView distanceLeftV = new TextView(this);
			TextView timeLeftV = new TextView(this);
		
			nameV.setText(jsObject.getJSONArray(PEOPLE).getString(i));
			distanceLeftV.setText(String.valueOf(jsObject.getJSONArray(DISTANCE_LEFT).getDouble(i))+"miles");		
			timeLeftV.setText(jsObject.getJSONArray(TIME_LEFT).getInt(i)/60+"min"+jsObject.getJSONArray(TIME_LEFT).getInt(i)%60+"sec");
			
			innerLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			nameV.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
			distanceLeftV.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
			timeLeftV.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.2f));
			
			innerLayout.setHorizontalGravity(1);
			
			innerLayout.addView(nameV);
			innerLayout.addView(distanceLeftV);
			innerLayout.addView(timeLeftV);
			linearLayout.addView(innerLayout);
		}		
		
	}
	
}
