package tech.lapsa.insurance.facade.beans;

import java.util.List;
import java.util.Optional;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;

import com.lapsa.insurance.domain.policy.PolicyVehicle;

import tech.lapsa.insurance.facade.PolicyVehicleFacade;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.javax.cdi.qualifiers.QDelegateToEJB;
import tech.lapsa.kz.vehicle.VehicleRegNumber;

@Dependent
@QDelegateToEJB
public class PolicyVehicleFacadeDelegatingCDIBean implements PolicyVehicleFacade {

    @EJB
    private PolicyVehicleFacade delegate;

    @Override
    public List<PolicyVehicle> fetchByRegNumber(final VehicleRegNumber regNumber) throws IllegalArgument, IllegalState {
	return delegate.fetchByRegNumber(regNumber);
    }

    @Override
    public Optional<PolicyVehicle> fetchFirstByRegNumber(final VehicleRegNumber regNumber)
	    throws IllegalArgument, IllegalState {
	return delegate.fetchFirstByRegNumber(regNumber);
    }

    @Override
    public List<PolicyVehicle> fetchByVINCode(final String vinCode) throws IllegalArgument, IllegalState {
	return delegate.fetchByVINCode(vinCode);
    }

    @Override
    public Optional<PolicyVehicle> fetchFirstByVINCode(final String vinCode) throws IllegalArgument, IllegalState {
	return delegate.fetchFirstByVINCode(vinCode);
    }

    @Override
    public PolicyVehicle getByRegNumberOrDefault(final VehicleRegNumber regNumber)
	    throws IllegalArgument, IllegalState {
	return delegate.getByRegNumberOrDefault(regNumber);
    }
}