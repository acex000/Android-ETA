package com.example.eta;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;

public class ViewTripListActivity extends Activity {
	
	public final static String EXTRA_TRIP_ID = "com.example.eta.listview_click_id";
	private ListView listView;
	private ArrayList<String> arrString = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_trip_list);
		// Show the Up button in the action bar.
		setupActionBar();
		listView = (ListView) findViewById(R.id.trip_listview);
		showList();
		
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ViewTripListActivity.this, ViewTripActivity.class);
				intent.putExtra(EXTRA_TRIP_ID, position+1);//position(0~...), position+1 indicates the id of the trip
				startActivity(intent);
			}
		});		 
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
		getMenuInflater().inflate(R.menu.view_trip_list, menu);
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

	public void showList(){

		TripDatabaseHelper dbh = new TripDatabaseHelper(this);
		List<Trip> tripList = dbh.getAllTrips();
		for(int i=1;i<=tripList.size();i++){
			arrString.add(String.valueOf(i)+"."+tripList.get(i-1).getName());
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrString);
		listView.setAdapter(adapter);
	}
	
	 
}
