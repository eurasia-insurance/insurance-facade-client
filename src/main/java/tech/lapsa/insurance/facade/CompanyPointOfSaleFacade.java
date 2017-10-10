package com.lapsa.eurasia36.facade;

import java.util.List;

import com.lapsa.insurance.domain.CompanyPointOfSale;
import com.lapsa.kz.country.KZCity;

public interface CompanyPointOfSaleFacade {

    List<CompanyPointOfSale> pointOfSalesForPickup();

    List<CompanyPointOfSale> pointOfSalesForPickup(KZCity city);

    List<CompanyPointOfSale> pointOfSalesForDelivery();

    List<CompanyPointOfSale> pointOfSalesForDelivery(KZCity city);

    List<KZCity> getCitiesForPickup();

    List<CompanyPointOfSale> findAllOwnOffices();

}