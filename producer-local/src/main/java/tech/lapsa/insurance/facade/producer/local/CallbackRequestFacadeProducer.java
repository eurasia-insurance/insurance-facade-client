package tech.lapsa.insurance.facade.producer.local;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import tech.lapsa.insurance.facade.CallbackRequestFacade;
import tech.lapsa.insurance.facade.CallbackRequestFacade.CallbackRequestFacadeLocal;
import tech.lapsa.insurance.facade.EJBViaCDI;

@Dependent
public class CallbackRequestFacadeProducer {

    @EJB
    private CallbackRequestFacadeLocal ejb;

    @Produces
    @EJBViaCDI
    public CallbackRequestFacade getEjb() {
	return ejb;
    }
}
