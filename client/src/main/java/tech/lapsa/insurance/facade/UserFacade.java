package tech.lapsa.insurance.facade;

import java.security.Principal;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.lapsa.insurance.domain.crm.User;

public interface UserFacade {

    @Local
    public interface UserFacadeLocal extends UserFacade {
    }

    @Remote
    public interface UserFacadeRemote extends UserFacade {
    }

    User findOrCreate(String principalName) throws IllegalArgumentException;

    User findOrCreate(Principal principal) throws IllegalArgumentException;

    List<User> getWhoEverCreatedRequests();

}