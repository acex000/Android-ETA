package com.example.eta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;


public class Trip implements Serializable {
	private UUID tuuid;
	private String name;
	private String content;
	private Date dateTime;
	private String mDestination;
	private ArrayList<Person> arrPeople;
	private String givenIdFromServer;
	
	public Trip(){
		tuuid = UUID.randomUUID();
		name = null;
		content = null;
		dateTime = null;
		mDestination = null;
		arrPeople = new ArrayList<Person>();
		givenIdFromServer = null;
	}
	
	public Trip(String tripName, String tripDestination, String tripContent, Date tripDate){
		tuuid = UUID.randomUUID();
		name = tripName;
		content = tripContent;
		dateTime = tripDate;
		mDestination = tripDestination;
		arrPeople = new ArrayList<Person>();
	}
	
	public Trip(String tripName, String tripDestination, String tripContent, Date tripDate, ArrayList<Person> arrP){
		tuuid = UUID.randomUUID();
		name = tripName;
		content = tripContent;
		dateTime = tripDate;
		mDestination = tripDestination;
		arrPeople = arrP;
	}
	
	//set functions
	public void setName(String name){
		this.name = name;
	}
	
	public void setContent(String content){
		this.content = content;
	}

	public void setDestination(String destination){
		this.mDestination = destination;
	}
	
	public void setDate(Date date){
		this.dateTime = date;
	}
	
	public void addPeople(Person person) {
		arrPeople.add(person);
	}	
	
	public void setTripIdGivenByServer(String givenId){
		this.givenIdFromServer = givenId;
	}
	
	//get functions
	public String getName(){
		return name;
	}
	
	public String getContent(){
		return content;
	}

	public String getDestination(){
		return mDestination;
	}
	
	public Date getDate(){
		return dateTime;
	}
	
	public ArrayList<Person> getPeopleList() {
		return arrPeople;
	}
	
	public String getIdGivenByServer(){
		return givenIdFromServer;
	}
}
