package tech.lapsa.insurance.facade.beans;

import static tech.lapsa.java.commons.function.MyExceptions.*;

import java.net.URI;

import javax.ejb.Stateless;
import javax.inject.Inject;

import tech.lapsa.epayment.shared.jms.EpaymentDestinations;
import tech.lapsa.insurance.facade.PaymentsFacade;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.javax.jms.JmsCallableResultType;
import tech.lapsa.javax.jms.JmsClientFactory.JmsCallable;
import tech.lapsa.javax.jms.JmsDestinationMappedName;
import tech.lapsa.javax.jms.JmsServiceEntityType;

@Stateless
public class PaymentsFacadeBean implements PaymentsFacade {

    @Inject
    @JmsDestinationMappedName(EpaymentDestinations.PAYMENT_URI_QUALIFIER)
    @JmsServiceEntityType(String.class)
    @JmsCallableResultType(URI.class)
    private JmsCallable<String, URI> paymentURIQualifier;

    @Override
    public URI getPaymentURI(final String invoiceNumber) throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> {
	    return paymentURIQualifier.call(invoiceNumber);
	});
    }
}
