package tech.lapsa.insurance.facade;

import java.security.Principal;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.lapsa.insurance.domain.crm.User;

import tech.lapsa.java.commons.exceptions.IllegalArgument;

public interface UserFacade extends EJBConstants {

    public static final String BEAN_NAME = "UserFacadeBean";

    @Local
    public interface UserFacadeLocal extends UserFacade {
    }

    @Remote
    public interface UserFacadeRemote extends UserFacade {
    }

    User findOrCreate(String principalName) throws IllegalArgument;

    User findOrCreate(Principal principal) throws IllegalArgument;

    List<User> getWhoEverCreatedRequests();

    List<User> getWhoEverAcceptedRequests();

    List<User> getWhoEverCompletedRequests();

    List<User> getAll();
}