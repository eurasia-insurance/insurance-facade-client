package tech.lapsa.insurance.facade.beans;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Provider;

import com.lapsa.insurance.domain.CompanyPointOfSale;
import com.lapsa.kz.country.KZCity;

import tech.lapsa.insurance.facade.CompanyPointOfSaleFacade;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.javax.cdi.qualifiers.QDelegateToEJB;

@Dependent
@QDelegateToEJB
public class CompanyPointOfSaleFacadeDelegatingCDIBean implements CompanyPointOfSaleFacade {

    @Inject
    private Provider<CompanyPointOfSaleFacade> delegateProvider;

    @Override
    public List<CompanyPointOfSale> pointOfSalesForPickup() throws IllegalArgument, IllegalState {
	return delegateProvider.get().pointOfSalesForPickup();
    }

    @Override
    public List<CompanyPointOfSale> pointOfSalesForPickup(final KZCity city) throws IllegalArgument, IllegalState {
	return delegateProvider.get().pointOfSalesForPickup(city);
    }

    @Override
    public List<CompanyPointOfSale> pointOfSalesForDelivery() throws IllegalArgument, IllegalState {
	return delegateProvider.get().pointOfSalesForDelivery();
    }

    @Override
    public List<CompanyPointOfSale> pointOfSalesForDelivery(final KZCity city) throws IllegalArgument, IllegalState {
	return delegateProvider.get().pointOfSalesForDelivery(city);
    }

    @Override
    public List<KZCity> getCitiesForPickup() throws IllegalArgument, IllegalState {
	return delegateProvider.get().getCitiesForPickup();
    }

    @Override
    public List<CompanyPointOfSale> findAllOwnOffices() throws IllegalArgument, IllegalState {
	return delegateProvider.get().findAllOwnOffices();
    }
}
