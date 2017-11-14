package tech.lapsa.insurance.facade;

import java.security.Principal;
import java.util.List;

import javax.ejb.Local;

import com.lapsa.insurance.domain.crm.User;

import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;

@Local
public interface UserFacade {

    User findOrCreate(String principalName) throws IllegalArgument, IllegalState;

    User findOrCreate(Principal principal) throws IllegalArgument, IllegalState;

    List<User> getWhoEverCreatedRequests() throws IllegalArgument, IllegalState;

}