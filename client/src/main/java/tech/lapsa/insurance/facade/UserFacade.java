package tech.lapsa.insurance.facade;

import java.security.Principal;
import java.util.List;

import com.lapsa.insurance.domain.crm.User;

public interface UserFacade {

    User findOrCreate(String principalName);

    User findOrCreate(Principal principal);

    List<User> getWhoCreatedRequests();

}