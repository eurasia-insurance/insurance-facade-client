package tech.lapsa.insurance.facade.beans;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.lapsa.epayment.facade.Ebill;
import com.lapsa.epayment.facade.QEpaymentSuccess;

import tech.lapsa.insurance.facade.InsuranceRequestFacade;

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
