package tech.lapsa.insurance.facade.beans;

import java.net.URI;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;

import tech.lapsa.insurance.facade.PaymentsFacade;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.javax.cdi.qualifiers.QDelegateToEJB;

@Dependent
@QDelegateToEJB
public class PaymentsFacadeDelegatingCDIBean implements PaymentsFacade {

    @EJB
    private PaymentsFacade delegate;

    @Override
    public URI getPaymentURI(final String invoiceId) throws IllegalArgument, IllegalState {
	return delegate.getPaymentURI(invoiceId);
    }
}
