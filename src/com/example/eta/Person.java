package com.example.eta;

import java.io.Serializable;
import java.util.UUID;

public class Person implements Serializable {
	private UUID puuid;
	private String name;
//	private Integer phoneNumber;
	
	public Person(){
		puuid = UUID.randomUUID();
		name = null;
//		phoneNumber = null;
	}
	
	public Person(String personName){
		puuid = UUID.randomUUID();
		name = personName;
//		phoneNumber = pNum;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
