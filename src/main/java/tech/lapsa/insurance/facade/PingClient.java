package tech.lapsa.insurance.facade;

import javax.ejb.Local;
import javax.ejb.Remote;

import tech.lapsa.java.commons.exceptions.IllegalState;

public interface PingClient extends EJBConstants {

    public static final String BEAN_NAME = "PingClientBean";

    @Local
    public interface PingClientLocal extends PingClient {
    }

    @Remote
    public interface PingClientRemote extends PingClient {
    }

    void fullPing() throws IllegalState;
}