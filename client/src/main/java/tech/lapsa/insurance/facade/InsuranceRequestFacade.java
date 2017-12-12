package tech.lapsa.insurance.facade;

import java.time.Instant;
import java.util.Currency;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.lapsa.insurance.domain.InsuranceRequest;

public interface InsuranceRequestFacade extends Acceptor<InsuranceRequest> {

    @Local
    public interface InsuranceRequestFacadeLocal extends InsuranceRequestFacade {
    }

    @Remote
    public interface InsuranceRequestFacadeRemote extends InsuranceRequestFacade {
    }

    void completePayment(Integer id, String methodName, Instant paymentInstant, Double amount,
	    Currency currency, String paymentReference) throws IllegalArgumentException, IllegalStateException;
}