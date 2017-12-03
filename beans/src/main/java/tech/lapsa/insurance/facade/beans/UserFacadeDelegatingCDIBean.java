package tech.lapsa.insurance.facade.beans;

import java.security.Principal;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;

import com.lapsa.insurance.domain.crm.User;

import tech.lapsa.insurance.facade.UserFacade;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.javax.cdi.qualifiers.QDelegateToEJB;

@Dependent
@QDelegateToEJB
public class UserFacadeDelegatingCDIBean implements UserFacade {

    @EJB
    private UserFacade delegate;

    @Override
    public User findOrCreate(final String principalName) throws IllegalArgument, IllegalState {
	return delegate.findOrCreate(principalName);
    }

    @Override
    public User findOrCreate(final Principal principal) throws IllegalArgument, IllegalState {
	return delegate.findOrCreate(principal);
    }

    @Override
    public List<User> getWhoEverCreatedRequests() throws IllegalArgument, IllegalState {
	return delegate.getWhoEverCreatedRequests();
    }

}
