package tech.lapsa.insurance.facade;

import java.net.URI;
import java.time.Instant;

import javax.ejb.Local;
import javax.ejb.Remote;

import tech.lapsa.java.commons.exceptions.IllegalArgument;
import tech.lapsa.java.commons.exceptions.IllegalState;

public interface EpaymentConnectionFacade {

    @Local
    public interface EpaymentConnectionFacadeLocal extends EpaymentConnectionFacade {
    }

    @Remote
    public interface EpaymentConnectionFacadeRemote extends EpaymentConnectionFacade {
    }

    URI getPaymentURI(String invoiceNumber) throws IllegalArgument;

    void markInvoiceHasPaid(String invoiceNumber, Double paidAmount, Instant paidInstant, String paidReference)
	    throws IllegalArgument, IllegalState;
}
