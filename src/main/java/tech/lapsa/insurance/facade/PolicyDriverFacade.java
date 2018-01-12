package tech.lapsa.insurance.facade;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.lapsa.insurance.domain.policy.PolicyDriver;
import com.lapsa.insurance.elements.InsuranceClassType;

import tech.lapsa.java.commons.exceptions.IllegalArgument;
import tech.lapsa.kz.taxpayer.TaxpayerNumber;

public interface PolicyDriverFacade extends EJBConstants {

    public static final String BEAN_NAME = "PolicyDriverFacadeBean";

    @Local
    public interface PolicyDriverFacadeLocal extends PolicyDriverFacade {
	@Deprecated
	void fetch(PolicyDriver driver) throws IllegalArgument, PolicyDriverNotFound;

	@Deprecated
	void clearFetched(PolicyDriver driver) throws IllegalArgument;
    }

    @Remote
    public interface PolicyDriverFacadeRemote extends PolicyDriverFacade {
    }

    InsuranceClassType getDefaultInsuranceClass();

    PolicyDriver getByTaxpayerNumber(TaxpayerNumber idNumber) throws IllegalArgument, PolicyDriverNotFound;

    PolicyDriver getByTaxpayerNumberOrDefault(TaxpayerNumber taxpayerNumber) throws IllegalArgument;
}