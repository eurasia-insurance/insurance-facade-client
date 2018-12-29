package tech.lapsa.insurance.facade;

import java.time.Instant;
import java.util.Currency;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.lapsa.insurance.domain.InsuranceRequest;

import tech.lapsa.java.commons.exceptions.IllegalArgument;

public interface InsuranceRequestFacade extends EJBConstants {

    public static final String BEAN_NAME = "InsuranceRequestFacadeBean";

    @Local
    public interface InsuranceRequestFacadeLocal extends InsuranceRequestFacade {
    }

    @Remote
    public interface InsuranceRequestFacadeRemote extends InsuranceRequestFacade {
    }

    <T extends InsuranceRequest> T newRequest(T request) throws IllegalArgument;

    <T extends InsuranceRequest> T newAcceptedRequest(T request) throws IllegalArgument;

    <T extends InsuranceRequest> T acceptRequest(T request) throws IllegalArgument;
    
    void completePayment(Integer id,
	    String methodName,
	    Instant paymentInstant,
	    Double amount,
	    Currency currency,
	    String paymentCard,
	    String paymentCardBank,
	    String paymentReference,
	    String payerName) throws IllegalArgument;
}