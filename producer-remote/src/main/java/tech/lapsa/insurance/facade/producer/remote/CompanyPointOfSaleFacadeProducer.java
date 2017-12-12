package tech.lapsa.insurance.facade.producer.remote;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import tech.lapsa.insurance.facade.CompanyPointOfSaleFacade;
import tech.lapsa.insurance.facade.CompanyPointOfSaleFacade.CompanyPointOfSaleFacadeRemote;
import tech.lapsa.insurance.facade.EJBViaCDI;

@Dependent
public class CompanyPointOfSaleFacadeProducer {

    @EJB
    private CompanyPointOfSaleFacadeRemote ejb;

    @Produces
    @EJBViaCDI
    public CompanyPointOfSaleFacade getEjb() {
	return ejb;
    }
}
