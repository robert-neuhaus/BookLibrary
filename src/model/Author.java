package model;

import java.time.LocalDate;

public class Author {
	int id;
	String firstName;
	String lastName;
	LocalDate dateOfBirth;
	String gender;
	String website;
	
	
	public Author() {
		this.id = 0;
		this.firstName = "";
		this.lastName = "";
		this.dateOfBirth = LocalDate.now();
		this.gender = "";
		this.website = "";
	}
	
	public Author(int id, String firstName, String lastName, 
			LocalDate dateOfBirth, String gender, String website) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.gender = gender;
		this.website = website;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}


	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public String getWebsite() {
		return website;
	}


	public void setWebsite(String website) {
		this.website = website;
	}
	
	public String toString() {
		return this.firstName + this.lastName;
	}

}
