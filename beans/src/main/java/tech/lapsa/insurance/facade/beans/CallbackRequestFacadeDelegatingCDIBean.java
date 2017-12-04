package tech.lapsa.insurance.facade.beans;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Provider;

import com.lapsa.insurance.domain.CallbackRequest;

import tech.lapsa.insurance.facade.CallbackRequestFacade;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.javax.cdi.qualifiers.QDelegateToEJB;

@Dependent
@QDelegateToEJB
public class CallbackRequestFacadeDelegatingCDIBean implements CallbackRequestFacade {

    @Inject
    private Provider<CallbackRequestFacade> delegateProvider;

    @Override
    public <T extends CallbackRequest> T acceptAndReply(final T request) throws IllegalArgument, IllegalState {
	return delegateProvider.get().acceptAndReply(request);
    }
}
