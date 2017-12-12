package tech.lapsa.insurance.facade.beans.ejb;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import tech.lapsa.insurance.esbd.entities.SubjectPersonEntityService;
import tech.lapsa.insurance.esbd.entities.SubjectPersonEntityService.SubjectPersonEntityServiceRemote;

@Dependent
public class SubjectPersonEntityServiceProcucer {

    @EJB
    private SubjectPersonEntityServiceRemote ejb;

    @Produces
    @EJBViaCDI
    public SubjectPersonEntityService getEjb() {
	return ejb;
    }
}
