package tech.lapsa.insurance.facade;

import java.util.List;

import com.lapsa.insurance.domain.policy.PolicyVehicle;

public interface PolicyVehicleFacade {

    List<PolicyVehicle> fetchByRegNumber(String regNumber);

    List<PolicyVehicle> fetchByVINCode(String vinCode);

    PolicyVehicle fetchFirstByRegNumber(String regNumber);

    PolicyVehicle fetchFirstByVINCode(String vinCode);

}