package tech.lapsa.insurance.facade.beans;

import java.time.Instant;
import java.util.Currency;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Provider;

import com.lapsa.insurance.domain.InsuranceRequest;

import tech.lapsa.insurance.facade.InsuranceRequestFacade;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.javax.cdi.qualifiers.QDelegateToEJB;

@Dependent
@QDelegateToEJB
public class InsuranceRequestFacadeDelegatingCDIBean implements InsuranceRequestFacade {

    @Inject
    private Provider<InsuranceRequestFacade> delegateProvider;

    @Override
    public <T extends InsuranceRequest> T acceptAndReply(final T request) throws IllegalArgument, IllegalState {
	return delegateProvider.get().acceptAndReply(request);
    }

    @Override
    public void markPaymentSuccessful(final Integer id, final String methodName, final Instant paymentInstant,
	    final Double amount, final Currency currency, final String paymentReference)
	    throws IllegalArgument, IllegalState {
	delegateProvider.get().markPaymentSuccessful(id, methodName, paymentInstant, amount, currency,
		paymentReference);
    }

}
