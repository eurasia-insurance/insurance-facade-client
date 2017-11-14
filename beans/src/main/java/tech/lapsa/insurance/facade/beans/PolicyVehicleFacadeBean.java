package tech.lapsa.insurance.facade.beans;

import static tech.lapsa.java.commons.function.MyExceptions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.lapsa.insurance.domain.policy.PolicyVehicle;
import com.lapsa.insurance.elements.VehicleAgeClass;
import com.lapsa.insurance.elements.VehicleClass;

import tech.lapsa.insurance.esbd.entities.VehicleEntity;
import tech.lapsa.insurance.esbd.entities.VehicleEntityService;
import tech.lapsa.insurance.facade.PolicyVehicleFacade;
import tech.lapsa.java.commons.function.MyCollectors;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.java.commons.function.MyOptionals;
import tech.lapsa.java.commons.function.MyStrings;
import tech.lapsa.kz.vehicle.VehicleRegNumber;
import tech.lapsa.kz.vehicle.VehicleType;

@Stateless
public class PolicyVehicleFacadeBean implements PolicyVehicleFacade {

    @Inject
    private VehicleEntityService vehicleService;

    @Override
    public List<PolicyVehicle> fetchByRegNumber(VehicleRegNumber regNumber) throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> {
	    MyObjects.requireNonNull(regNumber, "regNumber");
	    VehicleRegNumber.requireValid(regNumber);
	    return MyOptionals.streamOf(vehicleService.getByRegNumber(regNumber)) //
		    .orElseGet(Stream::empty) //
		    .map(this::fetchFromESBDEntity) //
		    .collect(MyCollectors.unmodifiableList());
	});
    }

    @Override
    public List<PolicyVehicle> fetchByVINCode(String vinCode) throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> {
	    MyStrings.requireNonEmpty(vinCode, "vinCode");
	    return MyOptionals.streamOf(vehicleService.getByVINCode(vinCode)) //
		    .orElseGet(Stream::empty) //
		    .map(this::fetchFromESBDEntity) //
		    .collect(MyCollectors.unmodifiableList());
	});
    }

    @Override
    public Optional<PolicyVehicle> fetchFirstByRegNumber(VehicleRegNumber regNumber)
	    throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> MyOptionals.streamOf(vehicleService.getByRegNumber(regNumber)) //
		.orElseGet(Stream::empty) //
		.findFirst() //
		.map(this::fetchFromESBDEntity) //
		.map(x -> fillFromVehicleRegNumber(x, regNumber)));
    }

    @Override
    public Optional<PolicyVehicle> fetchFirstByVINCode(String vinCode) throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> MyOptionals.streamOf(vehicleService.getByVINCode(vinCode)) //
		.orElseGet(Stream::empty) //
		.findFirst()
		.map(this::fetchFromESBDEntity));
    }

    @Override
    public PolicyVehicle getByRegNumberOrDefault(VehicleRegNumber regNumber) throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> fetchFirstByRegNumber(regNumber) //
		.orElseGet(() -> fillFromVehicleRegNumber(new PolicyVehicle(), regNumber)));
    }

    @Deprecated
    public void fetch(PolicyVehicle vehicle) throws IllegalArgument, IllegalState {
	reThrowAsChecked(() -> {
	    clearFetched(vehicle);

	    PolicyVehicle fetched = fetchFirstByVINCode(vehicle.getVinCode()).orElse(null);
	    if (fetched == null)
		return;

	    vehicle.setFetched(fetched.isFetched());
	    vehicle.setVinCode(fetched.getVinCode());
	    vehicle.setVehicleAgeClass(fetched.getVehicleAgeClass());
	    vehicle.setYearOfManufacture(fetched.getYearOfManufacture());
	    vehicle.setVehicleClass(fetched.getVehicleClass());

	    vehicle.setColor(fetched.getColor());
	    vehicle.setModel(fetched.getModel());
	    vehicle.setManufacturer(fetched.getManufacturer());

	    vehicle.getCertificateData().setRegistrationNumber(fetched.getCertificateData().getRegistrationNumber());
	});
    }

    @Deprecated
    public void clearFetched(PolicyVehicle vehicle) throws IllegalArgument, IllegalState {
	reThrowAsChecked(() -> {
	    vehicle.setFetched(false);

	    vehicle.setFetched(false);
	    vehicle.setVehicleClass(null);
	    vehicle.setVehicleAgeClass(null);
	    vehicle.setColor(null);
	    vehicle.setManufacturer(null);
	    vehicle.setModel(null);
	    vehicle.setYearOfManufacture(null);

	    vehicle.getCertificateData().setRegistrationNumber(null);
	});
    }

    // PRIVATE

    private PolicyVehicle fetchFromESBDEntity(VehicleEntity esbdEntity) {
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

    private PolicyVehicle fillFromVehicleRegNumber(PolicyVehicle vehicle, VehicleRegNumber vehicleRegNumber) {

	if (vehicle.getCertificateData().getRegistrationNumber() == null)
	    vehicle.getCertificateData().setRegistrationNumber(vehicleRegNumber);

	if (vehicle.getArea() == null)
	    vehicleRegNumber.optionalArea() //
		    .ifPresent(vehicle::setArea);

	if (vehicle.getVehicleClass() == null)
	    vehicleRegNumber.optionalVehicleType() //
		    .map(this::converKZLibVehcileType) //
		    .ifPresent(vehicle::setVehicleClass);

	return vehicle;
    }

    private VehicleClass converKZLibVehcileType(VehicleType y) {
	switch (y) {
	case MOTORBIKE:
	    return VehicleClass.MOTO;
	case TRAILER:
	    return VehicleClass.TRAILER;
	case CAR:
	default:
	    return null;
	}
    }

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
