package tech.lapsa.insurance.facade.beans;

import static tech.lapsa.java.commons.function.MyExceptions.*;

import java.time.Instant;
import java.util.Currency;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.lapsa.fin.FinCurrency;
import com.lapsa.insurance.domain.CalculationData;
import com.lapsa.insurance.domain.InsuranceProduct;
import com.lapsa.insurance.domain.InsuranceRequest;
import com.lapsa.insurance.domain.RequesterData;
import com.lapsa.insurance.elements.PaymentStatus;

import tech.lapsa.epayment.shared.entity.XmlInvoiceAcceptRequest;
import tech.lapsa.epayment.shared.entity.XmlInvoiceAcceptResponse;
import tech.lapsa.epayment.shared.entity.XmlInvoicePurposeItem;
import tech.lapsa.epayment.shared.jms.EpaymentDestinations;
import tech.lapsa.insurance.dao.InsuranceRequestDAO;
import tech.lapsa.insurance.facade.InsuranceRequestFacade;
import tech.lapsa.insurance.notifier.Notification;
import tech.lapsa.insurance.notifier.Notification.NotificationBuilder;
import tech.lapsa.insurance.notifier.NotificationChannel;
import tech.lapsa.insurance.notifier.NotificationRecipientType;
import tech.lapsa.insurance.notifier.NotificationRequestStage;
import tech.lapsa.insurance.notifier.Notifier;
import tech.lapsa.java.commons.function.MyExceptions;
import tech.lapsa.java.commons.function.MyExceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions.IllegalState;
import tech.lapsa.java.commons.function.MyNumbers;
import tech.lapsa.java.commons.function.MyOptionals;
import tech.lapsa.java.commons.function.MyStrings;
import tech.lapsa.java.commons.logging.MyLogger;
import tech.lapsa.javax.jms.client.JmsCallableClient;
import tech.lapsa.javax.jms.client.JmsDestination;
import tech.lapsa.javax.jms.client.JmsResultType;

@Stateless
public class InsuranceRequestFacadeBean implements InsuranceRequestFacade {

    // READERS

