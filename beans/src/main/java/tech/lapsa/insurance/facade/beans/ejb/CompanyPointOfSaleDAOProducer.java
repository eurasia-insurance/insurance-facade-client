package tech.lapsa.insurance.facade.beans.ejb;

import javax.ejb.EJB;
import javax.enterprise.inject.Produces;

import tech.lapsa.insurance.dao.CompanyPointOfSaleDAO;
import tech.lapsa.insurance.dao.CompanyPointOfSaleDAO.CompanyPointOfSaleDAORemote;

public class CompanyPointOfSaleDAOProducer {

    @EJB
    private CompanyPointOfSaleDAORemote ejb;

    @Produces
    @EJBViaCDI
    public CompanyPointOfSaleDAO getEjb() {
	return ejb;
    }
}
