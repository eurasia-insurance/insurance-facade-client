package tech.lapsa.insurance.facade;

import java.security.Principal;
import java.util.List;

import javax.ejb.Local;

import com.lapsa.insurance.domain.crm.User;

@Local
public interface UserFacade {

    User findOrCreate(String principalName);

    User findOrCreate(Principal principal);

    List<User> getWhoCreatedRequests();

}