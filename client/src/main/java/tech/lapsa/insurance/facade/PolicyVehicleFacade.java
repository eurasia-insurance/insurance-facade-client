package tech.lapsa.insurance.facade;

import java.util.List;

import javax.ejb.Local;

import com.lapsa.insurance.domain.policy.PolicyVehicle;

@Local
public interface PolicyVehicleFacade {

    List<PolicyVehicle> fetchByRegNumber(String regNumber);

    List<PolicyVehicle> fetchByVINCode(String vinCode);

    PolicyVehicle fetchFirstByRegNumber(String regNumber);

    PolicyVehicle fetchFirstByVINCode(String vinCode);

}