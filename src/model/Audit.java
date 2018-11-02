package model;

import java.time.LocalDateTime;

public class Audit {


	int 		  audit_id;
	int 		  book_id;
	String 		  audit_message;
	LocalDateTime timestamp;
	
	public Audit(int new_book_id, String new_msg){
		this.book_id 	   = new_book_id;
		this.audit_message = new_msg;
		this.timestamp	   = null;
	}
	
	public Audit(int new_audit_id, int new_book_id, LocalDateTime ldt, String msg){
		this.audit_id 	   = new_audit_id;
		this.book_id 	   = new_book_id;
		this.audit_message = msg;
		this.timestamp     = ldt;
	}
	
	// Access methods.
	
	public int getBookID() {
		return this.book_id;
	}
	
	public String getAuditMsg() {
		return this.audit_message;
	}
	
	public int getAuditId() {
		return this.audit_id;
	}
	
	public LocalDateTime getTimestamp() {
		return this.timestamp;
	}
	
	public void setBookID(int new_book_id) {
		this.book_id 	   = new_book_id;
	}
	
	public void setAuditMsg(String new_msg) {
		this.audit_message = new_msg;
	}
	
	public void setAuditId(int new_audit_id) {
		this.audit_id 	   = new_audit_id;
	}
	
	public void setTimestamp(LocalDateTime ldt) {
		this.timestamp 	   = ldt;
	}
	
}
