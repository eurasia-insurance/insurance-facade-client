package tech.lapsa.insurance.facade.producer.local;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import tech.lapsa.insurance.facade.EJBViaCDI;
import tech.lapsa.insurance.facade.EpaymentConnectionFacade;
import tech.lapsa.insurance.facade.EpaymentConnectionFacade.EpaymentConnectionFacadeLocal;

@Dependent
public class EpaymentConnectionFacadeProducer {

    @EJB
    private EpaymentConnectionFacadeLocal ejb;

    @Produces
    @EJBViaCDI
    public EpaymentConnectionFacade getEjb() {
	return ejb;
    }
}
