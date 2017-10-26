package tech.lapsa.insurance.facade;

import java.util.List;
import java.util.Optional;

import javax.ejb.Local;

import com.lapsa.insurance.domain.policy.PolicyVehicle;

import tech.lapsa.kz.vehicle.VehicleRegNumber;

@Local
public interface PolicyVehicleFacade {

    List<PolicyVehicle> fetchByRegNumber(VehicleRegNumber regNumber);

    Optional<PolicyVehicle> fetchFirstByRegNumber(VehicleRegNumber regNumber);

    List<PolicyVehicle> fetchByVINCode(String vinCode);

    Optional<PolicyVehicle> fetchFirstByVINCode(String vinCode);

    PolicyVehicle getByRegNumberOrDefault(VehicleRegNumber regNumber);

}