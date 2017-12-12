package tech.lapsa.insurance.facade.producer.local;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import tech.lapsa.insurance.facade.EJBViaCDI;
import tech.lapsa.insurance.facade.UserFacade;
import tech.lapsa.insurance.facade.UserFacade.UserFacadeLocal;

@Dependent
public class UserFacadeProducer {

    @EJB
    private UserFacadeLocal ejb;

    @Produces
    @EJBViaCDI
    public UserFacade getEjb() {
	return ejb;
    }
}
