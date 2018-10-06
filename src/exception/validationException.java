package exception;

import java.util.List;

public class validationException extends Exception {

	List<Throwable> causes;

	public validationException(List<Throwable> causes) {
		for (Throwable cause : causes)
			this.causes.add(cause);
	}
	
	public List<Throwable> getCauses() {
		return this.causes;
	}

}
