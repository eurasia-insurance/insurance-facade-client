package tech.lapsa.insurance.facade.beans;

import java.net.URI;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Provider;

import tech.lapsa.insurance.facade.PaymentsFacade;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.javax.cdi.qualifiers.QDelegateToEJB;

@Dependent
@QDelegateToEJB
public class PaymentsFacadeDelegatingCDIBean implements PaymentsFacade {

    @Inject
    private Provider<PaymentsFacade> delegateProvider;

    @Override
    public URI getPaymentURI(final String invoiceId) throws IllegalArgument, IllegalState {
	return delegateProvider.get().getPaymentURI(invoiceId);
    }
}
