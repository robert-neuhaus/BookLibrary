package model;

import java.time.LocalDateTime;

public class Audit {

	int bookId;
	String message;
	LocalDateTime dateAdded;
	
	public Audit(int bookId, String msg){
		this.bookId = bookId;
		this.message = msg;
		this.dateAdded = null;
	}
	
	public Audit(int bookId, LocalDateTime ldt, String msg){
		this.bookId = bookId;
		this.message = msg;
		this.dateAdded = ldt;
	}
	
	// Access methods.
	
	public int getBookID() {
		return this.bookId;
	}
	
	public String getAuditMsg() {
		return this.message;
	}
	
	public LocalDateTime getTimestamp() {
		return this.dateAdded;
	}
	
	public void setBookID(int bookId) {
		this.bookId = bookId;
	}
	
	public void setAuditMsg(String msg) {
		this.message= msg;
	}
	
	public void setTimestamp(LocalDateTime ldt) {
		this.dateAdded = ldt;
	}
	
}
