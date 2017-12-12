package tech.lapsa.insurance.facade;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.lapsa.insurance.domain.CompanyPointOfSale;
import com.lapsa.kz.country.KZCity;

public interface CompanyPointOfSaleFacade {

    @Local
    public interface CompanyPointOfSaleFacadeLocal extends CompanyPointOfSaleFacade {
    }

    @Remote
    public interface CompanyPointOfSaleFacadeRemote extends CompanyPointOfSaleFacade {
    }

    List<CompanyPointOfSale> pointOfSalesForPickup();

    List<CompanyPointOfSale> pointOfSalesForPickup(KZCity city) throws IllegalArgumentException;

    List<CompanyPointOfSale> pointOfSalesForDelivery();

    List<CompanyPointOfSale> pointOfSalesForDelivery(KZCity city)
	    throws IllegalArgumentException;

    List<KZCity> getCitiesForPickup();

    List<CompanyPointOfSale> findAllOwnOffices();

}