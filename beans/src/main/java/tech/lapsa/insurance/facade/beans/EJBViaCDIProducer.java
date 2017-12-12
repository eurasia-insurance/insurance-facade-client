package tech.lapsa.insurance.facade.beans;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import tech.lapsa.insurance.dao.CallbackRequestDAO;
import tech.lapsa.insurance.dao.CallbackRequestDAO.CallbackRequestDAORemote;
import tech.lapsa.insurance.dao.CompanyPointOfSaleDAO;
import tech.lapsa.insurance.dao.CompanyPointOfSaleDAO.CompanyPointOfSaleDAORemote;
import tech.lapsa.insurance.dao.InsuranceRequestDAO;
import tech.lapsa.insurance.dao.InsuranceRequestDAO.InsuranceRequestDAORemote;
import tech.lapsa.insurance.dao.UserDAO;
import tech.lapsa.insurance.dao.UserDAO.UserDAORemote;
import tech.lapsa.insurance.esbd.elements.InsuranceClassTypeService;
import tech.lapsa.insurance.esbd.elements.InsuranceClassTypeService.InsuranceClassTypeServiceRemote;
import tech.lapsa.insurance.esbd.entities.SubjectPersonEntityService;
import tech.lapsa.insurance.esbd.entities.SubjectPersonEntityService.SubjectPersonEntityServiceRemote;
import tech.lapsa.insurance.esbd.entities.VehicleEntityService;
import tech.lapsa.insurance.esbd.entities.VehicleEntityService.VehicleEntityServiceRemote;
import tech.lapsa.insurance.facade.NotificationFacade;
import tech.lapsa.insurance.facade.NotificationFacade.NotificationFacadeLocal;
import tech.lapsa.insurance.facade.beans.ejb.EJBViaCDI;

@Dependent
public class EJBViaCDIProducer {

    // own (local)

    @EJB
    private NotificationFacadeLocal notifications;

    // insurance-dao (remote)

    @EJB
    private CallbackRequestDAORemote callbackRequestDAO;

    @EJB
    private CompanyPointOfSaleDAORemote companyPointOfSaleDAO;

    @EJB
    private InsuranceRequestDAORemote insuranceRequestDAO;

    @EJB
    private UserDAORemote userDAO;

    // insurance-esbd (remote)

    @EJB
    private SubjectPersonEntityServiceRemote subjectPersonEntityService;

    @EJB
    private InsuranceClassTypeServiceRemote insuranceClassTypeService;

    @EJB
    private VehicleEntityServiceRemote vehicleEntityService;

    // geters

    @Produces
    @EJBViaCDI
    public NotificationFacade getNotifications() {
	return notifications;
    }

    @Produces
    @EJBViaCDI
    public CallbackRequestDAO getCallbackRequestDAO() {
	return callbackRequestDAO;
    }

    @Produces
    @EJBViaCDI
    public CompanyPointOfSaleDAO getCompanyPointOfSaleDAO() {
	return companyPointOfSaleDAO;
    }

    @Produces
    @EJBViaCDI
    public InsuranceRequestDAO getInsuranceRequestDAO() {
	return insuranceRequestDAO;
    }

    @Produces
    @EJBViaCDI
    public UserDAO getUserDAO() {
	return userDAO;
    }

    @Produces
    @EJBViaCDI
    public SubjectPersonEntityService getSubjectPersonEntityService() {
	return subjectPersonEntityService;
    }

    @Produces
    @EJBViaCDI
    public InsuranceClassTypeService getInsuranceClassTypeService() {
	return insuranceClassTypeService;
    }

    @Produces
    @EJBViaCDI
    public VehicleEntityService getVehicleEntityService() {
	return vehicleEntityService;
    }
}
