package tech.lapsa.insurance.facade.producer.remote;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import tech.lapsa.insurance.facade.CallbackRequestFacade;
import tech.lapsa.insurance.facade.CallbackRequestFacade.CallbackRequestFacadeRemote;
import tech.lapsa.insurance.facade.EJBViaCDI;

@Dependent
public class CallbackRequestFacadeProducer {

    @EJB
    private CallbackRequestFacadeRemote ejb;

    @Produces
    @EJBViaCDI
    public CallbackRequestFacade getEjb() {
	return ejb;
    }
}
