package tech.lapsa.insurance.facade.beans.ejb;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import tech.lapsa.insurance.esbd.entities.VehicleEntityService;
import tech.lapsa.insurance.esbd.entities.VehicleEntityService.VehicleEntityServiceRemote;

@Dependent
public class VehicleEntityServiceProcucer {

    @EJB
    private VehicleEntityServiceRemote ejb;

    @Produces
    @EJBViaCDI
    public VehicleEntityService getEjb() {
	return ejb;
    }
}
