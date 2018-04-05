package tech.lapsa.insurance.facade;

import java.time.Instant;
import java.util.Currency;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.lapsa.insurance.domain.InsuranceRequest;

import tech.lapsa.java.commons.exceptions.IllegalArgument;

public interface InsuranceRequestFacade extends Acceptor<InsuranceRequest>, EJBConstants {

    public static final String BEAN_NAME = "InsuranceRequestFacadeBean";

    @Local
    public interface InsuranceRequestFacadeLocal extends InsuranceRequestFacade {
    }

    @Remote
    public interface InsuranceRequestFacadeRemote extends InsuranceRequestFacade {
    }

    void completePayment(Integer id,
	    String methodName,
	    Instant paymentInstant,
	    Double amount,
	    Currency currency,
	    String paymentCard,
	    String paymentReference,
	    String payerName) throws IllegalArgument;
}