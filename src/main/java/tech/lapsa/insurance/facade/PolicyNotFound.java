package tech.lapsa.insurance.facade;

public class PolicyNotFound extends Exception {

    private static final long serialVersionUID = 1L;

    public PolicyNotFound() {
	super();
    }

    public PolicyNotFound(String message, Throwable cause) {
	super(message, cause);
    }

    public PolicyNotFound(String message) {
	super(message);
    }

    public PolicyNotFound(Throwable cause) {
	super(cause);
    }
}
