package tech.lapsa.insurance.facade.beans;

import java.time.Instant;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.lapsa.insurance.domain.CalculationData;
import com.lapsa.insurance.domain.InsuranceProduct;
import com.lapsa.insurance.domain.InsuranceRequest;
import com.lapsa.insurance.domain.RequesterData;
import com.lapsa.insurance.domain.policy.PolicyRequest;
import com.lapsa.insurance.elements.PaymentMethod;
import com.lapsa.insurance.elements.PaymentStatus;
import com.lapsa.international.localization.LocalizationLanguage;

import tech.lapsa.epayment.facade.EpaymentFacade;
import tech.lapsa.epayment.facade.EpaymentFacade.EbillAcceptorBuilder;
import tech.lapsa.insurance.dao.InsuranceRequestDAO;
import tech.lapsa.insurance.facade.InsuranceRequestFacade;
import tech.lapsa.insurance.notifier.NotificationChannel;
import tech.lapsa.insurance.notifier.NotificationRecipientType;
import tech.lapsa.insurance.notifier.NotificationRequestStage;
import tech.lapsa.insurance.notifier.Notifier;
import tech.lapsa.insurance.notifier.Notifier.NotificationBuilder;
import tech.lapsa.java.commons.function.MyNumbers;
import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.java.commons.function.MyOptionals;
import tech.lapsa.java.commons.function.MyStrings;
import tech.lapsa.java.commons.logging.MyLogger;

@Stateless
public class InsuranceRequestFacadeBean implements InsuranceRequestFacade {

    @Override
    public <T extends InsuranceRequest> T acceptAndReply(T request) {
	Requests.preSave(request);
	T saved = persistRequest(request);
	setupPaymentOrder(saved);
	setupNotifications(saved);
	logInsuranceRequestAccepted(saved);
	return saved;
    }

    @Override
    public void markPaymentComplete(Integer id, String paymentReference, Instant paymentInstant) {
	InsuranceRequest request = dao.optionalById(id)
		.orElseThrow(() -> new IllegalArgumentException("Request not found with id " + id));
	request.getPayment().setStatus(PaymentStatus.DONE);
	request.getPayment().setPostReference(paymentReference);
	request.getPayment().setPostInstant(paymentInstant);
	request = dao.save(request);

	// TODO This is requred to prevent ValidationExpression
	// EclipseLink-7242 Exception Description: An attempt was made to
	// traverse a relationship using indirection that had a null Session.
	// This often occurs when an entity with an uninstantiated LAZY
	// relationship is serialized and that relationship is traversed after
	// serialization. To avoid this issue, instantiate the LAZY relationship
	// prior to serialization.
	request.getAcceptedBy();
	if (MyObjects.isA(request, PolicyRequest.class)) {
	    ((PolicyRequest) request).getPolicy();
	}

	notifier.newNotificationBuilder() //
		.withEvent(NotificationRequestStage.REQUEST_PAID) //
		.withChannel(NotificationChannel.EMAIL) //
		.forEntity(request) //
		.withRecipient(NotificationRecipientType.COMPANY) //
		.build()
		.send();
    }

    // PRIVATE

    @Inject
    private EpaymentFacade epaymentFacade;

    private <T extends InsuranceRequest> T setupPaymentOrder(T request) {
	if (request.getPayment() != null //
		&& MyStrings.empty(request.getPayment().getExternalId()) //
		&& PaymentMethod.PAYCARD_ONLINE.equals(request.getPayment().getMethod())) {

	    LocalizationLanguage consumerLanguage = MyOptionals.of(request.getRequester()) //
		    .map(RequesterData::getPreferLanguage) //
		    .orElseThrow(() -> new IllegalArgumentException("Can't determine the language"));

	    String consumerEmail = MyOptionals.of(request.getRequester()) //
		    .map(RequesterData::getEmail) //
		    .orElseThrow(() -> new IllegalArgumentException("Can't determine a consumer email"));

	    String consumerName = MyOptionals.of(request.getRequester()) //
		    .map(RequesterData::getName) //
		    .orElseThrow(() -> new IllegalArgumentException("Can't determine a consumer name"));

	    EbillAcceptorBuilder builder = epaymentFacade.newEbillAcceptorBuilder() //
		    .winthGeneratedId() //
		    .withDefaultCurrency() //
		    .withConsumerLanguage(consumerLanguage) //
		    .withConsumerEmail(consumerEmail) //
		    .withConsumerName(consumerName) //
		    .withExternalId(request.getId());

	    String itemName = MyOptionals.of(request.getProductType()) //
		    .map(x -> x.regular(consumerLanguage.getLocale())) //
		    .orElseThrow(() -> new IllegalArgumentException("Can't determine an item name"));

	    double cost = MyOptionals.of(request.getProduct()) //
		    .map(InsuranceProduct::getCalculation) //
		    .map(CalculationData::getPremiumCost) //
		    .filter(MyNumbers::nonZero) //
		    .orElseThrow(() -> new IllegalArgumentException("Can't determine an item cost")) //
		    .doubleValue();

	    String ebillId = builder.withMoreItem(itemName, cost, 1) //
		    .build() //
		    .accept() //
		    .getId();

	    request.getPayment() //
		    .setExternalId(ebillId);
	}
	return request;
    }

    @Inject
    private Notifier notifier;

    private <T extends InsuranceRequest> T setupNotifications(T request) {
	NotificationBuilder builder = notifier.newNotificationBuilder() //
		.withEvent(NotificationRequestStage.NEW_REQUEST) //
		.forEntity(request);

	switch (request.getType()) {
	case ONLINE:
	case EXPRESS:
	    builder.withChannel(NotificationChannel.EMAIL) //
		    .withRecipient(NotificationRecipientType.COMPANY) //
		    .build() //
		    .send();
	    if (request.getRequester().getEmail() != null)
		builder.withChannel(NotificationChannel.EMAIL) //
			.withRecipient(NotificationRecipientType.REQUESTER) //
			.build() //
			.send();
	case UNCOMPLETE:
	    // TODO PUSH DISABLED temporary
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

    private <T extends InsuranceRequest> T logInsuranceRequestAccepted(T request) {
	logger.INFO.log("New %4$s accepded from '%1$s' '<%2$s>' tel '%3$s' ", //
		request.getRequester().getName(), // 1
		request.getRequester().getEmail(), // 2
		request.getRequester().getPhone(), // 3
		request.getClass().getSimpleName() // 4
	);
	return request;
    }

}
