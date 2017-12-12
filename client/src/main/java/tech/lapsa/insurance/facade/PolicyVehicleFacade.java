package tech.lapsa.insurance.facade;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.lapsa.insurance.domain.policy.PolicyVehicle;

import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.kz.vehicle.VehicleRegNumber;

public interface PolicyVehicleFacade {

    @Local
    public interface PolicyVehicleFacadeLocal extends PolicyVehicleFacade {
    }

    @Remote
    public interface PolicyVehicleFacadeRemote extends PolicyVehicleFacade {
    }

    List<PolicyVehicle> fetchByRegNumber(VehicleRegNumber regNumber) throws IllegalArgumentException;

    List<PolicyVehicle> fetchByVINCode(String vinCode) throws IllegalArgumentException;

    PolicyVehicle getByRegNumberOrDefault(VehicleRegNumber regNumber) throws IllegalArgumentException;

    @Deprecated
    void fetch(PolicyVehicle vehicle) throws IllegalArgument, IllegalState;

    @Deprecated
    void clearFetched(PolicyVehicle vehicle) throws IllegalArgument, IllegalState;
}