package tech.lapsa.insurance.facade;

import java.net.URI;
import java.time.Instant;

import javax.ejb.Local;

import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;

@Local
public interface EpaymentConnectionFacade {

    URI getPaymentURI(String invoiceNumber) throws IllegalArgument, IllegalState;

    void markInvoiceHasPaid(String invoiceNumber, Double paidAmount, Instant paidInstant, String paidReference)
	    throws IllegalArgument, IllegalState;
}
