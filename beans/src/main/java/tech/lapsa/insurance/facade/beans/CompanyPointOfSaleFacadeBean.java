package tech.lapsa.insurance.facade.beans;

import java.util.List;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.lapsa.insurance.domain.CompanyPointOfSale;
import com.lapsa.insurance.domain.PostAddress;
import com.lapsa.kz.country.KZCity;

import tech.lapsa.insurance.dao.CompanyPointOfSaleDAO.CompanyPointOfSaleDAORemote;
import tech.lapsa.insurance.facade.CompanyPointOfSaleFacade.CompanyPointOfSaleFacadeLocal;
import tech.lapsa.insurance.facade.CompanyPointOfSaleFacade.CompanyPointOfSaleFacadeRemote;
import tech.lapsa.java.commons.exceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyCollectors;
import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.java.commons.function.MyOptionals;

@Stateless
public class CompanyPointOfSaleFacadeBean implements CompanyPointOfSaleFacadeLocal, CompanyPointOfSaleFacadeRemote {

    // READERS

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<CompanyPointOfSale> pointOfSalesForPickup() {
	return _pointOfSalesForPickup();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<CompanyPointOfSale> pointOfSalesForPickup(final KZCity city) throws IllegalArgument {
	try {
	    return _pointOfSalesForPickup(city);
	} catch (IllegalArgumentException e) {
	    throw IllegalArgument.from(e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<CompanyPointOfSale> pointOfSalesForDelivery() {
	return _pointOfSalesForDelivery();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<CompanyPointOfSale> pointOfSalesForDelivery(final KZCity city) throws IllegalArgument {
	try {
	    return _pointOfSalesForDelivery(city);
	} catch (IllegalArgumentException e) {
	    throw IllegalArgument.from(e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<KZCity> getCitiesForPickup() {
	return _getCitiesForPickup();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<CompanyPointOfSale> findAllOwnOffices() {
	return _findAllOwnOffices();

    }

    // MODIFIERS

    // PRIVATE

    @EJB
    private CompanyPointOfSaleDAORemote companyPointOfSaleDAO;

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

    private List<CompanyPointOfSale> _pointOfSalesForDelivery(final KZCity city) throws IllegalArgumentException {
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

    private List<CompanyPointOfSale> _pointOfSalesForPickup(final KZCity city) throws IllegalArgumentException {
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
