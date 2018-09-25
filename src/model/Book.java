package model;

import java.time.LocalDateTime;

public class Book {
	private int id;
	private String title;
	private String summary;
	private int yearPublished;
	private String isbn;
	private LocalDateTime dateAdded;
	
	
	public Book() {
		this.id = 0;
		this.title = "";
		this.summary = "";
		this.yearPublished = 0;
		this.isbn = "";
		this.dateAdded = LocalDateTime.now();
	}

	public Book(int id, String title, String summary, 
			int yearPublished, String ISBN, 
			LocalDateTime dateAdded) {
		this.id = id;
		this.title = title;
		this.summary = summary;
		this.yearPublished = yearPublished;
		this.isbn = ISBN;
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
	
	public String getIsbn() {
		return this.isbn;
	}
	
	public String getDateAdded() {
		return this.dateAdded.toString();
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public void setYearPublished(int yearPublished) {
		this.yearPublished = yearPublished;
	}
	
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	
	public void setDateAdded(LocalDateTime dateAdded) {
		this.dateAdded = dateAdded;
	}
	
	@Override
	public String toString() {
		return this.title;
	}
}
