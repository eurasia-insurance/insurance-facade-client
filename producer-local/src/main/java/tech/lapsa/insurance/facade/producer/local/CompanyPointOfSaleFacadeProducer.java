package tech.lapsa.insurance.facade.producer.local;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import tech.lapsa.insurance.facade.CompanyPointOfSaleFacade;
import tech.lapsa.insurance.facade.CompanyPointOfSaleFacade.CompanyPointOfSaleFacadeLocal;
import tech.lapsa.insurance.facade.EJBViaCDI;

@Dependent
public class CompanyPointOfSaleFacadeProducer {

    @EJB
    private CompanyPointOfSaleFacadeLocal ejb;

    @Produces
    @EJBViaCDI
    public CompanyPointOfSaleFacade getEjb() {
	return ejb;
    }
}
