package com.example.eta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TripDatabaseHelper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "eta";
	
	private static final String TABLE_TRIP = "trip";
	private static final String COLUMN_TRIP_ID = "_id"; // convention	
	private static final String COLUMN_TRIP_NAME = "name";
	private static final String COLUMN_TRIP_DESTINATION = "destination";
	private static final String COLUMN_TRIP_CONTENT = "content";
	private static final String COLUMN_TRIP_DATE = "date";
	private static final String COLUMN_TRIP_GIVEN_ID = "given_id";////
	
	private static final String TABLE_PERSON ="person";
	private static final String COLUMN_PERSON_ID ="_id";
	private static final String COLUMN_PERSON_NAME ="name";
	private static final String COLUMN_PERSON_TRIP ="trip_id";

	public TripDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// create trip table
		db.execSQL("create table " + TABLE_TRIP + "("
				+ COLUMN_TRIP_ID + " integer primary key autoincrement, "				
				+ COLUMN_TRIP_NAME + " text, "
				+ COLUMN_TRIP_DESTINATION + " text, "
				+ COLUMN_TRIP_CONTENT + " text, "
				+ COLUMN_TRIP_DATE + " text, "
				+ COLUMN_TRIP_GIVEN_ID+ " text)");////
		
		db.execSQL("create table " + TABLE_PERSON + "("
				+ COLUMN_PERSON_ID + " integer primary key autoincrement, "
				+ COLUMN_PERSON_NAME + " text, "
				+ COLUMN_PERSON_TRIP + " integer references trip(_id))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// Drop older table if exists
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSON);////
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);

		// create tables again
		onCreate(db);
	}
	
	public void rebuildTable() {
		SQLiteDatabase db = this.getReadableDatabase();
		this.onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION);
	}
	
	
	public long insertTrip(Trip trip) {

		ContentValues cv = new ContentValues();

		cv.put(COLUMN_TRIP_NAME, trip.getName());
		cv.put(COLUMN_TRIP_DESTINATION, trip.getDestination());
		cv.put(COLUMN_TRIP_CONTENT, trip.getContent());
		String tripDateStr = String.valueOf(trip.getDate());
		cv.put(COLUMN_TRIP_DATE, tripDateStr);
		cv.put(COLUMN_TRIP_GIVEN_ID, trip.getIdGivenByServer());////
		
		// return id of new trip
		return getWritableDatabase().insert(TABLE_TRIP, null, cv);
	
	}
	
	public long insertPerson(Person person) {
		//before insert person ,we need to get the _id of the newest trip we created just now
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE_TRIP + " where _id = (select max(_id) from trip)", null);
		cursor.moveToFirst();
		int tripIdInDb = cursor.getInt(0);
		
		ContentValues cv = new ContentValues();
		
		cv.put(COLUMN_PERSON_NAME, person.getName());
		cv.put(COLUMN_PERSON_TRIP, tripIdInDb);
		
		return getWritableDatabase().insert(TABLE_PERSON, null, cv);
	}
	
	
	public List<Trip> getAllTrips() {
		List<Trip> tripList = new ArrayList<Trip>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE_TRIP, null);
		// loop through all query results
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			Trip trip = new Trip();
			
			trip.setName(cursor.getString(1));
			trip.setDestination(cursor.getString(2));
			trip.setContent(cursor.getString(3));
			String tripDateStr = cursor.getString(4);
			Date dateTime = new Date(tripDateStr);
			trip.setDate(dateTime);
			trip.setTripIdGivenByServer(cursor.getString(5));////
			tripList.add(trip);
		}
		return tripList;
	}
	
	public List<Person> getPerson(int tripId) {
		List<Person> personList = new ArrayList<Person>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE_PERSON + " where trip_id = " + tripId, null);
//		loop through all query results
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			Person person = new Person();
			
			person.setName(cursor.getString(1));
			personList.add(person);
		}
		
		return personList;
	}

}
