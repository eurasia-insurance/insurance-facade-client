package tech.lapsa.insurance.facade.beans;

import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;

import com.lapsa.insurance.domain.CompanyPointOfSale;
import com.lapsa.kz.country.KZCity;

import tech.lapsa.insurance.facade.CompanyPointOfSaleFacade;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.javax.cdi.qualifiers.QDelegateToEJB;

@Dependent
@QDelegateToEJB
public class CompanyPointOfSaleFacadeDelegatingCDIBean implements CompanyPointOfSaleFacade {

    @EJB
    private CompanyPointOfSaleFacade delegate;

    @Override
    public List<CompanyPointOfSale> pointOfSalesForPickup() throws IllegalArgument, IllegalState {
	return delegate.pointOfSalesForPickup();
    }

    @Override
    public List<CompanyPointOfSale> pointOfSalesForPickup(final KZCity city) throws IllegalArgument, IllegalState {
	return delegate.pointOfSalesForPickup(city);
    }

    @Override
    public List<CompanyPointOfSale> pointOfSalesForDelivery() throws IllegalArgument, IllegalState {
	return delegate.pointOfSalesForDelivery();
    }

    @Override
    public List<CompanyPointOfSale> pointOfSalesForDelivery(final KZCity city) throws IllegalArgument, IllegalState {
	return delegate.pointOfSalesForDelivery(city);
    }

    @Override
    public List<KZCity> getCitiesForPickup() throws IllegalArgument, IllegalState {
	return delegate.getCitiesForPickup();
    }

    @Override
    public List<CompanyPointOfSale> findAllOwnOffices() throws IllegalArgument, IllegalState {
	return delegate.findAllOwnOffices();
    }
}
