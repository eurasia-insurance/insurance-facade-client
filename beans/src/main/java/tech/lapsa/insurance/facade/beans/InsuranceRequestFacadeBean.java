package tech.lapsa.insurance.facade.beans;

import java.time.Instant;
import java.util.Currency;
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.lapsa.fin.FinCurrency;
import com.lapsa.insurance.domain.CalculationData;
import com.lapsa.insurance.domain.InsuranceProduct;
import com.lapsa.insurance.domain.InsuranceRequest;
import com.lapsa.insurance.domain.RequesterData;
import com.lapsa.insurance.elements.PaymentStatus;
import com.lapsa.international.localization.LocalizationLanguage;

import tech.lapsa.epayment.domain.Invoice;
import tech.lapsa.epayment.domain.Invoice.InvoiceBuilder;
import tech.lapsa.epayment.facade.EpaymentFacade.EpaymentFacadeRemote;
import tech.lapsa.insurance.dao.InsuranceRequestDAO.InsuranceRequestDAORemote;
import tech.lapsa.insurance.facade.InsuranceRequestFacade;
import tech.lapsa.insurance.facade.InsuranceRequestFacade.InsuranceRequestFacadeLocal;
import tech.lapsa.insurance.facade.InsuranceRequestFacade.InsuranceRequestFacadeRemote;
import tech.lapsa.insurance.facade.NotificationFacade.Notification;
import tech.lapsa.insurance.facade.NotificationFacade.Notification.NotificationBuilder;
import tech.lapsa.insurance.facade.NotificationFacade.Notification.NotificationChannel;
import tech.lapsa.insurance.facade.NotificationFacade.Notification.NotificationEventType;
import tech.lapsa.insurance.facade.NotificationFacade.Notification.NotificationRecipientType;
import tech.lapsa.insurance.facade.NotificationFacade.NotificationFacadeLocal;
import tech.lapsa.java.commons.exceptions.IllegalArgument;
import tech.lapsa.java.commons.exceptions.IllegalState;
import tech.lapsa.java.commons.function.MyExceptions;
import tech.lapsa.java.commons.function.MyNumbers;
import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.java.commons.function.MyOptionals;
import tech.lapsa.java.commons.function.MyStrings;
import tech.lapsa.java.commons.logging.MyLogger;
import tech.lapsa.patterns.dao.NotFound;

@Stateless(name = InsuranceRequestFacade.BEAN_NAME)
public class InsuranceRequestFacadeBean implements InsuranceRequestFacadeLocal, InsuranceRequestFacadeRemote {

    // READERS

