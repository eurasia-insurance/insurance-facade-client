package tech.lapsa.insurance.facade;

import javax.ejb.Local;

import com.lapsa.insurance.domain.policy.PolicyDriver;
import com.lapsa.insurance.elements.InsuranceClassType;

@Local
public interface PolicyDriverFacade {

    InsuranceClassType getDefaultInsuranceClass();

    PolicyDriver fetchByIdNumber(String idNumber);

}