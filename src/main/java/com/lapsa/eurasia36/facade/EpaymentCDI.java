package com.lapsa.eurasia36.facade;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.lapsa.epayment.facade.Ebill;
import com.lapsa.epayment.facade.QEpaymentSuccess;

@ApplicationScoped
public class EpaymentCDI {

    @Inject
    private InsuranceRequestFacade requestFacade;

    public void epaymentSuccess(@Observes @QEpaymentSuccess Ebill ebill) {
	requestFacade.markPaymentSucces(Integer.valueOf(ebill.getExternalId()), //
		ebill.getReference(), //
		ebill.getPaid());
    }
}
