package tech.lapsa.insurance.facade;

public class PolicyDriverNotFound extends Exception {

    private static final long serialVersionUID = 1L;

    public PolicyDriverNotFound() {
    }

    public PolicyDriverNotFound(final String message, final Throwable cause) {
	super(message, cause);
    }

    public PolicyDriverNotFound(final String message) {
	super(message);
    }

    public PolicyDriverNotFound(final Throwable cause) {
	super(cause);
    }
}
