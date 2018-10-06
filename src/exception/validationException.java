package exception;

import java.util.ArrayList;
import java.util.List;

public class validationException extends Exception {

	private static final long serialVersionUID = 1L;
	List<Throwable> exceptions = new ArrayList<Throwable>();

	public validationException(List<Throwable> exceptions) {
		for (Throwable exception : exceptions)
			this.exceptions.add(exception);
	}
	
	public List<Throwable> getCauses() {
		return this.exceptions;
	}

}
