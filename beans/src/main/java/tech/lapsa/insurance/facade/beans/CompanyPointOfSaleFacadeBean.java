package tech.lapsa.insurance.facade.beans;

import static tech.lapsa.java.commons.function.MyExceptions.*;

import java.util.List;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.lapsa.insurance.domain.CompanyPointOfSale;
import com.lapsa.insurance.domain.PostAddress;
import com.lapsa.kz.country.KZCity;

import tech.lapsa.insurance.dao.CompanyPointOfSaleDAO;
import tech.lapsa.insurance.facade.CompanyPointOfSaleFacade;
import tech.lapsa.java.commons.function.MyCollectors;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.java.commons.function.MyOptionals;

@Stateless
public class CompanyPointOfSaleFacadeBean implements CompanyPointOfSaleFacade {

    // READERS

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<CompanyPointOfSale> pointOfSalesForPickup() throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> _pointOfSalesForPickup());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<CompanyPointOfSale> pointOfSalesForPickup(final KZCity city) throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> _pointOfSalesForPickup(city));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<CompanyPointOfSale> pointOfSalesForDelivery() throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> _pointOfSalesForDelivery());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<CompanyPointOfSale> pointOfSalesForDelivery(final KZCity city) throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> _pointOfSalesForDelivery(city));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<KZCity> getCitiesForPickup() throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> _getCitiesForPickup());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<CompanyPointOfSale> findAllOwnOffices() throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> _findAllOwnOffices());

    }

    // MODIFIERS

    // PRIVATE

    @Inject
    private CompanyPointOfSaleDAO companyPointOfSaleDAO;

    private List<CompanyPointOfSale> _findAllOwnOffices() {
	return allAvailable() //
		.filter(CompanyPointOfSale::isCompanyOwnOffice)
		.collect(MyCollectors.unmodifiableList());
    }

    private List<KZCity> _getCitiesForPickup() {
	return allAvailable() //
		.filter(CompanyPointOfSale::isPickupAvailable) //
		.map(CompanyPointOfSale::getAddress) //
		.map(PostAddress::getCity) //
		.distinct() //
		.collect(MyCollectors.unmodifiableList());
    }

    private List<CompanyPointOfSale> _pointOfSalesForDelivery(final KZCity city) {
	MyObjects.requireNonNull(city, "city");
	return allAvailable() //
		.filter(CompanyPointOfSale::isDeliveryServicesAvailable) //
		.filter(x -> MyObjects.nonNull(x.getAddress())) //
		.filter(x -> city.equals(x.getAddress().getCity())) //
		.collect(MyCollectors.unmodifiableList());
    }

    private List<CompanyPointOfSale> _pointOfSalesForDelivery() {
	return allAvailable() //
		.filter(CompanyPointOfSale::isDeliveryServicesAvailable) //
		.collect(MyCollectors.unmodifiableList());
    }

    private List<CompanyPointOfSale> _pointOfSalesForPickup(final KZCity city) {
	MyObjects.requireNonNull(city, "city");
	return allAvailable() //
		.filter(CompanyPointOfSale::isPickupAvailable)
		.filter(x -> MyObjects.nonNull(x.getAddress()))
		.filter(x -> city.equals(x.getAddress().getCity()))
		.collect(MyCollectors.unmodifiableList());
    }

    private List<CompanyPointOfSale> _pointOfSalesForPickup() {
	return allAvailable() //
		.filter(CompanyPointOfSale::isPickupAvailable) //
		.collect(MyCollectors.unmodifiableList());
    }

    private Stream<CompanyPointOfSale> allAvailable() {
	final List<CompanyPointOfSale> poses = companyPointOfSaleDAO.findAll();
	return MyOptionals.streamOf(poses) //
		.orElseGet(Stream::empty) //
		.filter(CompanyPointOfSale::isAvailable);
    }
}
