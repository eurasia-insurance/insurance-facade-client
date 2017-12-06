package tech.lapsa.insurance.facade.beans;

import static tech.lapsa.java.commons.function.MyExceptions.*;

import java.net.URI;
import java.time.Instant;

import javax.ejb.Stateless;
import javax.inject.Inject;

import tech.lapsa.epayment.shared.entity.XmlPaymentURISpecifierRequest;
import tech.lapsa.epayment.shared.entity.XmlPaymentURISpecifierResponse;
import tech.lapsa.epayment.shared.entity.XmlPaymentWithUnknwonTypeCompleterRequest;
import tech.lapsa.epayment.shared.jms.EpaymentDestinations;
import tech.lapsa.insurance.facade.EpaymentConnectionFacade;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.javax.jms.client.JmsCallableClient;
import tech.lapsa.javax.jms.client.JmsConsumerClient;
import tech.lapsa.javax.jms.client.JmsDestination;
import tech.lapsa.javax.jms.client.JmsResultType;

@Stateless
public class EpaymentConnectionFacadeBean implements EpaymentConnectionFacade {

    @Inject
    @JmsDestination(EpaymentDestinations.SPECIFY_PAYMENT_URI)
    @JmsResultType(XmlPaymentURISpecifierResponse.class)
    private JmsCallableClient<XmlPaymentURISpecifierRequest, XmlPaymentURISpecifierResponse> paymentURISpecifierClient;

    @Override
    public URI getPaymentURI(final String invoiceNumber) throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> {
	    final XmlPaymentURISpecifierRequest r = new XmlPaymentURISpecifierRequest(invoiceNumber);
	    final XmlPaymentURISpecifierResponse resp = paymentURISpecifierClient.call(r);
	    return resp.getURI();
	});
    }

    @Inject
    @JmsDestination(EpaymentDestinations.COMPLETE_PAYMENT_WITH_UNKNOWN_TYPE)
    @JmsResultType(XmlPaymentWithUnknwonTypeCompleterRequest.class)
    private JmsConsumerClient<XmlPaymentWithUnknwonTypeCompleterRequest> paymentWithUnknwonTypeCompleterClient;

    @Override
    public void markInvoiceHasPaid(String invoiceNumber, Double paidAmount, Instant paidInstant,
	    String paidReference) throws IllegalArgument, IllegalState {
	reThrowAsChecked(() -> {
	    final XmlPaymentWithUnknwonTypeCompleterRequest r = new XmlPaymentWithUnknwonTypeCompleterRequest(
		    invoiceNumber, paidAmount, paidInstant, paidReference);
	    paymentWithUnknwonTypeCompleterClient.accept(r);
	});
    }
}
