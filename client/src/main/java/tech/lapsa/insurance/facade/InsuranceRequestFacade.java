package tech.lapsa.insurance.facade;

import java.time.Instant;

import javax.ejb.Local;

import com.lapsa.insurance.domain.InsuranceRequest;

@Local
public interface InsuranceRequestFacade extends Acceptor<InsuranceRequest> {

    <T extends InsuranceRequest> T acceptAndReply(T request);

    void markPaymentComplete(Integer id, String paymentReference, Instant paymentInstant);
}