package tech.lapsa.insurance.facade.beans;

import static tech.lapsa.java.commons.function.MyExceptions.*;

import java.net.URI;

import javax.ejb.Stateless;
import javax.inject.Inject;

import tech.lapsa.epayment.shared.entity.XmlPaymentURISpecifierRequest;
import tech.lapsa.epayment.shared.entity.XmlPaymentURISpecifierResponse;
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
    @JmsDestinationMappedName(EpaymentDestinations.SPECIFY_PAYMENT_URI)
    @JmsServiceEntityType(XmlPaymentURISpecifierRequest.class)
    @JmsCallableResultType(XmlPaymentURISpecifierResponse.class)
    private JmsCallable<XmlPaymentURISpecifierRequest, XmlPaymentURISpecifierResponse> paymentURISpecifier;

    @Override
    public URI getPaymentURI(final String invoiceNumber) throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> {
	    final XmlPaymentURISpecifierRequest r = new XmlPaymentURISpecifierRequest(invoiceNumber);
	    final XmlPaymentURISpecifierResponse resp = paymentURISpecifier.call(r);
	    return resp.getURI();
	});
    }
}
