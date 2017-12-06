package tech.lapsa.insurance.facade;

import java.net.URI;

import javax.ejb.Local;

import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;

@Local
public interface PaymentsFacade {

    URI getPaymentURI(String invoiceNumber) throws IllegalArgument, IllegalState;

}
