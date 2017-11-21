package tech.lapsa.insurance.facade;

import java.time.Instant;
import java.util.Currency;

import javax.ejb.Local;

import com.lapsa.insurance.domain.InsuranceRequest;

import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;

@Local
public interface InsuranceRequestFacade extends Acceptor<InsuranceRequest> {

    <T extends InsuranceRequest> T acceptAndReply(T request) throws IllegalArgument, IllegalState;

    void markPaymentSuccessful(Integer id, String methodName, Instant paymentInstant, Double amount,
	    Currency currency, String paymentReference) throws IllegalArgument, IllegalState;
}