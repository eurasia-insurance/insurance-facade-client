package tech.lapsa.insurance.facade;

public class PolicyDriverNotFound extends Exception {

    private static final long serialVersionUID = 1L;

    public PolicyDriverNotFound() {
    }

    public PolicyDriverNotFound(String message, Throwable cause) {
	super(message, cause);
    }

    public PolicyDriverNotFound(String message) {
	super(message);
    }

    public PolicyDriverNotFound(Throwable cause) {
	super(cause);
    }
}
