package tech.lapsa.insurance.facade.beans;

import java.net.URI;

import javax.ejb.Stateless;
import javax.inject.Inject;

import tech.lapsa.epayment.facade.Ebill;
import tech.lapsa.epayment.facade.EpaymentFacade;
import tech.lapsa.insurance.facade.PaymentsFacade;

@Stateless
public class PaymentsFacadeBean implements PaymentsFacade {

    @Inject
    private EpaymentFacade delegate;

    @Override
    public URI getPaymentURI(String invoiceId) {
	Ebill ebill = delegate.newEbillFetcherBuilder().usingId(invoiceId).build().fetch();
	return delegate.getDefaultPaymentURI(ebill);
    }

}
