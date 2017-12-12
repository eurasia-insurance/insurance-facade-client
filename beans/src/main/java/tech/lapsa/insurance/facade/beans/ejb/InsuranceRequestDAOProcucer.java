package tech.lapsa.insurance.facade.beans.ejb;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import tech.lapsa.insurance.dao.InsuranceRequestDAO;
import tech.lapsa.insurance.dao.InsuranceRequestDAO.InsuranceRequestDAORemote;

@Dependent
public class InsuranceRequestDAOProcucer {

    @EJB
    private InsuranceRequestDAORemote ejb;

    @Produces
    @EJBViaCDI
    public InsuranceRequestDAO getEjb() {
	return ejb;
    }
}
