package tech.lapsa.insurance.facade;

import java.time.Instant;

import javax.ejb.Local;

import com.lapsa.insurance.domain.InsuranceRequest;

@Local
public interface InsuranceRequestFacade extends Acceptor<InsuranceRequest> {

    <T extends InsuranceRequest> T acceptAndReply(T request);

    void markPaymentSuccessful(Integer id, String methodName, Instant paymentInstant, Double amount, String paymentReference);
}