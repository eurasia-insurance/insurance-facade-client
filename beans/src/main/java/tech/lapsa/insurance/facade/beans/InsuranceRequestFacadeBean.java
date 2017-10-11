package tech.lapsa.insurance.facade.beans;

import java.time.Instant;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.lapsa.insurance.dao.EntityNotFound;
import com.lapsa.insurance.dao.InsuranceRequestDAO;
import com.lapsa.insurance.domain.CalculationData;
import com.lapsa.insurance.domain.InsuranceProduct;
import com.lapsa.insurance.domain.InsuranceRequest;
import com.lapsa.insurance.domain.RequesterData;
import com.lapsa.insurance.elements.PaymentMethod;
import com.lapsa.insurance.elements.PaymentStatus;
import com.lapsa.insurance.mesenger.NotificationChannel;
import com.lapsa.insurance.mesenger.NotificationRecipientType;
import com.lapsa.insurance.mesenger.NotificationRequestStage;
import com.lapsa.insurance.mesenger.Notifier;
import com.lapsa.international.localization.LocalizationLanguage;

import tech.lapsa.epayment.facade.EpaymentFacade;
import tech.lapsa.epayment.facade.EpaymentFacade.EbillAcceptorBuilder;
import tech.lapsa.insurance.facade.InsuranceRequestFacade;
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
	try {
	    InsuranceRequest request = dao.findById(id);
	    request.getPayment().setStatus(PaymentStatus.DONE);
	    request.getPayment().setPostReference(paymentReference);
	    request.getPayment().setPostInstant(paymentInstant);
	    request = dao.save(request);
	} catch (EntityNotFound e) {
	    throw new IllegalArgumentException("Request not found", e);
	}
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
		    .map(x -> x.displayName(consumerLanguage.getLocale())) //
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
	switch (request.getType()) {
	case ONLINE:
	case EXPRESS:
	    notifier.assignRequestNotification(NotificationChannel.EMAIL, NotificationRecipientType.COMPANY,
		    NotificationRequestStage.NEW_REQUEST, request);
	    if (request.getRequester().getEmail() != null)
		notifier.assignRequestNotification(NotificationChannel.EMAIL, NotificationRecipientType.REQUESTER,
			NotificationRequestStage.NEW_REQUEST, request);
	case UNCOMPLETE:
	    notifier.assignRequestNotification(NotificationChannel.PUSH, NotificationRecipientType.COMPANY,
		    NotificationRequestStage.NEW_REQUEST, request);
	}
	return request;
    }

    @Inject
    private InsuranceRequestDAO dao;

    private <T extends InsuranceRequest> T persistRequest(final T request) {
	return dao.save(request);
    }

    @Inject
    private Logger logger;

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
