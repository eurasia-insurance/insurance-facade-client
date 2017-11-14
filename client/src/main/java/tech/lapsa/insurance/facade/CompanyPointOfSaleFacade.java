package tech.lapsa.insurance.facade;

import java.util.List;

import javax.ejb.Local;

import com.lapsa.insurance.domain.CompanyPointOfSale;
import com.lapsa.kz.country.KZCity;

import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;

@Local
public interface CompanyPointOfSaleFacade {

    List<CompanyPointOfSale> pointOfSalesForPickup() throws IllegalArgument, IllegalState;

    List<CompanyPointOfSale> pointOfSalesForPickup(KZCity city) throws IllegalArgument, IllegalState;

    List<CompanyPointOfSale> pointOfSalesForDelivery() throws IllegalArgument, IllegalState;

    List<CompanyPointOfSale> pointOfSalesForDelivery(KZCity city) throws IllegalArgument, IllegalState;

    List<KZCity> getCitiesForPickup() throws IllegalArgument, IllegalState;

    List<CompanyPointOfSale> findAllOwnOffices() throws IllegalArgument, IllegalState;

}