package com.example.eta;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SubmitAndViewActivity extends Activity {

	private Trip newTrip;
	private final static String TAG = "SubmitAndViewActivity";
	private PostToServerTask ptsTask;
	private String nameLabel = "TRIP NAME:";
	private String destinationLabel = "DESTINATION:";
	private String contentLabel = "TRIP CONTENT:";
	private String dateLabel = "DATE & TIME:";
	private String peopleLable = "INVITED PEOPLE:";
	private String stringUrl = "http://cs9033-homework.appspot.com/";
	private String jsonStr;
	private String postResult;
	private String givenId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_submit_and_view);

		Intent intent = getIntent();
		newTrip = (Trip) intent
				.getSerializableExtra(CreateTripActivity.EXTRA_tripobject);

		// //get the trip time and split it into an array
		// String[] dateArr = String.valueOf(newTrip.getDate()).split(" ");

		// new trip information display
		TextView nameView = new TextView(this);
		TextView destinationView = new TextView(this);
		TextView contentView = new TextView(this);
		TextView dateView = new TextView(this);
		//
		TextView peopleView = new TextView(this);
		//
		nameView.setText(newTrip.getName());
		destinationView.setText(newTrip.getDestination());
		contentView.setText(newTrip.getContent());
		// dateView.setText(dateArr[1]+" "+dateArr[2]+"\n"+dateArr[3]+" "+dateArr[4]);
		dateView.setText(newTrip.getDate().toString());
		//
		String peopleStr = "";
		for (int i = 0; i < newTrip.getPeopleList().size(); i++)
			peopleStr = peopleStr + newTrip.getPeopleList().get(i).getName()
					+ "\n";
		peopleView.setText(peopleStr);
		//
		nameView.setTextSize(15);
		destinationView.setTextSize(15);
		contentView.setTextSize(15);

		nameView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		destinationView.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		contentView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		// create labels in display
		TextView nameViewLabel = new TextView(this);
		TextView destinationViewLabel = new TextView(this);
		TextView contentViewLabel = new TextView(this);
		TextView dateViewLabel = new TextView(this);
		TextView peopleViewLabel = new TextView(this);
		nameViewLabel.setText(nameLabel);
		destinationViewLabel.setText(destinationLabel);
		contentViewLabel.setText(contentLabel);
		dateViewLabel.setText(dateLabel);
		peopleViewLabel.setText(peopleLable);
		nameViewLabel.setTextSize(20);
		destinationViewLabel.setTextSize(20);
		contentViewLabel.setTextSize(20);
		dateViewLabel.setTextSize(20);
		peopleViewLabel.setTextSize(20);
		// display all data in layout
		LinearLayout linearlayout = (LinearLayout) findViewById(R.id.submit_and_view);

		linearlayout.addView(nameViewLabel);
		linearlayout.addView(nameView);
		linearlayout.addView(destinationViewLabel);
		linearlayout.addView(destinationView);
		linearlayout.addView(contentViewLabel);
		linearlayout.addView(contentView);
		linearlayout.addView(dateViewLabel);
		linearlayout.addView(dateView);
		linearlayout.addView(peopleViewLabel);
		linearlayout.addView(peopleView);

		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.submit_and_view, menu);
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

	public void gotoMain(View view) throws JSONException, InterruptedException,
			ExecutionException {
		Intent intent = new Intent(this, MainActivity.class);

		// get JSON string which is to be sent to server
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
			givenId = getIdFromResult(postResult);
			Log.i(TAG, "Id is "+givenId);
		} else {
			Log.i(TAG, "No network connection available.");
		}
		
		newTrip.setTripIdGivenByServer(givenId);

		// store this new trip into Database
		TripDatabaseHelper dbh = new TripDatabaseHelper(this);

		// if we want to rebuild all tables, use dbh.rebuildTable().
//		 dbh.rebuildTable(); 

		// insert trip and person to database
		dbh.insertTrip(newTrip);
		for (int i = 0; i < newTrip.getPeopleList().size(); i++) {
			Person person = newTrip.getPeopleList().get(i);
			dbh.insertPerson(person);
		}

		// this flag can help to go back to the original Main page
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	public String getJsonStr() throws JSONException {
		JSONObject jsObject = new JSONObject();

		ArrayList<String> plist = new ArrayList<String>();
		for (int i = 0; i < newTrip.getPeopleList().size(); i++) {
			plist.add(newTrip.getPeopleList().get(i).getName());
		}

		jsObject.put("command", "CREATE_TRIP");
		jsObject.put("location", newTrip.getDestination());
		jsObject.put("datetime", newTrip.getDate().getTime() / 1000);
		jsObject.put("people", new JSONArray(plist));
		return jsObject.toString();
	}

	public String getIdFromResult(String string) {
		String givenId = null;
		int endIndex = string.indexOf("}");
		givenId = string.substring(endIndex - 10, endIndex); // Because the Id
																// given by
																// server is 10
																// digits here.
		return givenId;
	}

}
