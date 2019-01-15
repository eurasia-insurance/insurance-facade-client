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

    <T extends InsuranceRequest> T requestReceived(T insuranceRequest) throws IllegalArgument;

    <T extends InsuranceRequest> T policyIssued(T insuranceRequest,
	    String agreementNumber) throws IllegalArgument, IllegalState;

    /**
     * Alternative completion of request
     * @deprecated to be removed when query below will return empty result set
     * 
     * <pre>
     * select r.ID, 
     *        r.PROGRESS_STATUS, 
     *       ir.PAYMENT_STATUS, 
     *       ir.AGREEMENT_NUMBER 
     * FROM REQUEST r, 
     *      INSURANCE_REQUEST ir
     * WHERE ir.ID = r.ID 
     *   AND ir.INSURANCE_REQUEST_STATUS = 'PREMIUM_PAID' 
     *   AND r.PROGRESS_STATUS <> 'FINISHED';
     * </pre>
     */
    @Deprecated
    <T extends InsuranceRequest> T policyIssuedAlt(T insuranceRequest,
	    String agreementNumber,
	    User completedBy)
	    throws IllegalState, IllegalArgument;

    <T extends InsuranceRequest> T policyIssuedAndInvoiceCreated(T insuranceRequest,
	    String agreementNumber,
	    String invoicePayeeName,
	    Currency invoiceCurrency,
	    LocalizationLanguage invoiceLanguage,
	    String invoicePayeeEmail,
	    PhoneNumber invoicePayeePhone,
	    TaxpayerNumber invoicePayeeTaxpayerNumber,
	    String invoiceProductName,
	    Double invoiceAmount,
	    Integer invoiceQuantity) throws IllegalArgument, IllegalState;

    <T extends InsuranceRequest> T policyIssuedAndPremiumPaid(T insuranceRequest,
	    User completedBy,
	    String agreementNumber,
	    String paymentMethodName,
	    Double paymentAmount,
	    Currency paymentCurrency,
	    Instant paymentInstant,
	    String paymentCard,
	    String paymentCardBank,
	    String paymentReference,
	    String payerName) throws IllegalState, IllegalArgument;

    <T extends InsuranceRequest> T invoiceCreated(T insuranceRequest,
	    String invoicePayeeName,
	    Currency invoiceCurrency,
	    LocalizationLanguage invoiceLanguage,
	    String invoicePayeeEmail,
	    PhoneNumber invoicePayeePhone,
	    TaxpayerNumber invoicePayeeTaxpayerNumber,
	    String invoiceProductName,
	    Double invoiceAmount,
	    Integer invoiceQuantity) throws IllegalArgument, IllegalState;

    <T extends InsuranceRequest> T premiumPaid(T insuranceRequest,
	    String paymentMethodName,
	    Instant paymentInstant,
	    Double paymentAmount,
	    Currency paymentCurrency,
	    String paymentCard,
	    String paymentCardBank,
	    String paymentReference,
	    String payerName,
	    User completedBy) throws IllegalArgument, IllegalState;

    void premiumPaid(Integer id,
	    String paymentMethodName,
	    Instant paymentInstant,
	    Double paymentAmount,
	    Currency paymentCurrency,
	    String paymentCard,
	    String paymentCardBank,
	    String paymentReference,
	    String payerName) throws IllegalArgument, IllegalState;


    <T extends InsuranceRequest> T requestCanceled(T insuranceRequest,
	    User completedBy,
	    InsuranceRequestCancellationReason insuranceRequestCancellationReason) throws IllegalState, IllegalArgument;
}