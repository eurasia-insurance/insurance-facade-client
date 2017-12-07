package tech.lapsa.insurance.facade.beans;

import static tech.lapsa.java.commons.function.MyExceptions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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

    // READERS

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<PolicyVehicle> fetchByRegNumber(final VehicleRegNumber regNumber) throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> _fetchByRegNumber(regNumber));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<PolicyVehicle> fetchByVINCode(final String vinCode) throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> _fetchByVINCode(vinCode));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Optional<PolicyVehicle> fetchFirstByRegNumber(final VehicleRegNumber regNumber)
	    throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> _fetchFirstByRegNumber(regNumber));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Optional<PolicyVehicle> fetchFirstByVINCode(final String vinCode) throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> _fetchFirstByVINCode(vinCode));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public PolicyVehicle getByRegNumberOrDefault(final VehicleRegNumber regNumber)
	    throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> _getByRegNumberOrDefault(regNumber));
    }

    @Deprecated
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void fetch(final PolicyVehicle vehicle) throws IllegalArgument, IllegalState {
	reThrowAsChecked(() -> _fetch(vehicle));
    }

    @Deprecated
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void clearFetched(final PolicyVehicle vehicle) throws IllegalArgument, IllegalState {
	reThrowAsChecked(() -> _clearFetched(vehicle));
    }

    // MODIFIERS

    // PRIVATE

    @Deprecated
    private void _clearFetched(final PolicyVehicle vehicle) {
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

    @Deprecated
    private void _fetch(final PolicyVehicle vehicle) throws IllegalArgument, IllegalState {
	clearFetched(vehicle);

	final PolicyVehicle fetched = fetchFirstByVINCode(vehicle.getVinCode()).orElse(null);
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
    }

    private PolicyVehicle _getByRegNumberOrDefault(final VehicleRegNumber regNumber) {
	return _fetchFirstByRegNumber(regNumber) //
		.orElseGet(() -> fillFromVehicleRegNumber(new PolicyVehicle(), regNumber));
    }

    private Optional<PolicyVehicle> _fetchFirstByVINCode(final String vinCode) {
	return MyOptionals.streamOf(vehicleService.getByVINCode(vinCode)) //
		.orElseGet(Stream::empty) //
		.findFirst()
		.map(this::fetchFromESBDEntity);
    }

    private Optional<PolicyVehicle> _fetchFirstByRegNumber(final VehicleRegNumber regNumber) {
	return MyOptionals.streamOf(vehicleService.getByRegNumber(regNumber)) //
		.orElseGet(Stream::empty) //
		.findFirst() //
		.map(this::fetchFromESBDEntity) //
		.map(x -> fillFromVehicleRegNumber(x, regNumber));
    }

    private List<PolicyVehicle> _fetchByVINCode(final String vinCode) {
	MyStrings.requireNonEmpty(vinCode, "vinCode");
	return MyOptionals.streamOf(vehicleService.getByVINCode(vinCode)) //
		.orElseGet(Stream::empty) //
		.map(this::fetchFromESBDEntity) //
		.collect(MyCollectors.unmodifiableList());
    }

    @Inject
    private VehicleEntityService vehicleService;

    private List<PolicyVehicle> _fetchByRegNumber(final VehicleRegNumber regNumber) {
	MyObjects.requireNonNull(regNumber, "regNumber");
	VehicleRegNumber.requireValid(regNumber);
	return MyOptionals.streamOf(vehicleService.getByRegNumber(regNumber)) //
		.orElseGet(Stream::empty) //
		.map(this::fetchFromESBDEntity) //
		.collect(MyCollectors.unmodifiableList());
    }

    private PolicyVehicle fetchFromESBDEntity(final VehicleEntity esbdEntity) {
	final PolicyVehicle vehicle = new PolicyVehicle();

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

    private PolicyVehicle fillFromVehicleRegNumber(final PolicyVehicle vehicle,
	    final VehicleRegNumber vehicleRegNumber) {

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

    private VehicleClass converKZLibVehcileType(final VehicleType y) {
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

    private static VehicleAgeClass _obtainVehicleAgeClass(final int age) {
	return age > 7 ? VehicleAgeClass.OVER7 : VehicleAgeClass.UNDER7;
    }

    private static VehicleAgeClass obtainVehicleAgeClass(final LocalDate realeaseDate) {
	if (realeaseDate == null)
	    return null;
	final int age = calculateAgeByDOB(realeaseDate);
	return _obtainVehicleAgeClass(age);
    }

    private static int calculateAgeByDOB(final LocalDate dob) {
	if (dob == null)
	    throw new NullPointerException();
	return dob.until(LocalDate.now()).getYears();
    }

}
