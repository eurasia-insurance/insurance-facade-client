package tech.lapsa.insurance.facade.beans;

import java.net.URI;

import javax.ejb.Stateless;
import javax.inject.Inject;

import tech.lapsa.epayment.domain.Invoice;
import tech.lapsa.epayment.facade.EpaymentFacade;
import tech.lapsa.insurance.facade.PaymentsFacade;

@Stateless
public class PaymentsFacadeBean implements PaymentsFacade {

    @Inject
    private EpaymentFacade epayments;

    @Override
    public URI getPaymentURI(String invoiceNumber) {
	Invoice ebill = epayments.forNumber(invoiceNumber);
	return epayments.getDefaultPaymentURI(ebill);
    }

}
