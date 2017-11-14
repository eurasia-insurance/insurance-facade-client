package tech.lapsa.insurance.facade;

import java.util.List;
import java.util.Optional;

import javax.ejb.Local;

import com.lapsa.insurance.domain.policy.PolicyVehicle;

import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.kz.vehicle.VehicleRegNumber;

@Local
public interface PolicyVehicleFacade {

    List<PolicyVehicle> fetchByRegNumber(VehicleRegNumber regNumber) throws IllegalArgument, IllegalState;

    Optional<PolicyVehicle> fetchFirstByRegNumber(VehicleRegNumber regNumber) throws IllegalArgument, IllegalState;

    List<PolicyVehicle> fetchByVINCode(String vinCode) throws IllegalArgument, IllegalState;

    Optional<PolicyVehicle> fetchFirstByVINCode(String vinCode) throws IllegalArgument, IllegalState;

    PolicyVehicle getByRegNumberOrDefault(VehicleRegNumber regNumber) throws IllegalArgument, IllegalState;

}