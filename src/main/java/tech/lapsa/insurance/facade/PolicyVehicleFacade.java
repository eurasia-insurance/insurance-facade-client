package tech.lapsa.insurance.facade;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.lapsa.insurance.domain.policy.PolicyVehicle;

import tech.lapsa.java.commons.exceptions.IllegalArgument;
import tech.lapsa.kz.vehicle.VehicleRegNumber;

public interface PolicyVehicleFacade extends EJBConstants {

    public static final String BEAN_NAME = "PolicyVehicleFacadeBean";

    @Local
    public interface PolicyVehicleFacadeLocal extends PolicyVehicleFacade {
    }

    @Remote
    public interface PolicyVehicleFacadeRemote extends PolicyVehicleFacade {
    }

    List<PolicyVehicle> fetchAllByVINCode(String vinCode) throws IllegalArgument;

    PolicyVehicle fetchFirstByVINCode(String vinCode) throws IllegalArgument, PolicyVehicleNotFound;

    List<PolicyVehicle> fetchAllByRegNumber(VehicleRegNumber regNumber) throws IllegalArgument;

    PolicyVehicle fetchFirstByRegNumberOrDefault(VehicleRegNumber regNumber)
	    throws IllegalArgument;

    PolicyVehicle fetchLastByRegNumberOrDefault(VehicleRegNumber regNumber) throws IllegalArgument;

    PolicyVehicle fetchFirstByRegNumber(VehicleRegNumber regNumber) throws IllegalArgument, PolicyVehicleNotFound;

    PolicyVehicle fetchLastByRegNumber(VehicleRegNumber regNumber) throws IllegalArgument, PolicyVehicleNotFound;

    @Deprecated
    void fetch(PolicyVehicle vehicle) throws IllegalArgument, PolicyVehicleNotFound;

    @Deprecated
    void clearFetched(PolicyVehicle vehicle) throws IllegalArgument;

}