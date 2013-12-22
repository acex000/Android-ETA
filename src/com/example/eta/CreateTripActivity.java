package com.example.eta;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
//import android.annotation.TargetApi;

public class CreateTripActivity extends Activity {


    public final static String EXTRA_tripobject = "com.example.eta.tripobject";
    private final static String TAG = "CreateTripActivity";
    
    static final int REQUEST_CONTACT = 1; // The request code
    
    private static String peopleNameSet = "", tripNameStr = "", tripDestinationStr = "", tripContentStr = "";
    private TextView contactNameV;
    private EditText tripNameV;
	private EditText tripContentV;
	private EditText tripDestinationV;
	
	private DatePicker datePicker;
	private TimePicker timePicker;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get the intent that started this activity
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if(Intent.ACTION_SEND.equals(action)&& type != null) {
			if ("text/plain".equals(type)) {
				String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);				
				if (sharedText != null) {
				// Update UI to reflect text being shared
					tripDestinationStr = sharedText;
				}
			}
		}
		
		
		setContentView(R.layout.activity_create_trip);
		//set the text for each EditText component
		tripNameV = (EditText) findViewById(R.id.trip_name);
		tripNameV.setText(tripNameStr);
		tripDestinationV = (EditText) findViewById(R.id.trip_destination);
		tripDestinationV.setText(tripDestinationStr);
		tripContentV = (EditText) findViewById(R.id.trip_content);
		tripContentV.setText(tripContentStr);

		
		contactNameV = new TextView(this);
		contactNameV.setText(peopleNameSet);
		LinearLayout  linearLayout = (LinearLayout) findViewById(R.id.invite_layout);
		linearLayout.addView(contactNameV);
		
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
		getMenuInflater().inflate(R.menu.create_trip, menu);
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
	
	
	public void invite(View view){
		pickContact();
		
	}
	
	private void pickContact() {
		Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(pickContactIntent, REQUEST_CONTACT);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){

		if(resultCode != Activity.RESULT_OK) return;

		Uri contactUri = data.getData(); // gets Uri from Intent object
		// now you query the ContentProvider with the data you want
		String[] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME};
		Cursor c = getContentResolver().query(contactUri, queryFields, null, null, null);
		
		// check to make sure you got the results
	    if(c.getCount() == 0) {
	        c.close();
	        return;
	    }
	   
	    // Get first row (will be only row in most cases)
	    c.moveToFirst();
	    peopleNameSet = peopleNameSet + c.getString(0)+";";

	    contactNameV.setText(peopleNameSet);
	    
	    c.close();  
	}
		
	
	//submit function to submit the new trip information
	public void submitTrip(View view) throws ParseException{
		Intent intent = new Intent(this, SubmitAndViewActivity.class);
		
		//get trip name and trip content
		tripNameV = (EditText) findViewById(R.id.trip_name);
		tripContentV = (EditText) findViewById(R.id.trip_content);
		tripDestinationV = (EditText) findViewById(R.id.trip_destination);
		String tripName = tripNameV.getText().toString();
		String tripDestination = tripDestinationV.getText().toString();
		String tripContent = tripContentV.getText().toString();
		
		//get the trip date and trip time
		datePicker = (DatePicker) findViewById(R.id.date_picker);		
		timePicker = (TimePicker) findViewById(R.id.time_picker);		
	
    	int tripYear = Integer.valueOf(datePicker.getYear());
    	int tripMonth = Integer.valueOf(datePicker.getMonth()); //month(0~11)
    	int tripDay = Integer.valueOf(datePicker.getDayOfMonth());    	
    	int tripHour = timePicker.getCurrentHour();
    	int tripMinute = timePicker.getCurrentMinute();    	


    	SimpleDateFormat simFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    	
    	Date dateTime = simFormat.parse(tripYear+"."+(tripMonth+1)+"."+tripDay+" "+tripHour+":"+tripMinute+":00");
    	
    	Log.i(TAG, "time is"+dateTime);
    	
    	//instantiate a trip object to carry all the input data
    	Trip trip = new Trip(tripName, tripDestination, tripContent, dateTime);
    	//add all people to the trip object
    	String[] nameArr = peopleNameSet.split(";"); 	
    	for(int i=0;i<nameArr.length;i++){
    		Person person = new Person(nameArr[i]);
    		trip.addPeople(person);
    	}
    	
       	intent.putExtra(EXTRA_tripobject, trip);	
      	
    	initTextStr();
		startActivity(intent);
	}

	public void callGoogleMaps(View view) {
		//save the texts that user has input in static strings
		tripNameV = (EditText) findViewById(R.id.trip_name);
		tripContentV = (EditText) findViewById(R.id.trip_content);
		tripDestinationV = (EditText) findViewById(R.id.trip_destination);
		tripNameStr = tripNameV.getText().toString();
		tripDestinationStr = tripDestinationV.getText().toString();
		tripContentStr = tripContentV.getText().toString();
		
		
		
		Intent intent = new Intent();
		intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
		
		startActivity(intent);
	}
	
	//set all static strings to initial status
	public void initTextStr(){
		tripNameStr = "";
		tripDestinationStr = "";
		tripContentStr = "";
		peopleNameSet = "";
	}
	
}
