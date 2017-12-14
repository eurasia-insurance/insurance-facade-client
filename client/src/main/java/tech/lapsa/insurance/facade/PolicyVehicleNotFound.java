package tech.lapsa.insurance.facade;

public class PolicyVehicleNotFound extends Exception {

    private static final long serialVersionUID = 1L;

    public PolicyVehicleNotFound() {
    }

    public PolicyVehicleNotFound(String message, Throwable cause) {
	super(message, cause);
    }

    public PolicyVehicleNotFound(String message) {
	super(message);
    }

    public PolicyVehicleNotFound(Throwable cause) {
	super(cause);
    }
}
