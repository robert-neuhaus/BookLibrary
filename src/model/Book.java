package model;

import java.time.LocalDateTime;

public class Book {
	private int id;
	private String title;
	private String summary;
	private int yearPublished;
	private String ISBN;
	private LocalDateTime dateAdded;
	
	
	public Book() {
		title = "";
		summary = "";
		yearPublished = 0;
		ISBN = "";
		dateAdded = null;
	}

	public Book(int id, String title, String summary, 
			int yearPublished, String ISBN, 
			LocalDateTime dateAdded) {
		this.id = id;
		this.title = title;
		this.summary = summary;
		this.yearPublished = yearPublished;
		this.ISBN = ISBN;
		this.dateAdded = dateAdded;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getSummary() {
		return this.summary;
	}
	
	public String getYearPublished() {
		return Integer.toString(this.yearPublished);
	}
	
	public String getISBN() {
		return this.ISBN;
	}
	
	public String getDateAdded() {
		return this.dateAdded.toString();
	}
	

	@Override
	public String toString() {
		return this.title;
	}
}
