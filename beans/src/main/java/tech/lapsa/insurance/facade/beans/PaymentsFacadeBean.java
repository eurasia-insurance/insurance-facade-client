package tech.lapsa.insurance.facade.beans;

import static tech.lapsa.java.commons.function.MyExceptions.*;

import java.net.URI;

import javax.ejb.Stateless;
import javax.inject.Inject;

import tech.lapsa.epayment.shared.entity.XmlPaymentURIQualifierRequest;
import tech.lapsa.epayment.shared.entity.XmlPaymentURIQualifierResponse;
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
    @JmsServiceEntityType(XmlPaymentURIQualifierRequest.class)
    @JmsCallableResultType(XmlPaymentURIQualifierResponse.class)
    private JmsCallable<XmlPaymentURIQualifierRequest, XmlPaymentURIQualifierResponse> paymentURIQualifier;

    @Override
    public URI getPaymentURI(final String invoiceNumber) throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> {
	    final XmlPaymentURIQualifierRequest r = new XmlPaymentURIQualifierRequest(invoiceNumber);
	    final XmlPaymentURIQualifierResponse resp = paymentURIQualifier.call(r);
	    return resp.getURI();
	});
    }
}
