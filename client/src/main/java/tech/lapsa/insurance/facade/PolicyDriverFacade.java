package tech.lapsa.insurance.facade;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.lapsa.insurance.domain.policy.PolicyDriver;
import com.lapsa.insurance.elements.InsuranceClassType;

import tech.lapsa.kz.taxpayer.TaxpayerNumber;

public interface PolicyDriverFacade {

    @Local
    public interface PolicyDriverFacadeLocal extends PolicyDriverFacade {
    }

    @Remote
    public interface PolicyDriverFacadeRemote extends PolicyDriverFacade {
    }

    InsuranceClassType getDefaultInsuranceClass();

    PolicyDriver getByTaxpayerNumber(TaxpayerNumber idNumber) throws IllegalArgumentException;

    PolicyDriver getByTaxpayerNumberOrDefault(TaxpayerNumber taxpayerNumber) throws IllegalArgumentException;

    @Deprecated
    void fetch(PolicyDriver driver) throws IllegalArgumentException;

    @Deprecated
    void clearFetched(PolicyDriver driver) throws IllegalArgumentException;

}