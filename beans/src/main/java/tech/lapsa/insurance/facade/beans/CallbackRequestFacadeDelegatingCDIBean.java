package tech.lapsa.insurance.facade.beans;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;

import com.lapsa.insurance.domain.CallbackRequest;

import tech.lapsa.insurance.facade.CallbackRequestFacade;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.javax.cdi.qualifiers.QDelegateToEJB;

@Dependent
@QDelegateToEJB
public class CallbackRequestFacadeDelegatingCDIBean implements CallbackRequestFacade {

    @EJB
    private CallbackRequestFacade delegate;

    @Override
    public <T extends CallbackRequest> T acceptAndReply(final T request) throws IllegalArgument, IllegalState {
	return delegate.acceptAndReply(request);
    }
}
