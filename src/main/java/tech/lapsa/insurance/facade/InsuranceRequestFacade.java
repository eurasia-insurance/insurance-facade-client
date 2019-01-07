package tech.lapsa.insurance.facade;

import java.time.Instant;
import java.util.Currency;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.lapsa.insurance.domain.InsuranceRequest;
import com.lapsa.insurance.domain.crm.User;
import com.lapsa.insurance.elements.InsuranceRequestCancellationReason;
import com.lapsa.international.localization.LocalizationLanguage;
import com.lapsa.international.phone.PhoneNumber;

import tech.lapsa.java.commons.exceptions.IllegalArgument;
import tech.lapsa.java.commons.exceptions.IllegalState;
import tech.lapsa.kz.taxpayer.TaxpayerNumber;

public interface InsuranceRequestFacade extends EJBConstants {

    public static final String BEAN_NAME = "InsuranceRequestFacadeBean";

    @Local
    public interface InsuranceRequestFacadeLocal extends InsuranceRequestFacade {
    }

    @Remote
    public interface InsuranceRequestFacadeRemote extends InsuranceRequestFacade {
    }

    <T extends InsuranceRequest> T getById(Integer id) throws IllegalState, IllegalArgument;

    <T extends InsuranceRequest> T newRequest(T request) throws IllegalArgument;

    <T extends InsuranceRequest> T newAcceptedRequest(T request) throws IllegalArgument;

    <T extends InsuranceRequest> T acceptRequest(T request,
	    String invoicePayeeName,
	    Currency invoiceCurrency,
	    LocalizationLanguage invoiceLanguage,
	    String invoicePayeeEmail,
	    PhoneNumber invoicePayeePhone,
	    TaxpayerNumber invoicePayeeTaxpayerNumber,
	    String invoiceProductName,
	    Double invoiceAmount,
	    Integer invoiceQuantity) throws IllegalArgument;

    <T extends InsuranceRequest> T premiumPaid(T request,
	    String paymentMethodName,
	    Instant paymentInstant,
	    Double paymentAmount,
	    Currency paymentCurrency,
	    String paymentCard,
	    String paymentCardBank,
	    String paymentReference,
	    String payerName) throws IllegalArgument;

    <T extends InsuranceRequest> T policyIssued(T request, User user, String agreementNumber)
	    throws IllegalState, IllegalArgument;

    <T extends InsuranceRequest> T policyIssuedAndPremiumPaid(T request,
	    User user,
	    String agreementNumber,
	    String paymentMethodName,
	    Double paymentAmount,
	    Currency paymentCurrency,
	    Instant paymentInstant,
	    String paymentReference,
	    String payerName) throws IllegalState, IllegalArgument;

    <T extends InsuranceRequest> T requestCanceled(T request,
	    User user,
	    InsuranceRequestCancellationReason insuranceRequestCancellationReason) throws IllegalState, IllegalArgument;
}