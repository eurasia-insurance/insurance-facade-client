package tech.lapsa.insurance.facade.beans.ejb;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import tech.lapsa.insurance.esbd.elements.InsuranceClassTypeService;
import tech.lapsa.insurance.esbd.elements.InsuranceClassTypeService.InsuranceClassTypeServiceRemote;

@Dependent
public class InsuranceClassTypeServiceProcucer {

    @EJB
    private InsuranceClassTypeServiceRemote ejb;

    @Produces
    @EJBViaCDI
    public InsuranceClassTypeService getEjb() {
	return ejb;
    }
}
