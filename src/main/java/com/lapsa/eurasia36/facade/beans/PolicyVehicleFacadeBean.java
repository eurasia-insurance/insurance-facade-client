package com.lapsa.eurasia36.facade;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.lapsa.commons.function.MyCollectors;
import com.lapsa.commons.function.MyOptionals;
import com.lapsa.insurance.domain.policy.PolicyVehicle;
import com.lapsa.insurance.elements.VehicleAgeClass;
import com.lapsa.insurance.esbd.domain.entities.policy.VehicleEntity;
import com.lapsa.insurance.esbd.services.policy.VehicleServiceDAO;

@Stateless
public class PolicyVehicleFacadeBean implements PolicyVehicleFacade {

    @Inject
    private VehicleServiceDAO vehicleService;

    @Override
    public List<PolicyVehicle> fetchByRegNumber(String regNumber) {
	return MyOptionals.streamOf(vehicleService.getByRegNumber(regNumber)) //
		.orElseGet(Stream::empty) //
		.map(this::fetchFrom) //
		.peek(x -> x.getCertificateData().setRegistrationNumber(regNumber)) //
		.collect(MyCollectors.unmodifiableList());
    }

    @Override
    public List<PolicyVehicle> fetchByVINCode(String vinCode) {
	return MyOptionals.streamOf(vehicleService.getByRegNumber(vinCode)) //
		.orElseGet(Stream::empty) //
		.map(this::fetchFrom) //
		.collect(MyCollectors.unmodifiableList());
    }

    @Override
    public PolicyVehicle fetchFirstByRegNumber(String regNumber) {
	return MyOptionals.streamOf(vehicleService.getByRegNumber(regNumber)) //
		.orElseGet(Stream::empty) //
		.findFirst()
		.map(this::fetchFrom) //
		.map(x -> {
		    x.getCertificateData().setRegistrationNumber(regNumber);
		    return x;
		})
		.orElse(null);
    }

    @Override
    public PolicyVehicle fetchFirstByVINCode(String vinCode) {
	return MyOptionals.streamOf(vehicleService.getByVINCode(vinCode)) //
		.orElseGet(Stream::empty) //
		.findFirst()
		.map(this::fetchFrom) //
		.orElse(null);
    }

    @Deprecated
    public void fetch(PolicyVehicle vehicle) {
	clearFetched(vehicle);
	PolicyVehicle fetched = fetchFirstByVINCode(vehicle.getVinCode()); // TODO
									   // fetchFirst
									   // fetching
									   // the
									   // first
									   // entity.
									   // What
									   // if
									   // has
									   // more?
	vehicle.setFetched(fetched.isFetched());
	vehicle.setVinCode(fetched.getVinCode());
	vehicle.setVehicleAgeClass(fetched.getVehicleAgeClass());
	vehicle.setYearOfManufacture(fetched.getYearOfManufacture());
	vehicle.setVehicleClass(fetched.getVehicleClass());

	vehicle.setColor(fetched.getColor());
	vehicle.setModel(fetched.getModel());
	vehicle.setManufacturer(fetched.getManufacturer());

	vehicle.getCertificateData().setRegistrationNumber(fetched.getCertificateData().getRegistrationNumber());
    }

    @Deprecated
    public void clearFetched(PolicyVehicle vehicle) {
	vehicle.setFetched(false);

	vehicle.setFetched(false);
	vehicle.setVehicleClass(null);
	vehicle.setVehicleAgeClass(null);
	vehicle.setColor(null);
	vehicle.setManufacturer(null);
	vehicle.setModel(null);
	vehicle.setYearOfManufacture(null);

	vehicle.getCertificateData().setRegistrationNumber(null);
    }

    // PRIVATE

    private PolicyVehicle fetchFrom(VehicleEntity esbdEntity) {
	PolicyVehicle vehicle = new PolicyVehicle();

	if (esbdEntity != null) {
	    vehicle.setFetched(true);

	    vehicle.setVinCode(esbdEntity.getVinCode());
	    if (esbdEntity.getRealeaseDate() != null) {
		vehicle.setVehicleAgeClass(obtainVehicleAgeClass(esbdEntity.getRealeaseDate()));
		vehicle.setYearOfManufacture(esbdEntity.getRealeaseDate().getYear());
	    }

	    vehicle.setVehicleClass(esbdEntity.getVehicleClass());

	    vehicle.setColor(esbdEntity.getColor());

	    if (esbdEntity.getVehicleModel() != null) {
		vehicle.setModel(esbdEntity.getVehicleModel().getName());
		if (esbdEntity.getVehicleModel().getManufacturer() != null)
		    vehicle.setManufacturer(esbdEntity.getVehicleModel().getManufacturer().getName());
	    }
	}

	return vehicle;
    }

    // PRIVATE STATIC

    private static VehicleAgeClass _obtainVehicleAgeClass(int age) {
	return age > 7 ? VehicleAgeClass.OVER7 : VehicleAgeClass.UNDER7;
    }

    private static VehicleAgeClass obtainVehicleAgeClass(LocalDate realeaseDate) {
	if (realeaseDate == null)
	    return null;
	int age = calculateAgeByDOB(realeaseDate);
	return _obtainVehicleAgeClass(age);
    }

    private static int calculateAgeByDOB(LocalDate dob) {
	if (dob == null)
	    throw new NullPointerException();
	return dob.until(LocalDate.now()).getYears();
    }

}