    // MODIFIERS

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <T extends InsuranceRequest> T acceptAndReply(final T request) throws IllegalArgument, IllegalState {
	return reThrowAsChecked(() -> _acceptAndReply(request));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void completePayment(final Integer id, final String methodName, final Instant paymentInstant,
	    final Double paymentAmount, final Currency paymentCurrency, final String paymentReference)
	    throws IllegalArgument, IllegalState {
	reThrowAsChecked(() -> _completePayment(id, methodName, paymentInstant, paymentAmount, paymentReference));
    }

    // PRIVATE

    private <T extends InsuranceRequest> T _acceptAndReply(final T request) {
	Requests.preSave(request);
	final T saved = persistRequest(request);
	setupPaymentOrder(saved);
	setupNotifications(saved);
	logInsuranceRequestAccepted(saved);
	return saved;
    }

    private void _completePayment(final Integer id, final String methodName, final Instant paymentInstant,
	    final Double paymentAmount, final String paymentReference) {
	InsuranceRequest request = dao.optionalById(id)
		.orElseThrow(() -> new IllegalArgumentException("Request not found with id " + id));
	request.getPayment().setStatus(PaymentStatus.DONE);
	request.getPayment().setMethodName(methodName);
	request.getPayment().setPaymentAmount(paymentAmount);
	request.getPayment().setPaymentReference(paymentReference);
	request.getPayment().setPaymentInstant(paymentInstant);
	// TODO FEAUTURE : Save paymentCurrency or not?
	request = dao.save(request);

	request.unlazy();

	notifier.send(Notification.builder() //
		.withEvent(NotificationRequestStage.REQUEST_PAID) //
		.withChannel(NotificationChannel.EMAIL) //
		.forEntity(request) //
		.withRecipient(NotificationRecipientType.COMPANY) //
		.build() //
	);
    }

    @Inject
    @JmsDestination(EpaymentDestinations.ACCEPT_INVOICE)
    @JmsResultType(XmlInvoiceAcceptResponse.class)
    private JmsCallableClient<XmlInvoiceAcceptRequest, XmlInvoiceAcceptResponse> invoiceAcceptorCallableClient;

    private <T extends InsuranceRequest> T setupPaymentOrder(final T request) {

	if (MyStrings.nonEmpty(request.getPayment().getInvoiceNumber()))
	    return request;

	final XmlInvoiceAcceptRequest r = new XmlInvoiceAcceptRequest();

	final Optional<RequesterData> ord = MyOptionals.of(request.getRequester());

	final Optional<CalculationData> ocd = MyOptionals.of(request.getProduct()) //
		.map(InsuranceProduct::getCalculation);

	r.setLanguage(ord.map(RequesterData::getPreferLanguage) //
		.orElseThrow(MyExceptions.illegalStateSupplierFormat("Can't determine the language")));

	r.setCurrency(ocd.map(CalculationData::getPremiumCurrency) //
		.map(FinCurrency::getCurrency)
		.orElseThrow(MyExceptions.illegalStateSupplierFormat("Can't determine an premium currency")));

	r.setExternalId(request.getId());
	r.setName(ord.map(RequesterData::getName) //
		.orElseThrow(MyExceptions.illegalStateSupplierFormat("Can't determine a consumer name")));
	ord.map(RequesterData::getPhone) //
		.ifPresent(r::setPhoneNumber);

	ord.map(RequesterData::getEmail) //
		.ifPresent(r::setEmail);

	ord.map(RequesterData::getIdNumber) //
		.ifPresent(r::setTaxpayerNumber);

	final XmlInvoicePurposeItem p = new XmlInvoicePurposeItem(MyOptionals.of(request.getProductType()) //
		.map(x -> x.regular(r.getLanguage().getLocale())) //
		.orElseThrow(MyExceptions.illegalStateSupplierFormat("Can't determine an item name")),
		ocd.map(CalculationData::getPremiumCost) //
			.filter(MyNumbers::nonZero) //
			.orElseThrow(MyExceptions.illegalStateSupplierFormat("Can't determine an premium amount")),
		1);

	r.setItem(p);

	final XmlInvoiceAcceptResponse resp = invoiceAcceptorCallableClient.call(r);

	request.getPayment().setInvoiceNumber(resp.getInvoiceNumber());

	return request;
    }

    @Inject
    private Notifier notifier;

    private <T extends InsuranceRequest> T setupNotifications(final T request) {
	final NotificationBuilder builder = Notification.builder() //
		.withEvent(NotificationRequestStage.NEW_REQUEST) //
		.forEntity(request);

	switch (request.getType()) {
	case ONLINE:
	case EXPRESS:
	    notifier.send(builder.withChannel(NotificationChannel.EMAIL) //
		    .withRecipient(NotificationRecipientType.COMPANY) //
		    .build() //
	    );
	    if (request.getRequester().getEmail() != null)
		notifier.send(builder.withChannel(NotificationChannel.EMAIL) //
			.withRecipient(NotificationRecipientType.REQUESTER) //
			.build() //
		);
	case UNCOMPLETE:
	    // TODO DEBUG : Push disabled temporary. Need to debug
	    // builder.withChannel(NotificationChannel.PUSH) //
	    // .withRecipient(NotificationRecipientType.COMPANY) //
	    // .build() //
	    // .send();
	}
	return request;
    }

    @Inject
    private InsuranceRequestDAO dao;

    private <T extends InsuranceRequest> T persistRequest(final T request) {
	return dao.save(request);
    }

    private final MyLogger logger = MyLogger.newBuilder() //
	    .withNameOf(InsuranceRequestFacade.class) //
	    .build();

    private <T extends InsuranceRequest> T logInsuranceRequestAccepted(final T request) {
	logger.INFO.log("New %4$s accepded from '%1$s' '<%2$s>' tel '%3$s' ", //
		request.getRequester().getName(), // 1
		request.getRequester().getEmail(), // 2
		request.getRequester().getPhone(), // 3
		request.getClass().getSimpleName() // 4
	);
	return request;
    }

}
