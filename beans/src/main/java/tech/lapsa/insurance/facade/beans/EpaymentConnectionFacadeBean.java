package tech.lapsa.insurance.facade.beans;

import java.net.URI;
import java.time.Instant;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import tech.lapsa.epayment.shared.entity.XmlPaymentCompleteUnkownTypeRequest;
import tech.lapsa.epayment.shared.entity.XmlPaymentURISpecifierRequest;
import tech.lapsa.epayment.shared.entity.XmlPaymentURISpecifierResponse;
import tech.lapsa.epayment.shared.jms.EpaymentDestinations;
import tech.lapsa.insurance.facade.EpaymentConnectionFacade.EpaymentConnectionFacadeLocal;
import tech.lapsa.insurance.facade.EpaymentConnectionFacade.EpaymentConnectionFacadeRemote;
import tech.lapsa.javax.jms.client.JmsCallableClient;
import tech.lapsa.javax.jms.client.JmsConsumerClient;
import tech.lapsa.javax.jms.client.JmsDestination;
import tech.lapsa.javax.jms.client.JmsResultType;

@Stateless
public class EpaymentConnectionFacadeBean implements EpaymentConnectionFacadeLocal, EpaymentConnectionFacadeRemote {

    // READERS

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public URI getPaymentURI(final String invoiceNumber) throws IllegalArgumentException {
	return _getPaymentURI(invoiceNumber);
    }

    // MODIFIERS

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void markInvoiceHasPaid(final String invoiceNumber, final Double paidAmount, final Instant paidInstant,
	    final String paidReference) throws IllegalArgumentException, IllegalStateException {
	_markInvoiceHasPaid(invoiceNumber, paidAmount, paidInstant, paidReference);
    }

    // PRIVATE

    @Inject
    @JmsDestination(EpaymentDestinations.SPECIFY_PAYMENT_URI)
    @JmsResultType(XmlPaymentURISpecifierResponse.class)
    private JmsCallableClient<XmlPaymentURISpecifierRequest, XmlPaymentURISpecifierResponse> paymentURISpecifierClient;

    private URI _getPaymentURI(final String invoiceNumber) {
	// TODO FEAUTURE : Need to check required parameters
	final XmlPaymentURISpecifierRequest r = new XmlPaymentURISpecifierRequest(invoiceNumber);
	final XmlPaymentURISpecifierResponse resp = paymentURISpecifierClient.call(r);
	return resp.getURI();
    }

    @Inject
    @JmsDestination(EpaymentDestinations.COMPLETE_PAYMENT_WITH_UNKNOWN_TYPE)
    @JmsResultType(XmlPaymentCompleteUnkownTypeRequest.class)
    private JmsConsumerClient<XmlPaymentCompleteUnkownTypeRequest> paymentWithUnknwonTypeCompleterClient;

    private void _markInvoiceHasPaid(final String invoiceNumber, final Double paidAmount, final Instant paidInstant,
	    final String paidReference) {
	// TODO FEAUTURE : Need to check required parameters
	final XmlPaymentCompleteUnkownTypeRequest r = new XmlPaymentCompleteUnkownTypeRequest(
		invoiceNumber, paidAmount, paidInstant, paidReference);
	paymentWithUnknwonTypeCompleterClient.accept(r);
    }
}
