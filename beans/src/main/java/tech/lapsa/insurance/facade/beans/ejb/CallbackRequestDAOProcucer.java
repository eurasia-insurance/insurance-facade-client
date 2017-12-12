package tech.lapsa.insurance.facade.beans.ejb;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import tech.lapsa.insurance.dao.CallbackRequestDAO;
import tech.lapsa.insurance.dao.CallbackRequestDAO.CallbackRequestDAORemote;

@Dependent
public class CallbackRequestDAOProcucer {

    @EJB
    private CallbackRequestDAORemote ejb;

    @Produces
    @EJBViaCDI
    public CallbackRequestDAO getEjb() {
	return ejb;
    }
}