    // MODIFIERS

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <Y extends InsuranceRequest> Y acceptAndReply(final Y request) throws IllegalArgument {
	try {
	    return _acceptAndReply(request);
	} catch (final IllegalArgumentException e) {
	    throw new IllegalArgument(e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void completePayment(final Integer id, final String methodName, final Instant paymentInstant,
	    final Double paymentAmount, final Currency paymentCurrency, final String paymentReference)
	    throws IllegalArgument, IllegalState {
	try {
	    _completePayment(id, methodName, paymentInstant, paymentAmount, paymentReference);
	} catch (final IllegalStateException e) {
	    throw new IllegalState(e);
	} catch (final IllegalArgumentException e) {
	    throw new IllegalArgument(e);
	}
    }

    // PRIVATE

    @EJB
    private NotificationFacadeLocal notifications;

    @EJB
    private InsuranceRequestDAORemote dao;

    private <Y extends InsuranceRequest> Y _acceptAndReply(final Y insuranceRequest) throws IllegalArgumentException {

	MyObjects.requireNonNull(insuranceRequest, "insuranceRequest");

	Requests.preSave(insuranceRequest);

	final Y ir;
	try {
	    ir = dao.save(insuranceRequest);
	} catch (final IllegalArgument e) {
	    // it should not happens
	    throw new EJBException(e.getMessage());
	}

	setupPaymentOrder(ir);
	setupNotifications(ir);

	logger.INFO.log("New %4$s accepded from '%1$s' '<%2$s>' tel '%3$s' ", //
		ir.getRequester().getName(), // 1
		ir.getRequester().getEmail(), // 2
		ir.getRequester().getPhone(), // 3
		ir.getClass().getSimpleName() // 4
	);

	return ir;
    }

    private void _completePayment(final Integer id, final String methodName, final Instant paymentInstant,
	    final Double paymentAmount, final String paymentReference)
	    throws IllegalArgumentException, IllegalStateException {

	// TODO FEAUTURE : check parameter for requirements

	InsuranceRequest request;
	try {
	    request = dao.getById(id);
	} catch (final NotFound e) {
	    throw MyExceptions.format(IllegalArgumentException::new, "Request not found with id %1$s", id);
	} catch (final IllegalArgument e) {
	    // it should not happens
	    throw new EJBException(e.getMessage());
	}

	try {
	    if (request.getPayment().getStatus() == PaymentStatus.DONE)
		throw MyExceptions.illegalStateFormat("Request %1$s already paid on %2$s with reference %3$s",
			request.getId(),
			request.getPayment().getPaymentInstant(),
			request.getPayment().getPaymentReference());

	    request.getPayment().setStatus(PaymentStatus.DONE);
	    request.getPayment().setMethodName(methodName);
	    request.getPayment().setPaymentAmount(paymentAmount);
	    request.getPayment().setPaymentReference(paymentReference);
	    request.getPayment().setPaymentInstant(paymentInstant);
	    // TODO FEAUTURE : Save paymentCurrency or not?
	} catch (final NullPointerException e) {
	    // it should not happens
	    throw new EJBException(e.getMessage());
	}

	try {
	    request = dao.save(request);
	} catch (final IllegalArgument e) {
	    // it should not happens
	    throw new EJBException(e.getMessage());
	}

	request.unlazy();

	try {
	    notifications.send(Notification.builder() //
		    .withEvent(NotificationEventType.REQUEST_PAID) //
		    .withChannel(NotificationChannel.EMAIL) //
		    .forEntity(request) //
		    .withRecipient(NotificationRecipientType.COMPANY) //
		    .build());
	} catch (final IllegalArgument e) {
	    // it should not happen
	    throw new EJBException(e.getMessage());
	}
    }

    @EJB
    private EpaymentFacadeRemote epayments;

    private <T extends InsuranceRequest> T setupPaymentOrder(final T request) throws IllegalArgumentException {
	// TODO FEAUTURE : check parameter for requirements

	if (MyStrings.nonEmpty(request.getPayment().getInvoiceNumber()))
	    return request;

	final Optional<RequesterData> ord = MyOptionals.of(request.getRequester());

	final Optional<CalculationData> ocd = MyOptionals.of(request.getProduct()) //
		.map(InsuranceProduct::getCalculation);

	final LocalizationLanguage lang = ord.map(RequesterData::getPreferLanguage) //
		.orElseThrow(MyExceptions.illegalArgumentSupplier("Can't determine the language"));

	final String itemName = MyOptionals.of(request.getProductType()) //
		.map(x -> x.regular(lang.getLocale())) //
		.orElseThrow(MyExceptions.illegalArgumentSupplier("Can't determine an item name"));

	final Integer quantity = 1;

	final Double cost = ocd.map(CalculationData::getPremiumCost) //
		.filter(MyNumbers::nonZero) //
		.orElseThrow(MyExceptions.illegalArgumentSupplier("Can't determine an premium amount"));

	final String consumerName = ord.map(RequesterData::getName) //
		.orElseThrow(MyExceptions.illegalArgumentSupplier("Can't determine a consumer name"));

	final Currency currency = ocd.map(CalculationData::getPremiumCurrency) //
		.map(FinCurrency::getCurrency)
		.orElseThrow(MyExceptions.illegalArgumentSupplier("Can't determine an premium currency"));

	final InvoiceBuilder builder = Invoice.builder() //
		.withGeneratedNumber() //
		.withConsumerName(consumerName) //
		.withCurrency(currency) //
		.withConsumerPreferLanguage(lang)
		//
		.withExternalId(request.getId()) //
		.withConsumerEmail(ord.map(RequesterData::getEmail)) //
		.withConsumerPhone(ord.map(RequesterData::getPhone)) //
		.withConsumerTaxpayerNumber(ord.map(RequesterData::getIdNumber)) //
		.withItem(itemName, quantity, cost);

	final Invoice invoice;
	try {
	    invoice = epayments.invoiceAccept(builder);
	} catch (IllegalArgument e) {
	    // it should not happens
	    throw new EJBException(e.getMessage());
	}
	
	request.getPayment().setInvoiceNumber(invoice.getNumber());
	request.getPayment().setStatus(PaymentStatus.PENDING);

	return request;
    }

    private <T extends InsuranceRequest> T setupNotifications(final T request) throws IllegalArgumentException {
	// TODO FEAUTURE : check parameter for requirements
	final NotificationBuilder builder = Notification.builder() //
		.withEvent(NotificationEventType.NEW_REQUEST) //
		.forEntity(request);

	switch (request.getType()) {
	case ONLINE:
	case EXPRESS:
	    try {
		notifications.send(builder.withChannel(NotificationChannel.EMAIL) //
			.withRecipient(NotificationRecipientType.COMPANY) //
			.build());
	    } catch (final IllegalArgument e) {
		// it should not happen
		throw new EJBException(e.getMessage());
	    }
	    if (request.getRequester().getEmail() != null)
		try {
		    notifications.send(builder.withChannel(NotificationChannel.EMAIL) //
			    .withRecipient(NotificationRecipientType.REQUESTER) //
			    .build());
		} catch (final IllegalArgument e) {
		    // it should not happen
		    throw new EJBException(e.getMessage());
		}
	case UNCOMPLETE:
	    // TODO DEBUG : Push disabled temporary. Need to debug
	    // builder.withChannel(NotificationChannel.PUSH) //
	    // .withRecipient(NotificationRecipientType.COMPANY) //
	    // .build() //
	    // .send();
	}
	return request;
    }

    private final MyLogger logger = MyLogger.newBuilder() //
	    .withNameOf(InsuranceRequestFacade.class) //
	    .build();

}
