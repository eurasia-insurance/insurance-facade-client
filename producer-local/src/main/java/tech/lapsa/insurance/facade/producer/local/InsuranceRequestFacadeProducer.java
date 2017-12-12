package tech.lapsa.insurance.facade.producer.local;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import tech.lapsa.insurance.facade.EJBViaCDI;
import tech.lapsa.insurance.facade.InsuranceRequestFacade;
import tech.lapsa.insurance.facade.InsuranceRequestFacade.InsuranceRequestFacadeLocal;

@Dependent
public class InsuranceRequestFacadeProducer {

    @EJB
    private InsuranceRequestFacadeLocal ejb;

    @Produces
    @EJBViaCDI
    public InsuranceRequestFacade getEjb() {
	return ejb;
    }
}
