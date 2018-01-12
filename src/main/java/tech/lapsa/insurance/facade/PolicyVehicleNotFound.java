package tech.lapsa.insurance.facade;

public class PolicyVehicleNotFound extends Exception {

    private static final long serialVersionUID = 1L;

    public PolicyVehicleNotFound() {
    }

    public PolicyVehicleNotFound(final String message, final Throwable cause) {
	super(message, cause);
    }

    public PolicyVehicleNotFound(final String message) {
	super(message);
    }

    public PolicyVehicleNotFound(final Throwable cause) {
	super(cause);
    }
}
