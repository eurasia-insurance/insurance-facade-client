package tech.lapsa.insurance.facade.beans;

import static tech.lapsa.java.commons.function.MyExceptions.*;

import java.net.URI;

import javax.ejb.Stateless;
import javax.inject.Inject;

import tech.lapsa.epayment.domain.Invoice;
import tech.lapsa.epayment.facade.EpaymentFacade;
import tech.lapsa.epayment.facade.InvoiceNotFound;
import tech.lapsa.insurance.facade.PaymentsFacade;
import tech.lapsa.java.commons.function.MyExceptions;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;

@Stateless
public class PaymentsFacadeBean implements PaymentsFacade {

    @Inject
    private EpaymentFacade epayments;

    @Override
    public URI getPaymentURI(String invoiceNumber) throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> {
	    try {
		Invoice ebill = epayments.forNumber(invoiceNumber);
		return epayments.getDefaultPaymentURI(ebill);
	    } catch (InvoiceNotFound e) {
		throw MyExceptions.illegalArgumentFormat("Invoice not found with number %1$s", invoiceNumber);
	    }
	});
    }
}
