package assignment1;

public class Book {
	private String author;
	private String title;
	private String summary;
	private String yearPublished;
	private String ISBN;
	private String dateAdded;
	
	
	public Book() {
		author = "";
		title = "";
		summary = "";
		yearPublished = "";
		ISBN = "";
		dateAdded = "";
	}

	public Book(String title, String author, String summary, 
			String yearPublished, String ISBN, String dateAdded) {
		this.author = author;
		this.title = title;
		this.summary = summary;
		this.yearPublished = yearPublished;
		this.ISBN = ISBN;
		this.dateAdded = dateAdded;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getAuthor() {
		return this.author;
	}
	
	public String getSummary() {
		return this.summary;
	}
	
	public String getYearPublished() {
		return this.yearPublished;
	}
	
	public String getISBN() {
		return this.ISBN;
	}
	
	public String getDateAdded() {
		return this.dateAdded;
	}
	

	@Override
	public String toString() {
		return title + " by " + author;
	}
}
