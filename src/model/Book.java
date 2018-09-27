package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Book {
	private int id;
	private String title;
	private String summary;
	private int yearPublished;
	private String isbn;
	private LocalDateTime dateAdded;
	
	
	public Book() {
		this.id = -1;
		this.title = "";
		this.summary = "";
		this.yearPublished = -1;
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
	
	public void save(String title, String summary, String yearPublished, 
			String isbn, LocalDateTime dateAdded) throws Exception {
		validateTitle(title);
		validateSummary(summary);
		validateIsbn(isbn);
		
		int yearPublishedInt = 0;
		
		try {
			yearPublishedInt = Integer.parseInt(yearPublished);
			validateYearPublished(yearPublishedInt);
		} catch (NumberFormatException e) {
			throw new Exception("Unable to read year published.");
		}
		
		setTitle(title);
		setSummary(summary);
		setYearPublished(yearPublishedInt);
		setIsbn(isbn);
	}
	
	public void validateTitle(String title) throws Exception {
		if (title.length() <= 0)
			throw new Exception("Title of book must be provided.");
		if (title.length() > 255)
			throw new Exception("Title of book must be 255 characters or fewer.");
	}
	
	public void validateSummary(String title) throws Exception {
		if (title.length() > 65535)
			throw new Exception("Summary must be 65,535 characters or fewer.");
	}
	
	public void validateYearPublished(int yearPublished) throws Exception {	
		if (yearPublished > LocalDate.now().getYear())
			throw new Exception("Year published cannot be later than current year.");
	}
	
	public void validateIsbn(String isbn) throws Exception {
		if (isbn.length() > 13)
			throw new Exception("ISBN must be 13 characters of fewer.");
	}
	
	@Override
	public String toString() {
		return this.title;
	}
}
