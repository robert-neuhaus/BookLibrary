package exception;

import java.util.ArrayList;
import java.util.List;

import model.InvalidField;

public class ValidationException extends Exception {

	private static final long serialVersionUID = 1L;
	List<InvalidField> exceptions = new ArrayList<InvalidField>();

	public ValidationException(List<InvalidField> exceptions) {
		for (InvalidField exception : exceptions)
			this.exceptions.add(exception);
	}
	
	public List<InvalidField> getCauses() {
		return this.exceptions;
	}

}
