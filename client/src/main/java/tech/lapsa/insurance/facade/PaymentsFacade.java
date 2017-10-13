package tech.lapsa.insurance.facade;

import java.net.URI;

import javax.ejb.Local;

@Local
public interface PaymentsFacade {

    URI getPaymentURI(String invoiceId);

}
