package model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import exception.validationException;

public class Book {
	private int id;
	private String title;
	private String summary;
	private int yearPublished;
	private String isbn;
	private Timestamp dateAdded;
	
	
	public Book() {
		this.id = -1;
		this.title = "";
		this.summary = "";
		this.yearPublished = 0;
		this.isbn = "";
		this.dateAdded = Timestamp.valueOf(LocalDateTime.now());
	}

	public Book(int id, String title, String summary, 
			int yearPublished, String ISBN) {
		this.id = id;
		this.title = title;
		this.summary = summary;
		this.yearPublished = yearPublished;
		this.isbn = ISBN;
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
	
	public void setDateAdded(Timestamp dateAdded) {
		this.dateAdded = dateAdded;
	}
	
	public void save(String title, String summary, String yearPublished, 
			String isbn) throws validationException {
		List<Throwable> exceptions = new ArrayList<Throwable>();
		
		if (!validateTitle(title))
			exceptions.add(new Throwable("*Title of book must be provided and must be 255 characters or shorter."));
		
		try {
			if (!validateYearPublished(Integer.parseInt(yearPublished)))
				exceptions.add(new Throwable("*Year published cannot be later than current year."));
		} catch (NumberFormatException e) {
				exceptions.add(new Throwable("*Unable to read year published."));
		}
		
		if (!validateIsbn(isbn))
			exceptions.add(new Throwable("*ISBN must be 13 characters or fewer."));
		
		if (!validateSummary(summary))
			exceptions.add(new Throwable("*Summary must be 65,535 characters or fewer."));
		
		if (!exceptions.isEmpty())
			throw new validationException(exceptions);
		
		setTitle(title);
		setSummary(summary);
		setYearPublished(Integer.parseInt(yearPublished));
		setIsbn(isbn);
		
	}
	
	public boolean validateTitle(String title) {
		if (title.length() <= 0 || title.length() > 255)
			return false;
		return true;
	}
	
	public boolean validateSummary(String summary) {
		if (summary.length() > 65535)
			return false;
		return true;
	}
	
	public boolean validateYearPublished(int yearPublished) {	
		if (yearPublished > LocalDate.now().getYear())
			return false;
		return true;
	}
	
	public boolean validateIsbn(String isbn) {
		if (isbn.length() > 13)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return this.title;
	}
}
