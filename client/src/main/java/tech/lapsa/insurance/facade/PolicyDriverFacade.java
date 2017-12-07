package tech.lapsa.insurance.facade;

import java.util.Optional;

import javax.ejb.Local;

import com.lapsa.insurance.domain.policy.PolicyDriver;
import com.lapsa.insurance.elements.InsuranceClassType;

import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.kz.taxpayer.TaxpayerNumber;

@Local
public interface PolicyDriverFacade {

    InsuranceClassType getDefaultInsuranceClass() throws IllegalArgument, IllegalState;

    Optional<PolicyDriver> fetchByIdNumber(TaxpayerNumber idNumber) throws IllegalArgument, IllegalState;

    PolicyDriver getByTaxpayerNumberOrDefault(TaxpayerNumber taxpayerNumber) throws IllegalArgument, IllegalState;

}