package tech.lapsa.insurance.facade.beans;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Provider;

import com.lapsa.insurance.domain.policy.PolicyDriver;
import com.lapsa.insurance.elements.InsuranceClassType;

import tech.lapsa.insurance.facade.PolicyDriverFacade;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.javax.cdi.qualifiers.QDelegateToEJB;
import tech.lapsa.kz.taxpayer.TaxpayerNumber;

@Dependent
@QDelegateToEJB
public class PolicyDriverFacadeDelegatingCDIBean implements PolicyDriverFacade {

    @Inject
    private Provider<PolicyDriverFacade> delegateProvider;

    @Override
    public InsuranceClassType getDefaultInsuranceClass() throws IllegalArgument {
	return delegateProvider.get().getDefaultInsuranceClass();
    }

    @Override
    public Optional<PolicyDriver> fetchByIdNumber(final TaxpayerNumber idNumber) throws IllegalArgument, IllegalState {
	return delegateProvider.get().fetchByIdNumber(idNumber);
    }

    @Override
    public PolicyDriver getByTaxpayerNumberOrDefault(final TaxpayerNumber taxpayerNumber)
	    throws IllegalArgument, IllegalState {
	return delegateProvider.get().getByTaxpayerNumberOrDefault(taxpayerNumber);
    }
}
