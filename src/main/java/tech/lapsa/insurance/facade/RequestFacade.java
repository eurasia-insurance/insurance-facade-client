package tech.lapsa.insurance.facade;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.lapsa.insurance.domain.Request;
import com.lapsa.insurance.domain.crm.User;

import tech.lapsa.java.commons.exceptions.IllegalArgument;
import tech.lapsa.java.commons.exceptions.IllegalState;

public interface RequestFacade extends EJBConstants {

    public static final String BEAN_NAME = "RequestFacadeBean";

    @Local
    public interface RequestFacadeLocal extends RequestFacade {
    }

    @Remote
    public interface RequestFacadeRemote extends RequestFacade {
    }

    <T extends Request> T commentRequest(T r, User user, String message) throws IllegalState, IllegalArgument;
}
