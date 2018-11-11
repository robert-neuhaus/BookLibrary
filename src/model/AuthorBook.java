package model;

import javafx.beans.property.SimpleStringProperty;

public class AuthorBook {
	Author author;
	Book book;
	int royalty;
	Boolean newRecord = true;
	
	SimpleStringProperty authorSimpleString = new SimpleStringProperty();
	SimpleStringProperty royaltySimpleString = new SimpleStringProperty();
	
	public AuthorBook() {
		this.author = new Author();
		this.book = new Book();
		this.royalty = 0;
		this.setAuthorSimpleString(this.author.toString());
		this.setRoyaltySimpleString(Integer.toString(royalty));
	}

	public AuthorBook(Author author, Book book, int royalty) {
		this.author = author;
		this.book = book;
		this.royalty = royalty;
		this.setAuthorSimpleString(this.author.toString());
		this.setRoyaltySimpleString(Integer.toString(royalty));
	}
	
	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public int getRoyalty() {
		return royalty;
	}

	public void setRoyalty(int royalty) {
		this.royalty = royalty;
	}

	public Boolean getNewRecord() {
		return newRecord;
	}

	public void setNewRecord(Boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	public void setAuthorSimpleString(String author) {
		this.authorSimpleString.set(author);
	}
	
	public String getAuthorSimpleString() {
		 return this.authorSimpleString.get();
	}
	
	public void setRoyaltySimpleString(String royalty) {
		this.royaltySimpleString.set(royalty);
	}
	
	public String getRoyaltySimpleString() {
		 return this.royaltySimpleString.get();
	}
	
	public String toString() {
		return this.author.getLastName()
				+ ", "
				+ author.getFirstName();
	}

}
