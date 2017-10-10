package tech.lapsa.insurance.facade;

import com.lapsa.insurance.domain.policy.PolicyDriver;
import com.lapsa.insurance.elements.InsuranceClassType;

public interface PolicyDriverFacade {

    InsuranceClassType getDefaultInsuranceClass();

    PolicyDriver fetchByIdNumber(String idNumber);

}