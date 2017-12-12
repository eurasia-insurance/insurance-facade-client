package tech.lapsa.insurance.facade;

import java.net.URI;
import java.time.Instant;

import javax.ejb.Local;
import javax.ejb.Remote;

public interface EpaymentConnectionFacade {

    @Local
    public interface EpaymentConnectionFacadeLocal extends EpaymentConnectionFacade {
    }

    @Remote
    public interface EpaymentConnectionFacadeRemote extends EpaymentConnectionFacade {
    }

    URI getPaymentURI(String invoiceNumber) throws IllegalArgumentException;

    void markInvoiceHasPaid(String invoiceNumber, Double paidAmount, Instant paidInstant, String paidReference)
	    throws IllegalArgumentException, IllegalStateException;
}
