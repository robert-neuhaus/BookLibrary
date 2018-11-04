package model;

import java.time.LocalDateTime;

public class Book implements Cloneable{
	private int id;
	private String title;
	private String summary;
	private int yearPublished;
	private String isbn;
	private LocalDateTime dateAdded;
	private LocalDateTime lastModified;
	private Publisher publisher;
	
	
	public Book() {
		this.id = 0;
		this.title = "";
		this.summary = "";
		this.yearPublished = -1;
		this.isbn = "";
		this.dateAdded = null;
		this.publisher = new Publisher(0, "Unknown");
	}

	public Book(int id, String title, String summary, 
			int yearPublished, String ISBN, Publisher publisher) {
		this.id = id;
		this.title = title;
		this.summary = summary;
		this.yearPublished = yearPublished;
		this.isbn = ISBN;
		this.publisher = publisher;
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
	
	public int getYearPublished() {
		return this.yearPublished;
	}
	
	public String getIsbn() {
		return this.isbn;
	}
	
	public LocalDateTime getDateAdded() {
		return this.dateAdded;
	}
	
	public LocalDateTime getLastModified() {
		return this.lastModified;
	}
	
	public Publisher getPublisher() {
		return this.publisher;
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
	
	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}
	
	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}
	
	public String validateTitle(String title) {
		if (title.length() <= 0)
			return "Title must be provided.";
		else if (title.length() > 255)
			return "Title must be 255 characters or fewer.";
		return null;
	}
	
	public String validateSummary(String summary) {
		if (summary.length() < 1)
			return "Summary must be provided.";
		else if (summary.length() > 65535)
			return "Summary must be 65535 characters or fewer";	
		return null;
	}
	
	public String validateYearPublished(String yearPublished) {	
		int pubYear;
		
		if (yearPublished.length() < 1)
			return "Publication year must be provided.";
		
		try {
			pubYear = Integer.parseInt(yearPublished);
		} catch(NumberFormatException e) {
			return "Unable to read publication year.";
		}
		
		if (pubYear > LocalDateTime.now().getYear())
			return "Publication year must not be later than current year.";
		return null;
	}
	
	public String validateIsbn(String isbn) {
		if (isbn.length() > 13 || isbn.length() < 13)
			return "ISBN must be exactly 13 characters.";
		return null;
	}
	
	@Override
	public String toString() {
		return this.title;
	}
	
	@Override
	public Object clone(){  
	    try{  
	        return super.clone();  
	    }catch(Exception e){ 
	        return null; 
	    }
	}
}
