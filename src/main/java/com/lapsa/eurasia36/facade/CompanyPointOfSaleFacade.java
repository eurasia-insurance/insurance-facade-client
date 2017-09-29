package com.lapsa.insurance.facade.beans;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.lapsa.commons.function.MyCollectors;
import com.lapsa.commons.function.MyOptionals;
import com.lapsa.insurance.dao.CompanyPointOfSaleDAO;
import com.lapsa.insurance.domain.CompanyPointOfSale;
import com.lapsa.insurance.domain.PostAddress;
import com.lapsa.kz.country.KZCity;

@ApplicationScoped
public class CompanyPointOfSaleFacade {

    // do not use @Inject instead of @EJB because it goes to fault with CDI
    // deployment failure: WELD-001408: Unsatisfied dependencies
    @Inject
    private CompanyPointOfSaleDAO companyPointOfSaleDAO;

    public List<CompanyPointOfSale> pointOfSalesForPickup() {
	return allAvailable() //
		.filter(CompanyPointOfSale::isPickupAvailable) //
		.collect(MyCollectors.unmodifiableList());
    }

    public List<CompanyPointOfSale> pointOfSalesForPickup(final KZCity city) {
	Objects.requireNonNull(city, "city");
	return allAvailable() //
		.filter(CompanyPointOfSale::isPickupAvailable)
		.filter(x -> Objects.nonNull(x.getAddress()))
		.filter(x -> city.equals(x.getAddress().getCity()))
		.collect(MyCollectors.unmodifiableList());
    }

    public List<CompanyPointOfSale> pointOfSalesForDelivery() {
	return allAvailable() //
		.filter(CompanyPointOfSale::isDeliveryServicesAvailable) //
		.collect(MyCollectors.unmodifiableList());
    }

    public List<CompanyPointOfSale> pointOfSalesForDelivery(final KZCity city) {
	Objects.requireNonNull(city, "city");
	return allAvailable() //
		.filter(CompanyPointOfSale::isDeliveryServicesAvailable) //
		.filter(x -> Objects.nonNull(x.getAddress())) //
		.filter(x -> city.equals(x.getAddress().getCity())) //
		.collect(MyCollectors.unmodifiableList());
    }

    public List<KZCity> getCitiesForPickup() {
	return allAvailable() //
		.filter(CompanyPointOfSale::isPickupAvailable) //
		.map(CompanyPointOfSale::getAddress) //
		.map(PostAddress::getCity) //
		.distinct() //
		.collect(MyCollectors.unmodifiableList());
    }

    public List<CompanyPointOfSale> findAllOwnOffices() {
	return allAvailable() //
		.filter(CompanyPointOfSale::isCompanyOwnOffice)
		.collect(MyCollectors.unmodifiableList());
    }

    // PRIVATE

    private Stream<CompanyPointOfSale> allAvailable() {
	List<CompanyPointOfSale> poses = companyPointOfSaleDAO.findAll();
	return MyOptionals.streamOf(poses) //
		.orElseGet(Stream::empty) //
		.filter(CompanyPointOfSale::isAvailable);
    }
}
