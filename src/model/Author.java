package model;

import java.time.LocalDate;
import java.util.List;

import gateway.AuthorTableGateway;

public class Author {
	int id;
	String firstName;
	String lastName;
	LocalDate dateOfBirth;
	String gender;
	String website;
	
	
	public Author() {
		this.id = -1;
		this.firstName = "";
		this.lastName = "";
		this.dateOfBirth = null;
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


	public String getDOB() {
		if (this.dateOfBirth != null) {
			return dateOfBirth.toString();
		} else {
			return "";
		}
	}
	
	public LocalDate getDOBDate() {
		return this.dateOfBirth;
	}


	public void setDOB(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public String getWebsite() {
		if (website != null) {
			return website;
		} else {
			return "";
		}
	}

	public void setWebsite(String website) {
		this.website = website;
	}
	
	public List<AuthorBook> getBooks() {
		List<AuthorBook> authorBooks = null;
		try {
			authorBooks = AuthorTableGateway.getInstance().getBooksForAuthor(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return authorBooks;
	}
	
	public String toString() {
		if (this.lastName != "") {
			return this.lastName + ", " + this.firstName;
		} else {
			return "";
		}
	}
	
	public String validateFirstName(String firstName) {
		if (firstName.length() <= 0) {
			return "First name must be provided.";
		}
		if (firstName.length() > 100) {
			return "First name must be 100 characters or fewer.";
		}
		return null;
	}
	
	public String validateLastName(String lastName) {
		if (lastName.length() <= 0) {
			return "Last name must be provided.";
		}
		if (lastName.length() > 100) {
			return "Last name must be 100 characters or fewer.";
		}
		return null;
	}
	
	public String validateGender(String gender) {
		if (gender.length() <= 0) {
			return "Gender must be provided.";
		}
		if (gender.length() > 8) {
			return "Gender must be 8 characters or fewer.";
		}
		return null;
	}
	
	public String validateDOB(LocalDate dateOfBirth) {
		if (dateOfBirth == null) {
			return "Date of Birth must be provided.";
		}
		if (dateOfBirth.isAfter(LocalDate.now())) {
			return "Date of Birth cannot be later than current date.";
		}
		return null;
	}
	
	public String validateWebsite(String website) {
		if (website.length() > 100) {
			return "Website must be 100 characters or fewer.";
		}

		return null;
	}
	
	public Author copy() {
		Author copy = new Author(this.getId(), this.getFirstName(), this.getLastName(), 
				this.getDOBDate(), this.getGender(), this.getWebsite());
		return copy;
	}

}
