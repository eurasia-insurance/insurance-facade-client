package com.lapsa.eurasia36.facade;

import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.lapsa.insurance.domain.ContactData;
import com.lapsa.insurance.domain.IdentityCardData;
import com.lapsa.insurance.domain.OriginData;
import com.lapsa.insurance.domain.PersonalData;
import com.lapsa.insurance.domain.ResidenceData;
import com.lapsa.insurance.domain.policy.PolicyDriver;
import com.lapsa.insurance.elements.InsuranceClassType;
import com.lapsa.insurance.elements.InsuredAgeClass;
import com.lapsa.insurance.elements.Sex;
import com.lapsa.insurance.esbd.domain.entities.general.SubjectPersonEntity;
import com.lapsa.insurance.esbd.services.InvalidInputParameter;
import com.lapsa.insurance.esbd.services.NotFound;
import com.lapsa.insurance.esbd.services.elements.InsuranceClassTypeServiceDAO;
import com.lapsa.insurance.esbd.services.general.SubjectPersonServiceDAO;
import com.lapsa.kz.idnumber.IDNumberUtils;
import com.lapsa.utils.TemporalUtils;

@ApplicationScoped
public class PolicyDriverFacade {

    @Inject
    private SubjectPersonServiceDAO subjectPersonService;

    @Inject
    private InsuranceClassTypeServiceDAO insuranceClassTypeService;

    public InsuranceClassType getDefaultInsuranceClass() {
	return insuranceClassTypeService.getDefault();
    }

    public PolicyDriver fetchByIdNumber(String idNumber) {

	SubjectPersonEntity subjectLocal = null;
	{
	    if (idNumber != null)
		try {
		    subjectLocal = subjectPersonService.getByIIN(idNumber);
		} catch (NotFound | InvalidInputParameter e) {
		}
	}

	InsuranceClassType insuranceClassTypeLocal = null;
	{
	    insuranceClassTypeLocal = insuranceClassTypeService.getDefault();
	    if (subjectLocal != null) {
		try {
		    insuranceClassTypeLocal = insuranceClassTypeService.getForSubject(subjectLocal);
		} catch (NotFound | InvalidInputParameter e) {
		}
	    }
	}

	LocalDate dobLocal = null;
	{
	    if (idNumber != null)
		dobLocal = IDNumberUtils.parseDOBFromIdNumberLocalDate(idNumber);
	    if (subjectLocal != null && subjectLocal.getPersonal() != null
		    && subjectLocal.getPersonal().getDayOfBirth() != null)
		dobLocal = TemporalUtils.toLocalDate(subjectLocal.getPersonal().getDayOfBirth());
	}

	InsuredAgeClass insuredAgeClassLocal = null;
	{
	    if (dobLocal != null)
		insuredAgeClassLocal = obtainInsuredAgeClass(dobLocal);
	}

	Sex sexLocal = null;
	{
	    if (idNumber != null)
		sexLocal = convertKZLibSex(IDNumberUtils.parseSexFromIdNumber(idNumber));
	    if (subjectLocal != null && subjectLocal.getPersonal() != null
		    && subjectLocal.getPersonal().getSex() != null)
		sexLocal = subjectLocal.getPersonal().getSex();
	}

	PolicyDriver driver = new PolicyDriver();
	driver.setIdNumber(idNumber);
	driver.setInsuranceClassType(insuranceClassTypeLocal);
	driver.setAgeClass(insuredAgeClassLocal);

	driver.getPersonalData().setDayOfBirth(dobLocal);
	driver.getPersonalData().setSex(sexLocal);

	if (subjectLocal != null) {
	    driver.setFetched(true);

	    if (subjectLocal.getPersonal() != null) {
		driver.getPersonalData().setName(subjectLocal.getPersonal().getName());
		driver.getPersonalData().setSurename(subjectLocal.getPersonal().getSurename());
		driver.getPersonalData().setPatronymic(subjectLocal.getPersonal().getPatronymic());
	    }

	    if (subjectLocal.getOrigin() != null) {
		driver.getResidenceData().setResident(subjectLocal.getOrigin().isResident());
		driver.getOriginData().setCountry(subjectLocal.getOrigin().getCountry());
	    }

	    if (subjectLocal.getContact() != null)
		driver.getResidenceData().setAddress(subjectLocal.getContact().getHomeAdress());

	    if (subjectLocal.getOrigin().getCity() != null)
		driver.getResidenceData().setCity(subjectLocal.getOrigin().getCity());

	    if (subjectLocal.getIdentityCard() != null) {
		driver.getIdentityCardData().setNumber(subjectLocal.getIdentityCard().getNumber());
		driver.getIdentityCardData()
			.setDateOfIssue(TemporalUtils.toLocalDate(subjectLocal.getIdentityCard().getDateOfIssue()));
		driver.getIdentityCardData().setType(subjectLocal.getIdentityCard().getIdentityCardType());
		driver.getIdentityCardData()
			.setIssuingAuthority(subjectLocal.getIdentityCard().getIssuingAuthority());
	    }

	    if (subjectLocal.getContact() != null) {
		driver.getContactData().setEmail(subjectLocal.getContact().getEmail());
		driver.getContactData().setPhone(subjectLocal.getContact().getPhone());
		driver.getContactData().setSiteUrl(subjectLocal.getContact().getSiteUrl());
	    }

	    driver.setTaxPayerNumber(subjectLocal.getTaxPayerNumber());
	}

	return driver;
    }

    public void fetch(PolicyDriver driver) {
	clearFetched(driver);
	PolicyDriver fetched = fetchByIdNumber(driver.getIdNumber());

	driver.setFetched(fetched.isFetched());

	driver.setInsuranceClassType(fetched.getInsuranceClassType());
	driver.setAgeClass(fetched.getAgeClass());

	driver.setPersonalData(fetched.getPersonalData());
	driver.setResidenceData(fetched.getResidenceData());
	driver.setOriginData(fetched.getOriginData());
	driver.setIdentityCardData(fetched.getIdentityCardData());
	driver.setTaxPayerNumber(fetched.getTaxPayerNumber());
	driver.setContactData(fetched.getContactData());
    }

    public void clearFetched(PolicyDriver driver) {
	driver.setFetched(false);

	driver.setInsuranceClassType(getDefaultInsuranceClass());
	driver.setAgeClass(null);

	driver.setPersonalData(new PersonalData());
	driver.setResidenceData(new ResidenceData());
	driver.setOriginData(new OriginData());
	driver.setIdentityCardData(new IdentityCardData());
	driver.setTaxPayerNumber(null);
	driver.setContactData(new ContactData());
    }

    // PRIVATE STATIC

    private static Sex convertKZLibSex(com.lapsa.kz.idnumber.IDNumberUtils.Sex kzLibSex) {
	if (kzLibSex == null)
	    return null;
	switch (kzLibSex) {
	case FEMALE:
	    return Sex.FEMALE;
	case MALE:
	    return Sex.MALE;
	}
	return null;
    }

    private static InsuredAgeClass obtainInsuredAgeClass(LocalDate dayOfBirth) {
	if (dayOfBirth == null)
	    return null;
	int years = calculateAgeByDOB(dayOfBirth);
	return _obtainInsuredAgeClass(years);
    }

    private static int calculateAgeByDOB(LocalDate dob) {
	if (dob == null)
	    throw new NullPointerException();
	return dob.until(LocalDate.now()).getYears();
    }

    private static InsuredAgeClass _obtainInsuredAgeClass(int years) {
	return years < 25 ? InsuredAgeClass.UNDER25 : InsuredAgeClass.OVER25;
    }

}
