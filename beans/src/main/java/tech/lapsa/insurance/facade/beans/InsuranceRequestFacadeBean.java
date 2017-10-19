package tech.lapsa.insurance.facade.beans;

import java.time.Instant;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.lapsa.insurance.domain.CalculationData;
import com.lapsa.insurance.domain.InsuranceProduct;
import com.lapsa.insurance.domain.InsuranceRequest;
import com.lapsa.insurance.domain.RequesterData;
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
import tech.lapsa.java.commons.function.MyOptionals;
import tech.lapsa.java.commons.function.MyStrings;

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
		.withEvent(NotificationRequestStage.NEW_REQUEST);

	switch (request.getType()) {
	case ONLINE:
	case EXPRESS:
	    builder.withChannel(NotificationChannel.EMAIL) //
		    .withRecipient(NotificationRecipientType.COMPANY) //
		    .forEntity(request) //
		    .build() //
		    .send();
	    if (request.getRequester().getEmail() != null)
		builder.withChannel(NotificationChannel.EMAIL) //
			.withRecipient(NotificationRecipientType.REQUESTER) //
			.forEntity(request) //
			.build() //
			.send();
	case UNCOMPLETE:
	    builder.withChannel(NotificationChannel.PUSH) //
		    .withRecipient(NotificationRecipientType.COMPANY) //
		    .forEntity(request) //
		    .build() //
		    .send();
	}
	return request;
    }

    @Inject
    private InsuranceRequestDAO dao;

    private <T extends InsuranceRequest> T persistRequest(final T request) {
	return dao.save(request);
    }

    private final Logger logger = Logger.getLogger(InsuranceRequestFacade.class.getPackage().getName());

    private <T extends InsuranceRequest> T logInsuranceRequestAccepted(T request) {
	logger.info(String.format("New %4$s accepded from '%1$s' '<%2$s>' tel '%3$s' ", //
		request.getRequester().getName(), // 1
		request.getRequester().getEmail(), // 2
		request.getRequester().getPhone(), // 3
		request.getClass().getSimpleName() // 4
	));
	return request;
    }

}
