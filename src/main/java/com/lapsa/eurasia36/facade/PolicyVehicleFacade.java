package com.lapsa.eurasia36.facade;

import java.time.LocalDate;
import java.util.Calendar;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.lapsa.commons.time.Temporals;
import com.lapsa.insurance.domain.policy.PolicyVehicle;
import com.lapsa.insurance.elements.VehicleAgeClass;
import com.lapsa.insurance.esbd.domain.entities.policy.VehicleEntity;
import com.lapsa.insurance.esbd.services.InvalidInputParameter;
import com.lapsa.insurance.esbd.services.NotFound;
import com.lapsa.insurance.esbd.services.policy.VehicleServiceDAO;

@ApplicationScoped
public class PolicyVehicleFacade {

    @Inject
    private VehicleServiceDAO vehicleService;

    public PolicyVehicle fetchByVINCode(String vinCode) {

	VehicleEntity vehicleLocal = null;
	{
	    if (vinCode != null) {
		try {
		    vehicleLocal = vehicleService.getByVINCode(vinCode);
		} catch (NotFound | InvalidInputParameter e) {
		}
	    }
	}

	PolicyVehicle vehicle = new PolicyVehicle();
	vehicle.setVinCode(vinCode);

	if (vehicleLocal != null) {
	    vehicle.setFetched(true);

	    if (vehicleLocal.getRealeaseDate() != null) {
		vehicle.setVehicleAgeClass(obtainVehicleAgeClass(vehicleLocal.getRealeaseDate()));
		vehicle.setYearOfManufacture(vehicleLocal.getRealeaseDate().get(Calendar.YEAR));
	    }

	    vehicle.setVehicleClass(vehicleLocal.getVehicleClass());

	    vehicle.setColor(vehicleLocal.getColor());

	    if (vehicleLocal.getVehicleModel() != null) {
		vehicle.setModel(vehicleLocal.getVehicleModel().getName());
		if (vehicleLocal.getVehicleModel().getManufacturer() != null)
		    vehicle.setManufacturer(vehicleLocal.getVehicleModel().getManufacturer().getName());
	    }
	}

	return vehicle;
    }

    public void fetch(PolicyVehicle vehicle) {
	clearFetched(vehicle);
	PolicyVehicle fetched = fetchByVINCode(vehicle.getVinCode());

	vehicle.setFetched(fetched.isFetched());

	vehicle.setVinCode(fetched.getVinCode());
	vehicle.setVehicleAgeClass(fetched.getVehicleAgeClass());
	vehicle.setYearOfManufacture(fetched.getYearOfManufacture());
	vehicle.setVehicleClass(fetched.getVehicleClass());

	vehicle.setColor(fetched.getColor());
	vehicle.setModel(fetched.getModel());
	vehicle.setManufacturer(fetched.getManufacturer());
    }

    public void clearFetched(PolicyVehicle vehicle) {
	vehicle.setFetched(false);

	vehicle.setFetched(false);
	vehicle.setVehicleClass(null);
	vehicle.setVehicleAgeClass(null);
	vehicle.setColor(null);
	vehicle.setManufacturer(null);
	vehicle.setModel(null);
	vehicle.setYearOfManufacture(null);
    }

    // PRIVATE STATIC helpers

    private static VehicleAgeClass _obtainVehicleAgeClass(int age) {
	return age > 7 ? VehicleAgeClass.OVER7 : VehicleAgeClass.UNDER7;
    }

    private static VehicleAgeClass obtainVehicleAgeClass(Calendar realeaseDate) {
	if (realeaseDate == null)
	    return null;
	int age = calculateAgeByDOB(Temporals.toLocalDate(realeaseDate));
	return _obtainVehicleAgeClass(age);
    }

    private static int calculateAgeByDOB(LocalDate dob) {
	if (dob == null)
	    throw new NullPointerException();
	return dob.until(LocalDate.now()).getYears();
    }

}
