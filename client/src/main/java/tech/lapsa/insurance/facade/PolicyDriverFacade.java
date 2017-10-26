package tech.lapsa.insurance.facade;

import java.util.Optional;

import javax.ejb.Local;

import com.lapsa.insurance.domain.policy.PolicyDriver;
import com.lapsa.insurance.elements.InsuranceClassType;

import tech.lapsa.kz.taxpayer.TaxpayerNumber;

@Local
public interface PolicyDriverFacade {

    InsuranceClassType getDefaultInsuranceClass();

    Optional<PolicyDriver> fetchByIdNumber(TaxpayerNumber idNumber);

    PolicyDriver getByTaxpayerNumberOrDefault(TaxpayerNumber taxpayerNumber);

}